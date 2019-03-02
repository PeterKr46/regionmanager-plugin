package eu.saltyscout.regionmanager.flag.handlers.combat;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class MobDamageListener implements Listener {

    @EventHandler
    public void onDamageByMob(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            if (evt.getDamager() instanceof Monster) {
                if (!RegionRegistry.getBooleanDecision("mob-damage", true, evt.getEntity().getLocation().getBlock().getLocation())) {
                    evt.setCancelled(true);
                }
            }
        }
    }
}
