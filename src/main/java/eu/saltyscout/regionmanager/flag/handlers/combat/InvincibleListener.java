package eu.saltyscout.regionmanager.flag.handlers.combat;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Peter on 06-Nov-16.
 */
public class InvincibleListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (RegionRegistry.getBooleanDecision("invincible", false, event.getEntity().getLocation().getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByBlockEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (RegionRegistry.getBooleanDecision("invincible", false, event.getEntity().getLocation().getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (RegionRegistry.getBooleanDecision("invincible", false, event.getEntity().getLocation().getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
