package eu.saltyscout.regionmanager.flag.handlers.restrict;


import eu.saltyscout.regionmanager.flag.type.list.StringListFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import eu.saltyscout.utils.PriorityQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Peter on 27-Nov-16.
 */
public class CommandListener implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(!player.isOp()) {
            AccessToken token = AccessTokenFactory.wrap(player);
            PriorityQueue regions = RegionRegistry.getRegionsAtLocation(event.getPlayer().getLocation().getBlock().getLocation());
            String command = event.getMessage().split(" ")[0].substring(1).toLowerCase();
            boolean allowed = true;
            for (Region region : regions) {
                if (region.hasFlag("allowed-cmds")) {
                    if (region.getFlag("allowed-cmds", StringListFlag.class).stream().anyMatch(cmd -> cmd.equalsIgnoreCase(command))) {
                        allowed = true;
                    }
                }
                if (region.hasFlag("blocked-cmds")) {
                    if (!region.isMember(token) && !region.isOwner(token)) {
                        if (region.getFlag("blocked-cmds", StringListFlag.class).stream().anyMatch(cmd -> cmd.equalsIgnoreCase(command))) {
                            allowed = false;
                        }
                    }
                }
            }
            if (!allowed) {
                event.setCancelled(true);
                player.sendMessage(Lang.COMMAND_DENY_MESSAGE);
            }
        }
    }
}
