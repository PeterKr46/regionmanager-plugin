package eu.saltyscout.regionmanager.flag.handlers.protect;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Created by Peter on 15.11.2016.
 */
public class InteractListener implements Listener {

    private boolean isContainer(BlockState state) {
        return state instanceof InventoryHolder;
    }

    private boolean isContainer(Entity entity) {
        return entity instanceof InventoryHolder;
    }

    private boolean isGate(BlockState material) {
        return material instanceof Openable;
    }

    private boolean isPowerable(BlockState material) {
        return material instanceof Powerable;
    }

    @EventHandler
    public void onContainerOpen(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (isContainer(block.getState()) && !RegionRegistry.check(event.getPlayer(), block.getLocation(), "open-container", true, true, true, true)) {
                event.setCancelled(true);
                PlayerNotification.sendSubtitle(event.getPlayer(), Lang.CONTAINER_DENY_MESSAGE);
            }
        }
    }

    @EventHandler
    public void onContainerOpen(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (isContainer(entity)
                && !RegionRegistry.check(event.getPlayer(), entity.getLocation().getBlock().getLocation(), "open-container", true, true, true, true)
                ) {
            event.setCancelled(true);
            PlayerNotification.sendSubtitle(event.getPlayer(), Lang.CONTAINER_DENY_MESSAGE);
        }
    }

    @EventHandler
    public void onGateOpen(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (isGate(block.getState()) && !RegionRegistry.check(event.getPlayer(), block.getLocation(), "open-gate", true, true, true, true)) {
                event.setCancelled(true);
                PlayerNotification.sendSubtitle(event.getPlayer(), Lang.GATE_DENY_MESSAGE);
            }
        }
    }

    @EventHandler
    public void onRedstoneEdit(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (isPowerable(block.getState()) && !isGate(block.getState()) && !RegionRegistry.check(event.getPlayer(), block.getLocation(), "edit-redstone", true, true, true, true)) {
                event.setCancelled(true);
                PlayerNotification.sendSubtitle(event.getPlayer(), Lang.REDSTONE_DENY_MESSAGE);
            }
        }
    }
}
