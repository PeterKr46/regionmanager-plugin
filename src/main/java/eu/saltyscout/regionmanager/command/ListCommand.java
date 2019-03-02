package eu.saltyscout.regionmanager.command;

import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.region.RegionType;
import eu.saltyscout.regionmanager.user.AccessToken;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Peter on 17.11.2016.
 */
public class ListCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Stream<Region> regionStream = RegionRegistry.getRegions().stream();
        int page = 1;
        for (String arg : args) {
            String[] split = arg.split("=");
            if (split.length == 1) {
                try {
                    page = Integer.parseInt(arg);
                } catch (Exception e) {
                    sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
                    return true;
                }
            } else {
                switch (split[0].toLowerCase()) {
                    case "world": {
                        regionStream = regionStream.filter(region -> region.getWorld().getName().equalsIgnoreCase(split[1]));
                        break;
                    }
                    case "type": {
                        regionStream = regionStream.filter(region -> region.getType().toString().equalsIgnoreCase(split[1]));
                        break;
                    }
                    case "owner": {
                        AccessToken check = AccessTokenFactory.wrap(split[1]);
                        regionStream = regionStream.filter(region -> region.isOwner(check));
                        break;
                    }
                    case "member": {
                        AccessToken check = AccessTokenFactory.wrap(split[1]);
                        regionStream = regionStream.filter(region -> region.isMember(check));
                        break;
                    }
                    case "hasflag": {
                        regionStream = regionStream.filter(region -> region.hasFlag(split[1]));
                        break;
                    }
                    default: {
                        sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
                        return true;
                    }
                }
            }
        }
        page = Math.max(1, page);
        List<Region> regions = regionStream.collect(Collectors.toList());
        int regionCount = regions.size();
        int maxPages = (regionCount + 5) / 5;
        sender.sendMessage(String.format(Lang.SHOWING_PAGE_X_OF_Y, page, maxPages));
        List<String> data = regions.stream().skip((page - 1) * 5).limit(5).map(region ->
                String.format(
                        Lang.REGION_SUMMARY_LIST,
                        region.getName(),
                        region.getType(),
                        region.getWorld().getName())
        ).collect(Collectors.toList());
        sender.sendMessage(data.toArray(new String[data.size()]));
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "region.list";
    }

    @Override
    public String getDescription() {
        return "This command gives a list of all regions. You can filter out results by world, owner, type or flags. Simply add world=<name>, type=<TYPE>, owner=<name> or world=<world> as parameter.";
    }

    @Override
    String getUsage() {
        return "[filter=requirement] [..]";
    }

    static String[] reqs = {"world=", "type=", "owner=", "member=", "hasflag="};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String toComplete = args[args.length-1];
        String[] parts = toComplete.split("=",-1);
        if(parts.length == 1) {
            return Arrays.stream(reqs).filter(s -> s.startsWith(parts[0])).collect(Collectors.toList());
        } else {
            switch (parts[0]) {
                case "world":
                    return Bukkit.getWorlds().stream().map(World::getName).filter(w -> w.startsWith(parts[1])).map(s -> parts[0] + "=" + s).collect(Collectors.toList());
                case "type":
                    return Arrays.stream(RegionType.values()).map(Enum::toString).filter(w -> w.startsWith(parts[1])).map(s -> parts[0] + "=" + s).collect(Collectors.toList());
                case "owner": case "member":
                    return Bukkit.getOnlinePlayers().stream().map(s -> (Player) s).map(Player::getPlayerListName).filter(w -> w.startsWith(parts[1])).map(s -> parts[0] + "=" + s).collect(Collectors.toList());
                case "hasflag":
                    return FlagSet.getFlags().stream().filter(w -> w.startsWith(parts[1])).map(s -> parts[0] + "=" + s).collect(Collectors.toList());
                default:
                    return new ArrayList<>(0);
            }
        }
    }
}
