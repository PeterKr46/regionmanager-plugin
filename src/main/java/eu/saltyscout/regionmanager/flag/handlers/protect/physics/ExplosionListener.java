package eu.saltyscout.regionmanager.flag.handlers.protect.physics;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class ExplosionListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent evt) {
        if (evt.getEntity() instanceof Creeper) {
            // Cancel if denied at Creeper Location.
            if (!RegionRegistry.getBooleanDecision("creeper-explosion", true, evt.getLocation())) {
                evt.setCancelled(true);
            } else {
                // Remove protected blocks.
                evt.blockList().removeIf(block -> !RegionRegistry.getBooleanDecision("creeper-explosion", true, block.getLocation()));
            }
        } else {
            // Cancel if denied at Origin.
            if (!RegionRegistry.getBooleanDecision("other-explosion", true, evt.getLocation())) {
                evt.setCancelled(true);
            } else {
                // Remove protected blocks.
                evt.blockList().removeIf(block -> !RegionRegistry.getBooleanDecision("other-explosion", true, block.getLocation()));
            }
        }
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageByEntityEvent evt) {
        if (evt.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && evt.getDamager() instanceof Creeper) {
            // Cancel if denied at Creeper Location.
            if (!RegionRegistry.getBooleanDecision("creeper-explosion", true, evt.getEntity().getLocation().getBlock().getLocation())) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent evt) {
        if (evt.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || evt.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            // Cancel if denied at Creeper Location.
            if (!RegionRegistry.getBooleanDecision("other-explosion", true, evt.getEntity().getLocation().getBlock().getLocation())) {
                evt.setCancelled(true);
            }
        }
    }
}
