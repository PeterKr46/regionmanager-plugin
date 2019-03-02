package eu.saltyscout.regionmanager.worldguard;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import eu.saltyscout.regionmanager.RegionManagerPlugin;
import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.flag.type.StringFlag;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.region.RegionType;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Peter on 27-Oct-16.
 */
public class WorldGuardAdapter {

    public synchronized static boolean hasWorldGuardData() {
        return getWorldGuardDataFolder().exists();
    }

    private synchronized static File getWorldGuardDataFolder() {
        File configDir = RegionManagerPlugin.getInstance().getDataFolder().getParentFile();
        return new File(configDir, "WorldGuard");
    }

    public synchronized static int loadWorldGuardData() throws IOException, InvalidConfigurationException, IncompleteRegionException {
        File wgF = getWorldGuardDataFolder();
        File worldsF = new File(wgF, "worlds");
        // Check if the entire Data folder structure exists
        if (!wgF.exists() || !worldsF.exists()) return 0;
        int restored = 0;
        // Iterate the listed Worlds
        for (File worldF : worldsF.listFiles()) {
            World world = Bukkit.getWorld(worldF.getName());
            // Check if they exist on the current server
            if (world != null) {
                // Grab their regions, if they exist.
                File regions = new File(worldF, "regions.yml");
                if (regions.exists()) {
                    // Load the owner config
                    YamlConfiguration config = new YamlConfiguration();
                    config.load(regions);
                    // Grab the owner section, iterate the owner names.
                    ConfigurationSection regionSection = config.getConfigurationSection("regions");
                    for (String name : regionSection.getKeys(false)) {
                        // Grab the owner's data.
                        ConfigurationSection region = regionSection.getConfigurationSection(name);
                        // Check if the ID is taken, while it is add the world name.
                        while(RegionRegistry.exists(name)) {
                            name += "_" + world.getName();
                        }

                        RegionType type = null;
                        com.sk89q.worldedit.regions.Region selection = null;
                        // Load the Selection.
                        switch (region.getString("type")) {
                            case "cuboid": {
                                type = RegionType.CUBOID;
                                selection = new CuboidRegion(
                                        new BukkitWorld(world),
                                        BlockVector3.at(
                                                region.getInt("min.x"),
                                                region.getInt("min.y"),
                                                region.getInt("min.z")),
                                        BlockVector3.at(
                                                region.getInt("max.x"),
                                                region.getInt("max.y"),
                                                region.getInt("max.z"))
                                );
                                break;
                            }
                            case "poly2d": {
                                type = RegionType.POLYGONAL;
                                selection = new Polygonal2DRegion(
                                        new BukkitWorld(world),
                                        region.getMapList("points").stream().map(
                                                f ->
                                                        BlockVector2.at(((Map<String, Integer>) f).get("x"), ((Map<String, Integer>) f).get("z"))).collect(Collectors.toList()),
                                        region.getInt("min-y"),
                                        region.getInt("max-y")
                                );
                                break;
                            }
                        }
                        if (type != null) {
                            // Read and wrap the user list for runtime use.
                            List<AccessToken> members = new ArrayList<>();
                            if (region.contains("members.unique-ids")) {
                                members = region.getStringList("members.unique-ids").stream().map(AccessTokenFactory::wrap).collect(Collectors.toList());
                            }
                            // Read and wrap the owner list for runtime use.
                            List<AccessToken> owners = new ArrayList<>();
                            if (region.contains("owners.unique-ids")) {
                                owners = region.getStringList("owners.unique-ids").stream().map(uid -> AccessTokenFactory.wrap(Bukkit.getOfflinePlayer(uid))).collect(Collectors.toList());
                            }
                            // No owner -> console owner.
                            if (owners.isEmpty()) {
                                owners.add(AccessTokenFactory.getServerMember());
                            }
                            // Get the priority
                            int priority = region.getInt("priority", 0);
                            // Create & register the owner
                            try {
                                Region reg = RegionRegistry.create(selection, owners.get(0), name);
                                // Transfer the data.
                                reg.setPriority(priority);
                                for(AccessToken member : members) {
                                    reg.addMember(member);
                                    System.out.println("Member '" + member + "' added to '" + reg.getName() + "'.");
                                }
                                if (region.contains("flags")) {
                                    MemorySection flagSection = (MemorySection) region.get("flags");
                                    flagSection.getKeys(false).forEach(flagId -> {
                                        String value = flagSection.getString(flagId);
                                        Flag flag = FlagSet.get(flagId);
                                        if (flag == null) {
                                            flag = new StringFlag(null);
                                        }
                                        flag.setValue(flag.parse(value));
                                        reg.setFlag(flagId, flag);
                                    });
                                }
                                restored++;
                                System.out.println("BasicRegion '" + reg.getName() + "' restored from WorldGuard.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return restored;
    }
}
