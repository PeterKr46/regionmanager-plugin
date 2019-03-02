package eu.saltyscout.regionmanager.command;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17.11.2016.
 */
public class OwnerCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 1) {
            Region region = RegionRegistry.getRegion(args[1]);
            if (region != null) {
                AccessToken accessToken = AccessTokenFactory.wrap(args[2]);
                switch (args[0].toLowerCase()) {
                    case "add": {
                        if (region.addOwner(accessToken)) {
                            sender.sendMessage(Lang.OWNER_ADDED);
                        } else {
                            sender.sendMessage(Lang.ALREADY_AN_OWNER);
                        }
                        break;
                    }
                    case "remove": {
                        if (region.removeOwner(accessToken)) {
                            sender.sendMessage(Lang.OWNER_REMOVED);
                        } else {
                            sender.sendMessage(Lang.OWNER_NOT_FOUND);
                        }
                        break;
                    }
                    case "check": {
                        if (region.isOwner(accessToken)) {
                            sender.sendMessage(String.format(Lang.RESULT_IS_AN_OWNER, accessToken.getName()));
                        } else {
                            sender.sendMessage(String.format(Lang.RESULT_NOT_AN_OWNER, accessToken.getName()));
                        }
                        break;
                    }
                }
            } else {
                sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
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
        return "owner";
    }

    @Override
    public String getPermission() {
        return "region.owner";
    }

    @Override
    public String getDescription() {
        return "This command allows you to set or remove the owner of a region.";
    }

    @Override
    String getUsage() {
        return "<check|remove|add> <region> <[perm:]owner>";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList("check", "remove", "add");
            case 2:
                return RegionRegistry.getRegions().stream().map(Region::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            default:
                return null;
        }
    }
}
