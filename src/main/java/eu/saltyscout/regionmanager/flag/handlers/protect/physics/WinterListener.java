package eu.saltyscout.regionmanager.flag.handlers.protect.physics;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class WinterListener implements Listener {

    @EventHandler
    public void onDecay(BlockFormEvent evt) {
        if(evt.getBlock().getType() == Material.SNOW) {
            if (!RegionRegistry.getBooleanDecision("snow-fall", true, evt.getBlock().getLocation())) {
                evt.setCancelled(true);
            }
        } else if(evt.getBlock().getType() == Material.ICE) {
            if (!RegionRegistry.getBooleanDecision("ice-form", true, evt.getBlock().getLocation())) {
                evt.setCancelled(true);
            }
        }
    }
}
