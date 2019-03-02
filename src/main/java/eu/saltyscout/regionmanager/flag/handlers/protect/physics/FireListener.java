package eu.saltyscout.regionmanager.flag.handlers.protect.physics;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class FireListener implements Listener {

    @EventHandler
    public void onIgnite(BlockIgniteEvent evt) {
        if (evt.getCause() == BlockIgniteEvent.IgniteCause.SPREAD || evt.getCause() == BlockIgniteEvent.IgniteCause.LAVA) {
            if (!RegionRegistry.getBooleanDecision("fire-spread", true, evt.getBlock().getLocation()) || !RegionRegistry.getBooleanDecision("fire-spread", true, evt.getIgnitingBlock().getLocation())) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onIgnite(BlockBurnEvent evt) {
        if (!RegionRegistry.getBooleanDecision("fire-spread", true, evt.getBlock().getLocation())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireExtinguish(BlockFadeEvent evt) {
        if (!RegionRegistry.getBooleanDecision("fire-spread", true, evt.getBlock().getLocation())) {
            evt.setCancelled(true);
        }
    }
}
