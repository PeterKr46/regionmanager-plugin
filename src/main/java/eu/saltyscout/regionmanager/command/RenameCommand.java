package eu.saltyscout.regionmanager.command;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.region.RegionType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17.11.2016.
 */
public class RenameCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 1) {
            Region region = RegionRegistry.getRegion(args[0]);
            if (region == null) {
                sender.sendMessage(Lang.REGION_NOT_FOUND);
            } else if (region.getType() != RegionType.GLOBAL) {
                if (RegionRegistry.exists(args[1])) {
                    sender.sendMessage(Lang.REGION_ALREADY_EXISTS);
                } else if (region.rename(args[1])) {
                    sender.sendMessage(Lang.REGION_RENAMED);
                } else {
                    //Error
                }
            } else {
                sender.sendMessage(Lang.CANNOT_EDIT_GLOBAL);
            }
        } else {
            sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
        }
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getPermission() {
        return "region.rename";
    }

    @Override
    public String getDescription() {
        return "This command allows you to rename any non-global region.";
    }

    @Override
    String getUsage() {
        return "<region> <new name>";
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
