package eu.saltyscout.regionmanager.flag.handlers.restrict;

import eu.saltyscout.regionmanager.RegionManagerPlugin;
import eu.saltyscout.regionmanager.flag.type.BooleanFlag;
import eu.saltyscout.regionmanager.flag.type.StringFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Peter on 02-Nov-16.
 */
public class ExitListener implements Listener {

    private HashMap<Player, HashMap<Block, Long>> delays = new HashMap<>();

    public ExitListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                delays.forEach((player, blocks) -> {
                    if (!blocks.isEmpty()) {
                        blocks.forEach((block, show) -> {
                            if (show <= System.currentTimeMillis()) {
                                player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                            }
                        });
                    }
                });
            }
        }.runTaskTimer(RegionManagerPlugin.getInstance(), 20L, 20L);
    }

    public synchronized static boolean checkLeave(Player player, Location loc) {
        // OP overrides regions
        if (player.isOp()) {
            return true;
        }
        AccessToken playerMember = AccessTokenFactory.wrap(player);
        Region mostImportant = RegionRegistry.getHighestPriorityRegionWithFlagAt(loc, "exit");
        // No owner with leave flag -> definitely allowed.
        if (mostImportant == null) {
            return true;
        }
        // Owner of a owner can always leave it, so can members.
        if (mostImportant.isOwner(playerMember) || mostImportant.isMember(playerMember)) {
            return true;
        }
        // A owner with build set exists, the player does not own it -> return the state of the flag.
        return mostImportant.getFlag("exit", BooleanFlag.class, true);
    }

    private void sendWalls(final Player player, Region deniedBy, Location from) {
        final ArrayList<Block> deny = new ArrayList<>(12);
        for (int x = from.getBlockX() - 2; x < from.getBlockX() + 2; x++) {
            for (int y = from.getBlockY(); y < from.getBlockY() + 3; y++) {
                for (int z = from.getBlockZ() - 2; z < from.getBlockZ() + 2; z++) {
                    if (!deniedBy.contains(x, y, z)) {
                        deny.add(deniedBy.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }

        if (!delays.containsKey(player)) {
            delays.put(player, new HashMap<>());
        }
        final HashMap<Block, Long> timers = delays.get(player);
        deny.forEach(b -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendBlockChange(b.getLocation(), b.getType() == Material.AIR ? Material.BARRIER : b.isLiquid() ? Material.ICE : b.getType(), (byte) 0);
                }
            }.runTaskLater(RegionManagerPlugin.getInstance(), (long) (Math.random() * 4));
            timers.put(b, System.currentTimeMillis() + 10000);
        });
    }

    @EventHandler
    public void onInvalidLeave(PlayerMoveEvent event) {
        if (!checkLeave(event.getPlayer(), event.getFrom().getBlock().getLocation())) {
            Region confinedBy = RegionRegistry.getHighestPriorityRegionWithFlagAt(event.getFrom().getBlock().getLocation(), "exit");
            if (confinedBy != null && !confinedBy.contains(event.getTo().getBlock().getLocation())) {
                event.setCancelled(true);
                String denyMessage = String.valueOf(confinedBy.getFlag("exit-deny-text", StringFlag.class, Lang.EXIT_DENY_MESSAGE));
                PlayerNotification.sendSubtitle(event.getPlayer(), denyMessage);
            }
            sendWalls(event.getPlayer(), confinedBy, event.getTo().getBlock().getLocation());
        }
    }
}
