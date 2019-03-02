package eu.saltyscout.regionmanager.region.impl;


import eu.saltyscout.regionmanager.region.RegionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Peter on 11.11.2014.
 */
public class GlobalRegion extends BasicRegion implements eu.saltyscout.regionmanager.region.GlobalRegion{
    private World world;

    GlobalRegion(@Nonnull World world) {
        checkNotNull(world);
        this.world = world;
        setPriority(-1);
    }

    GlobalRegion(@Nonnull ConfigurationSection configurationSection) throws Exception {
        checkNotNull(configurationSection);
        super.load(configurationSection);
    }

    @Override
    void setName(String name) {

    }

    @Override
    protected HashMap<String, Object> saveData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", getType().toString());
        map.put("world", getWorld().getName());
        return map;
    }

    @Override
    protected void loadData(@Nonnull ConfigurationSection sec) {
        checkNotNull(sec);
        world = Bukkit.getWorld(sec.getString("world"));
    }

    @Override
    public RegionType getType() {
        return RegionType.GLOBAL;
    }
    
    @Override
    public String getName() {
        return "global:" + getWorld().getName();
    }
    
    @Override
    public boolean rename(@Nonnull String name) {
        return false;
    }

    @Override
    public boolean contains(@Nonnull Location loc) {
        checkNotNull(loc);
        return getWorld() == loc.getWorld();
    }
    
    @Override
    public boolean contains(@Nullable World world, float x, float y, float z) {
        return world != null && world == this.getWorld();
    }
    
    @Override
    public boolean contains(float x, float y, float z) {
        return true;
    }

    @Override
    public World getWorld() {
        return world;
    }
    
}
