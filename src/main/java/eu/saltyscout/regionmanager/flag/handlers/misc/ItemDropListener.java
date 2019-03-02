package eu.saltyscout.regionmanager.flag.handlers.misc;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Created by Peter on 27.11.2016.
 */
public class ItemDropListener implements Listener {
    @EventHandler
    public void onRegionEnter(PlayerDropItemEvent event) {
        if(!RegionRegistry.check(event.getPlayer(), event.getPlayer().getLocation().getBlock().getLocation(), "drop-item", true, true, true, false)) {
            event.setCancelled(true);
            PlayerNotification.sendSubtitle(event.getPlayer(), Lang.ITEM_DROP_DENY_MESSAGE);
        }
    }
}
