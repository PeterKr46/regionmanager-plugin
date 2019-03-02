package eu.saltyscout.regionmanager.flag.handlers.protect;

import eu.saltyscout.regionmanager.flag.type.list.ListFlag;
import eu.saltyscout.regionmanager.flag.type.list.MaterialListFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import eu.saltyscout.utils.PlayerNotification;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

/**
 * Created by Peter on 19.10.2016.
 */
public class BuildListener implements Listener {
    
    private synchronized static boolean canBreak(Player player, Location location, Material material) {
        boolean result;
        AccessToken token = AccessTokenFactory.wrap(player);
        Region mostImportant = RegionRegistry.getHighestPriorityRegionAt(location);
        if(player.isOp() || mostImportant.isOwner(token)) {
            result = true;
        } else {
            boolean buildFlag = RegionRegistry.check(player, location, "build", true, true, false, false);
            boolean breakFlag = RegionRegistry.check(player, location, "block-break", true, true, false, false);
            boolean breakFlagSet = RegionRegistry.isSet(location, "block-break");
            boolean buildFlagSet = RegionRegistry.isSet(location, "build");
            boolean breakOverride = false;
            if (material != null) {
                Region region = RegionRegistry.getHighestPriorityRegionWithFlagAt(location, "block-break-override");
                if (region != null) {
                    breakOverride = region.getFlag("block-break-override", MaterialListFlag.class).contains(material);
                }
            }
            result = breakFlag;
            if (buildFlagSet && breakFlagSet && buildFlag && breakFlag) {
                result = true;
            } else if (!breakFlagSet && !buildFlagSet) {
                // break and build are not set -> Default is: allow
                result = true;
            } else if (breakFlagSet && breakFlag) {
                // break is set to true -> allow, unless overridden.
                result = !breakOverride;
            } else if (breakFlagSet /* && !breakFlag (implicit) */) {
                // break is set to false -> deny, unless overridden.
                result = breakOverride;
            } else if (!buildFlag) {
                // build is set, break is not -> deny, unless overridden.
                result = breakOverride;
            }
        }
        return result;
    }
    
    private synchronized static boolean canPlace(Player player, Location location, Material material) {
        Region mostImportant = RegionRegistry.getHighestPriorityRegionAt(location);
        AccessToken token = AccessTokenFactory.wrap(player);
        if(player.isOp() || mostImportant.isOwner(token)) {
            return true;
        }
        boolean buildFlag = RegionRegistry.check(player, location, "build", true, false, false, false);
        //boolean breakFlag = check(player, location, "block-break", true, true);
        boolean placeOverride = false;
        if(material != null) {
            Region region = RegionRegistry.getHighestPriorityRegionWithFlagAt(location, "block-place-override");
            if (region != null) {
                ListFlag<Material> flag;
                placeOverride = region.getFlag("block-place-override", MaterialListFlag.class).contains(material);
            }
        }
        // build is true or not set -> allow unless overridden.
        if(buildFlag) {
            return !placeOverride;
        } else {
            return placeOverride;
        }
    }

    private synchronized static void sendDenyMessage(Player player) {
        PlayerNotification.sendSubtitle(player, Lang.BUILD_DENY_MESSAGE, 0, 2, 8);
    }
    
    

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!canBreak(evt.getPlayer(), evt.getBlock().getLocation(), evt.getBlock().getType())) {
            evt.setCancelled(true);
            sendDenyMessage(evt.getPlayer());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent evt) {
        if (!canPlace(evt.getPlayer(), evt.getBlock().getLocation(), evt.getBlockPlaced().getType())) {
            evt.setCancelled(true);
            sendDenyMessage(evt.getPlayer());
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent evt) {
        Material bucketContents = evt.getBucket();
        bucketContents = bucketContents.toString().contains("LAVA") ? Material.LAVA : Material.WATER;
        if (!canPlace(evt.getPlayer(), evt.getBlockClicked().getRelative(evt.getBlockFace()).getLocation(), bucketContents)) {
            sendDenyMessage(evt.getPlayer());
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent evt) {
        if (!canBreak(evt.getPlayer(), evt.getBlockClicked().getLocation(), evt.getBlockClicked().getType())) {
            sendDenyMessage(evt.getPlayer());
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onFrameInteract(PlayerInteractEntityEvent evt) {
        if (evt.getRightClicked() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) evt.getRightClicked();
            if (!canBreak(evt.getPlayer(), itemFrame.getLocation().getBlock().getLocation(), Material.ITEM_FRAME)) {
                sendDenyMessage(evt.getPlayer());
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent evt) {
        EntityType hangingType = evt.getEntity().getType();
        Material material = Material.LEAD;
        switch (hangingType) {
            case PAINTING: {
                material = Material.PAINTING;
                break;
            }
            case ITEM_FRAME: {
                material = Material.ITEM_FRAME;
            }
        }
        if (!canPlace(evt.getPlayer(), evt.getEntity().getLocation().getBlock().getLocation(), material)) {
            sendDenyMessage(evt.getPlayer());
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent evt) {
        if (evt.getRemover() instanceof Player) {
            EntityType hangingType = evt.getEntity().getType();
            Material material = Material.LEAD;
            switch (hangingType) {
                case PAINTING: {
                    material = Material.PAINTING;
                    break;
                }
                case ITEM_FRAME: {
                    material = Material.ITEM_FRAME;
                }
            }
            if (!canPlace((Player) evt.getRemover(), evt.getEntity().getLocation().getBlock().getLocation(), material)) {
                PlayerNotification.sendSubtitle((Player) evt.getRemover(), Lang.BUILD_DENY_MESSAGE);
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onStompCrops(PlayerInteractEvent evt) {
        if (evt.getAction() == Action.PHYSICAL && evt.getClickedBlock().getType() == Material.FARMLAND) {
            if (!canBreak(evt.getPlayer(), evt.getClickedBlock().getLocation(), Material.WHEAT)) {
                sendDenyMessage(evt.getPlayer());
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLighter(PlayerInteractEvent evt) {
        if (evt.getItem() != null && evt.getAction() == Action.RIGHT_CLICK_BLOCK && (evt.getItem().getType() == Material.FLINT_AND_STEEL || evt.getItem().getType() == Material.FIRE_CHARGE)) {
            if (!canPlace(evt.getPlayer(), evt.getClickedBlock().getRelative(evt.getBlockFace()).getLocation(), null)) {
                sendDenyMessage(evt.getPlayer());
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHoe(PlayerInteractEvent evt) {
        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getItem() != null && evt.getItem().getType().toString().contains("HOE")) {
            if (!canPlace(evt.getPlayer(), evt.getClickedBlock().getRelative(evt.getBlockFace()).getLocation(), null)) {//TODO
                sendDenyMessage(evt.getPlayer());
                evt.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onJukebox(PlayerInteractEvent evt) {
        if(evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getClickedBlock() != null && evt.getClickedBlock().getType() == Material.JUKEBOX) {
            if (!canBreak(evt.getPlayer(), evt.getClickedBlock().getRelative(evt.getBlockFace()).getLocation(), null)) {
                sendDenyMessage(evt.getPlayer());
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehiclePlace(PlayerInteractEvent evt) {
        if (evt.getItem() != null &&
                (evt.getItem().getType().toString().contains("MINECART") ||
                        evt.getItem().getType().toString().contains("BOAT")) ) {
            if (!canPlace(evt.getPlayer(), evt.getClickedBlock().getRelative(evt.getBlockFace()).getLocation(), null)) {
                sendDenyMessage(evt.getPlayer());
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleBreak(VehicleDamageEvent evt) {
        if (evt.getAttacker() instanceof Player) {
            if (!canBreak((Player) evt.getAttacker(), evt.getVehicle().getLocation().getBlock().getLocation(), null)) {
                PlayerNotification.sendSubtitle((Player) evt.getAttacker(), Lang.BUILD_DENY_MESSAGE);
                evt.setCancelled(true);
            }
        }
    }


}
