package eu.saltyscout.regionmanager.flag.handlers.combat;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class PvPListener implements Listener {

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent evt) {
        if (!(evt.getDamager() instanceof Player && evt.getEntity() instanceof Player)) return;
        Player attacker = (Player) evt.getDamager();
        Player victim = (Player) evt.getEntity();
        boolean attackerFlag = RegionRegistry.getBooleanDecision("pvp", true, attacker.getLocation().getBlock().getLocation());
        if (!attackerFlag) {
            PlayerNotification.sendSubtitle(attacker, Lang.PVP_DENY_MESSAGE_SELF);
            evt.setCancelled(true);
        } else {
            boolean victimFlag = RegionRegistry.getBooleanDecision("pvp", true, victim.getLocation().getBlock().getLocation());
            if (!victimFlag) {
                PlayerNotification.sendSubtitle(attacker, String.format(Lang.PVP_DENY_MESSAGE_OTHER, victim.getName()), 5);
                evt.setCancelled(true);
            }
        }
    }
}
