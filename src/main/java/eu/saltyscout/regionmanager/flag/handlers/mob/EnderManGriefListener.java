package eu.saltyscout.regionmanager.flag.handlers.mob;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class EnderManGriefListener implements Listener {

    @EventHandler
    public void onDamageByMob(EntityChangeBlockEvent evt) {
        if (evt.getEntity() instanceof Enderman) {
            if (RegionRegistry.getBooleanDecision("enderman-protect", false, evt.getBlock().getLocation())) {
                evt.setCancelled(true);
            }
        }
    }
}
