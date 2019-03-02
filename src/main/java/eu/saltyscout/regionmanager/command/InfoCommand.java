package eu.saltyscout.regionmanager.command;


import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Peter on 17.11.2016.
 */
public class InfoCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            if(sender instanceof Player) {
                sender.sendMessage(Lang.REGIONS_AT_LOCATION);
                Stream<Region> regions = RegionRegistry.getRegionsAtLocation(((Player)sender).getLocation().getBlock().getLocation()).stream();
                List<String> data = regions.map(region ->
                        String.format(
                                Lang.REGION_SUMMARY_INFO,
                                region.getName(),
                                region.getPriority())
                ).collect(Collectors.toList());
                Collections.reverse(data);
                sender.sendMessage(data.toArray(new String[data.size()]));
            } else {
                sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
            }
        } else {
            Region region = RegionRegistry.getRegion(args[0]);
            if (region == null) {
                sender.sendMessage(Lang.REGION_NOT_FOUND);
                return true;
            }
            String[] messages = new String[11];
            messages[0] = (String.format(Lang.ATTR_NAME, region.getName()));
            messages[1] = (String.format(Lang.ATTR_OWNERS, region.numOwners()));
            messages[2] = (String.join(", ",
                    region.getOwners().stream().map(
                            member -> String.format(Lang.MEMBER_SUMMARY, member.getName())
                    ).collect(Collectors.toList())
            ));
            messages[3] = (String.format(Lang.ATTR_WORLD, region.getWorld().getName()));
            messages[4] = (String.format(Lang.ATTR_TYPE, region.getType()));
            messages[5] = (String.format(Lang.ATTR_PRIORITY, region.getPriority()));
            messages[6] = (String.format(Lang.ATTR_APPROXIMATE_SIZE, "?"));
            messages[7] = String.format(Lang.ATTR_MEMBERS, region.numMembers());
            messages[8] = (String.join(", ",
                    region.getMembers().stream().map(
                            member -> String.format(Lang.MEMBER_SUMMARY, member.getName())
                    ).collect(Collectors.toList())
            ));
            messages[9] = String.format(Lang.ATTR_FLAGS, region.getSetFlags().size());
            // Filter out unrecognized/invisible flags unless told otherwise.
            Map<String, Flag> flags;
            int ignored = 0;
            if(!(args.length > 1 && args[1].equalsIgnoreCase("debug"))) {
                flags = new HashMap<>();
                for(Map.Entry<String, Flag> entry : region.getSetFlags().entrySet()) {
                    if (FlagSet.exists(entry.getKey())) {
                        flags.put(entry.getKey(), entry.getValue());
                    } else {
                        ignored++;
                    }
                }
            } else {
                flags = region.getSetFlags();
            }
            messages[10] = (String.join(", ",
                    flags.entrySet().stream().map(
                            entry -> String.format(Lang.FLAG_SUMMARY_REGION, entry.getKey(), entry.getValue().toChatString())
                    ).collect(Collectors.toList())
            ) + (ignored > 0 ? String.format("§4 and %s others.", ignored) : ""));
            sender.sendMessage(messages);
        }
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPermission() {
        return "region.info";
    }

    @Override
    public String getDescription() {
        return "This command gives a comprehensive summary of a region's type, owner, members, flags, etc.";
    }

    @Override
    String getUsage() {
        return "[region] {debug}";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            String incompleteRegion = args.length == 0 ? "" : args[0];
            return RegionRegistry.getRegions().stream().map(Region::getName).filter(s -> s.startsWith(incompleteRegion)).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }
}
