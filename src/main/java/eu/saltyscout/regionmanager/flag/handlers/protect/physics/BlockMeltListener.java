package eu.saltyscout.regionmanager.flag.handlers.protect.physics;

import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

/**
 * Created by Peter on 06-Nov-16.
 */
public class BlockMeltListener implements Listener {

    @EventHandler
    public void onLeafDecay(BlockFadeEvent event) {
        if (!RegionRegistry.getBooleanDecision("snow-melt", true, event.getBlock().getLocation())) {
            if (event.getBlock().getType() == Material.SNOW) {
                event.setCancelled(true);
            }
        } else if (!RegionRegistry.getBooleanDecision("ice-melt", true, event.getBlock().getLocation())) {
            if (event.getBlock().getType() == Material.ICE) {
                event.setCancelled(true);
            }
        }
    }
}
