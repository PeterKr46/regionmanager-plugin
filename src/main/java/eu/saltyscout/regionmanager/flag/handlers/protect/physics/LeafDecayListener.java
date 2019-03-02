package eu.saltyscout.regionmanager.flag.handlers.protect.physics;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class LeafDecayListener implements Listener {

    @EventHandler
    public void onDecay(LeavesDecayEvent evt) {
        if (!RegionRegistry.getBooleanDecision("leaf-decay", true, evt.getBlock().getLocation())) {
            evt.setCancelled(true);
        }
    }
}
