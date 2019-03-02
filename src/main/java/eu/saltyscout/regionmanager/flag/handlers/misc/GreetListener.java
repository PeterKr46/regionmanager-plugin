package eu.saltyscout.regionmanager.flag.handlers.misc;




import eu.saltyscout.regionmanager.event.PlayerChangeRegionsEvent;
import eu.saltyscout.regionmanager.flag.type.StringFlag;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Peter on 06-Nov-16.
 */
public class GreetListener implements Listener {
    @EventHandler
    public void onRegionEnter(PlayerChangeRegionsEvent event) {
        Object greeting = RegionRegistry.getFlag("greeting", event.getRegionsLeft(), StringFlag.class, null);
        if (greeting != null) {
            event.getPlayer().sendMessage(greeting.toString());
        }
    }
}
