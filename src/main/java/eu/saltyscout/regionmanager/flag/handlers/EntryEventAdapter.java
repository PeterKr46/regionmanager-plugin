package eu.saltyscout.regionmanager.flag.handlers;







import eu.saltyscout.regionmanager.event.PlayerChangeRegionsEvent;
import eu.saltyscout.regionmanager.event.RegionEnterEvent;
import eu.saltyscout.regionmanager.event.RegionExitEvent;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.utils.PriorityQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Peter on 27-Nov-16.
 */
public class EntryEventAdapter implements Listener {

    private PriorityQueue[] getRegionDifferences(Location a, Location b) {
        // Get regions at each location
        PriorityQueue leftRegions = RegionRegistry.getRegionsAtLocation(a.getBlock().getLocation());
        PriorityQueue enteredRegions = RegionRegistry.getRegionsAtLocation(b.getBlock().getLocation());
        // Get the regions at BOTH locations
        PriorityQueue shared = leftRegions.clone();
        shared.removeIf(region -> !enteredRegions.contains(region));
        // Remove the shared locations from both queues
        leftRegions.removeAll(shared);
        enteredRegions.removeAll(shared);
        return new PriorityQueue[] {leftRegions, enteredRegions, shared};
    }

    private void handleMovement(Player player, Location from, Location to, Cancellable event) {
        // Get the difference of owner sets before and after moving
        PriorityQueue[] changes = getRegionDifferences(from, to);
        PriorityQueue leftRegions = changes[0];
        PriorityQueue enteredRegions = changes[1];
        PriorityQueue unchanged = changes[2];
        // Check if any of the regions denies exit, adjust result of PlayerMoveEvent
        for(Region region : leftRegions) {
            RegionExitEvent leaveEvent = new RegionExitEvent(player, region);
            leaveEvent.setCancelled(event.isCancelled());
            Bukkit.getPluginManager().callEvent(leaveEvent);
            event.setCancelled(leaveEvent.isCancelled());
        }
        // Check if any of the regions denies entry, adjust result of PlayerMoveEvent
        for(Region region : enteredRegions) {
            RegionEnterEvent enterEvent = new RegionEnterEvent(player, region);
            enterEvent.setCancelled(event.isCancelled());
            Bukkit.getPluginManager().callEvent(enterEvent);
            event.setCancelled(enterEvent.isCancelled());
        }
        if(!event.isCancelled() && (leftRegions.size() > 0 || enteredRegions.size() > 0)) {
            PlayerChangeRegionsEvent resultingEvent = new PlayerChangeRegionsEvent(player, leftRegions, enteredRegions, unchanged);
            Bukkit.getPluginManager().callEvent(resultingEvent);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        handleMovement(event.getPlayer(), event.getFrom(), event.getTo(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleMovement(event.getPlayer(), event.getFrom(), event.getTo(), event);
    }
}
