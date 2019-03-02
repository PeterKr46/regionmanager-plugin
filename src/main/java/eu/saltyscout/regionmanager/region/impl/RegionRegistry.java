package eu.saltyscout.regionmanager.region.impl;

import eu.saltyscout.regionmanager.RegionManagerPlugin;
import eu.saltyscout.regionmanager.event.*;
import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.flag.type.BooleanFlag;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionType;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import eu.saltyscout.utils.PlayerNotification;
import eu.saltyscout.utils.PriorityQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Peter on 05-Apr-17.
 */
public class RegionRegistry extends eu.saltyscout.regionmanager.region.RegionRegistry {
    
    private File configFile;
    private YamlConfiguration config;
    private HashMap<String, HashMap<String, Region>> regions = new HashMap<>();
    
    protected void _initialize(File dataFolder) throws Exception {
        configFile = new File(dataFolder, "regions.yml");
        reload();
    }
    
    protected void _reregister(Region region, String newName) {
        Bukkit.getPluginManager().callEvent(new RegionRenameEvent(region, newName));
        _destroyRegion(region.getName());
        HashMap<String, Region> regions = _getWorldRegions(region.getWorld().getName());
        regions.put(newName.toLowerCase(), region);
    }

    @Override
    protected <T> T _getFlag(@Nonnull String flag, @Nonnull PriorityQueue queue, @Nonnull Class<? extends Flag<T>> clazz, T defValue) {
        T value = defValue;
        for(Region region : queue) {
            if(region.hasFlag(flag)) {
                value = region.getFlag(flag, clazz, value);
            }
        }
        return value;
    }

    @Override
    protected <T> T _getFlag(@Nonnull String flag, @Nonnull PriorityQueue queue, @Nonnull Class<? extends Flag<T>> clazz) {
        T value = null;
        for(Region region : queue) {
            if(region.hasFlag(flag)) {
                value = region.getFlag(flag, clazz, value);
            }
        }
        return value;
    }

    private HashMap<String, Region> _getWorldRegions(String world) {
        world = world.toLowerCase();
        return regions.computeIfAbsent(world, k -> new HashMap<>());
    }
    
    protected boolean _check(Player player, Location location, String flag, boolean positiveValue, boolean regionDefaultValue, boolean memberDefaultValue, boolean membersIgnoreFlag, boolean nonMemberDefaultValue, boolean nonMembersIgnoreFlag) {
        boolean result;
        if(player.isOp()) {
            result = true;
        } else {
            AccessToken token = AccessTokenFactory.wrap(player);
            Region mostImportant = getHighestPriorityRegionWithFlagAt(location, flag);
            if(mostImportant == null) {
                if(getHighestPriorityRegionAt(location).getType() == RegionType.GLOBAL) {
                    // No owner -> positive
                    result = positiveValue;
                } else {
                    // A owner -> that default
                    result = regionDefaultValue;
                }
            } else {
                if(mostImportant.isOwner(token)) {
                    result = positiveValue;
                } else if(mostImportant.isMember(token)) {
                    // Use user default
                    result = memberDefaultValue;
                    // If members don't ignore this flag
                    if(!membersIgnoreFlag) {
                        if (mostImportant.getFlag(flag, BooleanFlag.class) == positiveValue) {
                            // Flag is set to positive -> true
                            result = positiveValue;
                        } else {
                            //Flag is set to !default
                            result = !positiveValue;
                        }
                    }
                } else {
                    // Use non-user default
                    result = nonMemberDefaultValue;
                    if(!nonMembersIgnoreFlag) {
                        if (mostImportant.getFlag(flag, BooleanFlag.class) == positiveValue) {
                            // Flag is set to positive -> true
                            result = positiveValue;
                        } else {
                            //Flag is set to !default
                            result = !positiveValue;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    
    protected boolean _isSet(Location location, String flag) {
        return getRegions().stream().anyMatch(region -> region.contains(location) && region.hasFlag(flag));
    }
    
    protected ShapedRegion _create(com.sk89q.worldedit.regions.Region selection, AccessToken owner, String name) throws Exception {
        ShapedRegion region = null;
        if (!exists(name)) {
            region = new ShapedRegion(name, selection);
            region.addOwner(owner);
            region.setPriority(0);
            HashMap<String, Region> regions = _getWorldRegions(region.getWorld().getName());
            regions.put(name.toLowerCase(), region);
            Bukkit.getPluginManager().callEvent(new RegionCreateEvent(region));
        }
        return region;
    }
    
    
    protected void _reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        Set<String> keySet = config.getKeys(false);
        // If there are regions, make sure to send out the unload events for all of them.
        if(regions != null) {
            for (HashMap<String, Region> world : new ArrayList<>(regions.values())) {
                for(Region region : new ArrayList<>(world.values())) {
                    // No need to actually unload since we're resetting the entire HashMap.
                    Bukkit.getPluginManager().callEvent(new RegionUnloadEvent(region));
                }
            }
        }
        // Create a new HashMap
        regions = new HashMap<>(keySet.size());
        for (String name : keySet) {
            if (config.isConfigurationSection(name)) {
                Region region;
                if (RegionType.valueOf(config.getString(name + ".type")) == RegionType.GLOBAL) {
                    try {
                        region = new GlobalRegion(config.getConfigurationSection(name));
                        _getWorldRegions(region.getWorld().getName()).put(name, region);
                        Bukkit.getPluginManager().callEvent(new RegionLoadEvent(region));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        region = new ShapedRegion(config.getConfigurationSection(name));
                        _getWorldRegions(region.getWorld().getName()).put(name, region);
                        Bukkit.getPluginManager().callEvent(new RegionLoadEvent(region));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Bukkit.getWorlds().stream().filter(world -> getRegion("global:" + world.getName()) == null).forEach(world -> {
            GlobalRegion reg = new GlobalRegion(world);
            _getWorldRegions(reg.getWorld().getName()).put(world.getName(), reg);
        });
        // Load wether or not to use Subtitles
        PlayerNotification.setUseSubtitles(config.getBoolean("use-subtitle-notifications", true));
    }
    
    
    protected boolean _destroyRegion(String name) {
        Region reg = getRegion(name);
        if (reg == null || reg.getType() == RegionType.GLOBAL) return false;
        HashMap<String, Region> regions = _getWorldRegions(reg.getWorld().getName());
        for (String n : regions.keySet().toArray(new String[regions.size()])) {
            if (n.equalsIgnoreCase(name)) {
                regions.remove(n);
            }
        }
        Bukkit.getPluginManager().callEvent(new RegionDeleteEvent(reg));
        return true;
    }
    
    
    protected void _save() throws Exception {
        if(configFile == null) {
            return;
        }
        config = new YamlConfiguration();
        regions.values().forEach(regionSet -> {
            for (Region reg : regionSet.values()) {
                try {
                    reg.save(config.createSection(reg.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        FileWriter fw = new FileWriter(configFile);
        fw.write(config.saveToString());
        fw.close();
        if (RegionManagerPlugin.getInstance().getLastBackup() < System.currentTimeMillis() / 1000 - RegionManagerPlugin.getInstance().getBackupDelay()) { // Every 2 hours, hardcoded.
            File backupFolder = new File(RegionManagerPlugin.getInstance().getDataFolder(), "backups");
            if(!backupFolder.exists()) {
                backupFolder.mkdir();
            }
            FileWriter fwBackup = new FileWriter(new File(backupFolder, "regions_" + new Date().toString().replaceAll(" |:", "_") + ".yml"));
            fwBackup.write(config.saveToString());
            fwBackup.close();
            RegionManagerPlugin.getInstance().setBackupMade();
        }
    }
    
    protected Region _getRegion(String name) {
        for (HashMap<String, Region> regionSet : regions.values()) {
            for (Region reg : regionSet.values()) {
                if (reg.getName().equalsIgnoreCase(name)) {
                    return reg;
                }
            }
        }
        return null;
    }
    
    protected PriorityQueue _getRegions() {
        PriorityQueue regions = new PriorityQueue();
        this.regions.values().forEach(regionSet -> regions.addAll(regionSet.values()));
        return regions;
    }
    
    protected PriorityQueue _getRegions(World world) {
        return new PriorityQueue(_getWorldRegions(world.getName()).values());
    }
    
    protected PriorityQueue _getRegionsAtLocation(Location loc) {
        return _getRegions(loc.getWorld()).stream().filter(reg -> reg.contains(loc)).collect(Collectors.toCollection(PriorityQueue::new));
    }
    
    protected Region _getOneRegionAtLocation(Location loc) {
        for (Region reg : _getRegions(loc.getWorld())) {
            if (reg.contains(loc)) return reg;
        }
        return null;
    }

    public static void init() {
        instance = new RegionRegistry();
    }
}
