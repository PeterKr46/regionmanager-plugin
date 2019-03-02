package eu.saltyscout.regionmanager.flag.handlers.restrict;


import eu.saltyscout.regionmanager.flag.type.BooleanFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Peter on 06-Nov-16.
 */
public class AllowTeleportListener implements Listener {
    
    static boolean canTeleport(Player player, Location loc, String flag) {
        // OP overrides regions
        if (player.isOp()) {
            return true;
        }
        AccessToken playerMember = AccessTokenFactory.wrap(player);
        Region mostImportant = RegionRegistry.getHighestPriorityRegionWithFlagAt(loc, flag);
        // No owner with build flag -> check if there is a owner at all.
        if (mostImportant == null) {
            return true;
        }
        // Owner of a owner can always build inside it. Highest priority owner ownership = ability to build.
        if (mostImportant.isOwner(playerMember) || mostImportant.isMember(playerMember)) {
            return true;
        }
        // A owner with build set exists, the player does not own it -> return the state of the flag.
        return mostImportant.getFlag(flag, BooleanFlag.class, true);
    }
    
    @EventHandler
    public void onRegionEnter(PlayerTeleportEvent event) {
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            if (!canTeleport(event.getPlayer(), event.getFrom(), "teleport-from")) {
                event.setCancelled(true);
                PlayerNotification.sendSubtitle(event.getPlayer(), Lang.TELEPORT_FROM_DENY_MESSAGE);
            } else if (!canTeleport(event.getPlayer(), event.getTo(), "teleport-to")) {
                event.setCancelled(true);
                PlayerNotification.sendSubtitle(event.getPlayer(), Lang.TELEPORT_TO_DENY_MESSAGE);
            }
        }
    }
}
