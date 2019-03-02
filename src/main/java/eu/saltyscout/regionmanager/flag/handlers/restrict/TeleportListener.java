package eu.saltyscout.regionmanager.flag.handlers.restrict;


import eu.saltyscout.regionmanager.event.RegionEnterEvent;
import eu.saltyscout.regionmanager.flag.type.LocationFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Peter on 06-Nov-16.
 */
public class TeleportListener implements Listener {
    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        if (!event.isCancelled() && event.getRegion().hasFlag("teleport")) {
            Location location = event.getRegion().getFlag("teleport", LocationFlag.class, event.getPlayer().getLocation().getBlock().getLocation());
            event.getPlayer().teleport(location);
            PlayerNotification.sendSubtitle(event.getPlayer(), Lang.TELEPORTED_MESSAGE, 0, 5, 10);
        }
    }
}
