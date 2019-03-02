package eu.saltyscout.regionmanager.flag.handlers.restrict;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class EnderpearlListener implements Listener {

    @EventHandler
    public void onEnderpearl(ProjectileLaunchEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_PEARL && event.getEntity().getShooter() instanceof Player) {
            if (!RegionRegistry.check((Player) event.getEntity().getShooter(), event.getEntity().getLocation().getBlock().getLocation(), "enderpearl", true, true, true, false)) {
                event.setCancelled(true);
                PlayerNotification.sendSubtitle(((Player) event.getEntity().getShooter()), Lang.ENDERPEARL_DENY_MESSAGE);
            }
        }
    }
}
