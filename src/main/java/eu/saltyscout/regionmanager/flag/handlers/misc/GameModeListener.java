package eu.saltyscout.regionmanager.flag.handlers.misc;


import eu.saltyscout.regionmanager.event.PlayerChangeRegionsEvent;
import eu.saltyscout.regionmanager.flag.type.GameModeFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.utils.PlayerNotification;
import eu.saltyscout.utils.PriorityQueue;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created by Peter on 27.11.2016.
 */
public class GameModeListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionEnter(PlayerChangeRegionsEvent event) {
        PriorityQueue q = event.getRegionsEntered();
        q.addAll(event.getUnchangedRegions());
        GameMode mode = RegionRegistry.getFlag("gamemode", q, GameModeFlag.class, event.getPlayer().getGameMode());
        if(mode != event.getPlayer().getGameMode()) {
            event.getPlayer().setGameMode(mode);
            PlayerNotification.sendSubtitle(event.getPlayer(), Lang.GAMEMODE_CHANGED_MESSAGE, 10);
        }
    }
}
