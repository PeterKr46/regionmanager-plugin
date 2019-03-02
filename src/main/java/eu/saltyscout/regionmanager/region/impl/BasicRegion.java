package eu.saltyscout.regionmanager.region.impl;

import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.flag.type.UnknownFlag;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BasicRegion extends Container implements Region {
    private int priority;

    /**
     * Saves all data required for deserialization to a {@link ConfigurationSection}.
     * Will then call for the implementation-specific saving of information using {@link #saveData()}}
     * @param sec the ConfigurationSection to save to.
     */
    @Override
    public final ConfigurationSection save(@Nonnull ConfigurationSection sec) {
        sec.set("type", getType().toString());
        // Serialize Flags
        List<Map> flagDump = new ArrayList<>(numFlags());
        getSetFlags().forEach((id, flag) -> {
            try {
                Map map = flag.serialize();
                map.put("id", id);
                flagDump.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sec.set("flags", flagDump);
        // Serialize Members
        List<String> memberDump = new ArrayList<>(numMembers());
        for(AccessToken member : getMembers()) {
            try {
                memberDump.add(member.dump());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sec.set("members", memberDump);
        // Serialize Owners
        List<String> ownerDump = new ArrayList<>(numOwners());
        for(AccessToken owner : getOwners()) {
            try {
                ownerDump.add(owner.dump());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sec.set("owners", ownerDump);
        sec.set("priority", priority);
        try {
            sec.set("data", saveData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sec;
    }

    /**
     * Silently set the name of the region
     */
    abstract void setName(String name);

    /**
     * Loads data from a {@link ConfigurationSection}.
     * Will then call for the implementation-specific loading of information using {@link #loadData(ConfigurationSection)}
     * @param sec the ConfigurationSection to load from.
     */
    @Override
    public final void load(@Nonnull ConfigurationSection sec) {
        checkNotNull(sec);
        // Name
        setName(sec.getName());
        // Priority
        setPriority(sec.getInt("priority", 0));
        // Owners
        sec.getStringList("owners").stream().map(AccessTokenFactory::wrap).forEach(this::addOwner);
        // Members
        sec.getStringList("members").stream().map(AccessTokenFactory::wrap).forEach(this::addMember);
        // Flags
        if (sec.contains("flags")) {
            List<Map<?, ?>> flagMaps = sec.getMapList("flags");
            for(Map<?,?> map : flagMaps) {
                try {
                    if (map.containsKey("id")) {
                        String id = String.valueOf(map.get("id"));
                        Flag f = FlagSet.get(id);
                        if (f != null) {
                            f.deserialize(map);
                            setFlag(id, f);
                        } else {
                            f = new UnknownFlag((Map<String, Object>) map);
                            setFlag(id, f);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Shape and such.
        try {
            loadData(sec.getConfigurationSection("data"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Saves all implementation-specific data into a Map.
     * Only to be called by {@link #save(ConfigurationSection)}
     * @return the Map
     */
    abstract Map<String, Object> saveData();
    
    /**
     * Loads data from a {@link ConfigurationSection}.
     * The actual handling of this data is determined by the specific implementation.
     * This method is only to be called by {@link #load(ConfigurationSection)}
     * @param sec the ConfigurationSection to load from.
     */
    abstract void loadData(@Nonnull ConfigurationSection sec);


    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }


}