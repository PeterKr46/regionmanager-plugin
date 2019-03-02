package eu.saltyscout.regionmanager.command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.region.RegionType;
import eu.saltyscout.regionmanager.region.impl.ShapedRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17.11.2016.
 */
public class RedefineCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            Player player = (Player) sender;
            Region region = RegionRegistry.getRegion(args[0]);
            if (region != null) {
                if (region.getType() != RegionType.GLOBAL) {
                    WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                    com.sk89q.worldedit.regions.Region selection;
                    try {
                        selection = wep.getSession(player).getRegionSelector(new BukkitWorld(player.getWorld())).getRegion();
                        ((ShapedRegion) region).redefine(selection);
                        sender.sendMessage(Lang.REGION_REDEFINED);
                    } catch (IncompleteRegionException e) {
                        sender.sendMessage(Lang.INCOMPLETE_SELECTION);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(Lang.ERROR_OCCURRED);
                    }
                } else {
                    sender.sendMessage(Lang.CANNOT_EDIT_GLOBAL);
                }
            } else {
                sender.sendMessage(Lang.REGION_NOT_FOUND);
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
        return "redefine";
    }

    @Override
    public String getPermission() {
        return "region.redefine";
    }

    @Override
    public String getDescription() {
        return "This command allows you to redefine the boundary of any non-global region with your current WorldEdit selection.";
    }

    @Override
    String getUsage() {
        return "<region>";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            return RegionRegistry.getRegions().stream().filter(r -> r.getType() != RegionType.GLOBAL).map(Region::getName).filter(r -> r.startsWith(args[0])).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
