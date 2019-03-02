package eu.saltyscout.regionmanager.command;


import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.*;
import eu.saltyscout.booleregion.region.BooleRegion;
import eu.saltyscout.booleregion.region.BooleRegionSelector;
import eu.saltyscout.booleregion.region.RegionWrapper;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17.11.2016.
 */
public class SelectCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            Player player = (Player) sender;
            RegionSelector selector;
            try {
                String boole = String.join(" ", Arrays.asList(args));
                BooleRegion booleRegion = BooleParser.parse(boole);
                selector = new BooleRegionSelector(booleRegion);
                if (((BooleRegionSelector) selector).isRegionWrapper()) {
                    try {
                        Region shape = ((RegionWrapper) selector.getRegion()).getRegion();
                        if (shape.getClass() == CuboidRegion.class) {
                            selector = new com.sk89q.worldedit.regions.selector.CuboidRegionSelector(
                                    shape.getWorld(), shape.getMinimumPoint(), shape.getMaximumPoint());
                        }
                        if (shape.getClass() == EllipsoidRegion.class) {
                            selector = new com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector(
                                    shape.getWorld(), shape.getCenter().toBlockPoint(), ((EllipsoidRegion) shape).getRadius());
                        }
                        if (shape.getClass() == Polygonal2DRegion.class) {
                            selector = new com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector(shape.getWorld(), ((Polygonal2DRegion) shape).getPoints(), ((Polygonal2DRegion) shape).getMinimumY(), ((Polygonal2DRegion) shape).getMaximumY());
                        }
                        if (shape.getClass() == CylinderRegion.class) {
                            selector = new com.sk89q.worldedit.regions.selector.CylinderRegionSelector(
                                    shape.getWorld(),
                                    shape.getCenter().toBlockPoint().toBlockVector2(),
                                    ((CylinderRegion) shape).getRadius(),
                                    ((CylinderRegion) shape).getMinimumY(),
                                    ((CylinderRegion) shape).getMaximumY()
                            );
                        }
                        if (shape.getClass() == ConvexPolyhedralRegion.class) {
                            com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector sel = new com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector(shape.getWorld());
                            for (BlockVector3 vertex : ((ConvexPolyhedralRegion) shape).getVertices()) {
                                ((ConvexPolyhedralRegion) sel.getIncompleteRegion()).addVertex(vertex);
                            }
                            selector = sel;
                        }
                    } catch (IncompleteRegionException e) {
                        e.printStackTrace();
                    }
                }
                WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                try {
                    wep.getSession(player).setRegionSelector(selector.getWorld(), selector);
                    sender.sendMessage(Lang.REGION_SELECTED);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Lang.ERROR_OCCURRED);
                }
            } catch (UnsupportedOperationException e) {
                sender.sendMessage(e.getMessage());
            }
        } else {
            sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
        }
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "select";
    }

    @Override
    public String getPermission() {
        return "region.select";
    }

    @Override
    public String getDescription() {
        return "This command allows you to set your WorldEdit selection to any non-global region or any boolean-logic set of regions (see defineboole for more information).";
    }

    @Override
    String getUsage() {
        return "<region|boolean logic>";
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 2) {
            String booleLogic = args[1];
            String[] booleNames = booleLogic.split("[\\*\\+\\(\\)\\\\]", -1);
            String keptLogicOnComplete = booleLogic.substring(0, booleLogic.length() -  booleNames[booleNames.length - 1].length());
            System.out.println("Boole: " + Arrays.toString(booleNames));
            return RegionRegistry.getRegions().stream().map(eu.saltyscout.regionmanager.region.Region::getName).filter(s -> s.startsWith(booleNames[booleNames.length-1])).map(s -> keptLogicOnComplete + s).collect(Collectors.toList());
        } else {
            return new ArrayList<>(0);
        }
    }

}
