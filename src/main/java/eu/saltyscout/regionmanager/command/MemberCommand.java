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
public class MemberCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 2) {
            Region region = RegionRegistry.getRegion(args[1]);
            if (region != null) {
                AccessToken accessToken = AccessTokenFactory.wrap(args[2]);
                switch (args[0].toLowerCase()) {
                    case "add": {
                        if (region.addMember(accessToken)) {
                            sender.sendMessage(Lang.MEMBER_ADDED);
                        } else {
                            sender.sendMessage(Lang.ALREADY_A_MEMBER);
                        }
                        break;
                    }
                    case "remove": {
                        if (region.removeMember(accessToken)) {
                            sender.sendMessage(Lang.MEMBER_REMOVED);
                            if (region.isMember(accessToken)) {
                                sender.sendMessage(Lang.MEMBER_REMOVED_WARN_MEMBER);
                            }
                        } else {
                            sender.sendMessage(Lang.MEMBER_NOT_FOUND);
                        }
                        break;
                    }
                    case "check": {
                        if (region.isMember(accessToken)) {
                            sender.sendMessage(String.format(Lang.RESULT_IS_A_MEMBER, accessToken.getName()));
                        } else {
                            sender.sendMessage(String.format(Lang.RESULT_NOT_A_MEMBER, accessToken.getName()));
                        }
                        break;
                    }
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
        return false;
    }

    @Override
    public String getName() {
        return "member";
    }

    @Override
    public String getPermission() {
        return "region.member";
    }

    @Override
    public String getDescription() {
        return "This command allows you to manage check if a player is a member of a region, or to add or remove them from a region. Mark a member as a permission member by adding 'perm:' in front.";
    }

    @Override
    String getUsage() {
        return "<check|remove|add> <region> <[perm:]member>";
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
