package eu.saltyscout.regionmanager.command;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17.11.2016.
 */
public class PriorityCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 1) {
            Region region = RegionRegistry.getRegion(args[0]);
            if (region == null) {
                sender.sendMessage(Lang.REGION_NOT_FOUND);
            } else {
                try {
                    int priority = Integer.parseInt(args[1]);
                    region.setPriority(priority);
                    sender.sendMessage(Lang.PRIORITY_UPDATED);
                } catch (Exception e) {
                    sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
                }
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
        return "priority";
    }

    @Override
    public String getPermission() {
        return "region.priority";
    }

    @Override
    public String getDescription() {
        return "This command allows you to set the priority of a region.";
    }

    @Override
    String getUsage() {
        return "<region> <new priority>";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            return RegionRegistry.getRegions().stream().map(Region::getName).filter(r -> r.startsWith(args[0])).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
