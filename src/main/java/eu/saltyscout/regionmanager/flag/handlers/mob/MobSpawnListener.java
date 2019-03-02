package eu.saltyscout.regionmanager.flag.handlers.mob;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class MobSpawnListener implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent evt) {
        if (!RegionRegistry.getBooleanDecision("mob-spawning", true, evt.getLocation())) {
            evt.setCancelled(true);
        }
    }
}
