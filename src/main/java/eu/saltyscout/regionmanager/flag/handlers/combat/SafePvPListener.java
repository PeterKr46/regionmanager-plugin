package eu.saltyscout.regionmanager.flag.handlers.combat;


import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class SafePvPListener implements Listener {

    @EventHandler
    public void onPvP(PlayerDeathEvent evt) {
        Player victim = evt.getEntity();
        if (RegionRegistry.getBooleanDecision("safe-pvp", false, victim.getLocation().getBlock().getLocation())) {
            victim.sendMessage("§4You died in a safe pvp zone. You will keep your Inventory and Level.");
            evt.setKeepInventory(true);
            evt.setKeepLevel(true);
        }
    }
}
