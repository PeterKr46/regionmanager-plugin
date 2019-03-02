package eu.saltyscout.regionmanager.flag.handlers.protect.physics;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class FlowListener implements Listener {

    @EventHandler
    public void onFlow(BlockFromToEvent evt) {
        if ((evt.getBlock().getType() == Material.LAVA) && (!RegionRegistry.getBooleanDecision("lava-flow", true, evt.getBlock().getLocation()) || !RegionRegistry.getBooleanDecision("lava-flow", true, evt.getToBlock().getLocation()))) {
            evt.setCancelled(true);
        } else if ((evt.getBlock().getType() == Material.WATER) && (!RegionRegistry.getBooleanDecision("water-flow", true, evt.getBlock().getLocation()) || !RegionRegistry.getBooleanDecision("water-flow", true, evt.getToBlock().getLocation()))) {
            evt.setCancelled(true);
        }
    }
}
