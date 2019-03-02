package eu.saltyscout.regionmanager.command;


import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.flag.type.LocationFlag;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
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
public class FlagCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 2) {
            String id = args[1];
            if(FlagSet.isReadonly(id)) {
                sender.sendMessage(Lang.FLAG_IS_READ_ONLY);
            } else {
                Region region = RegionRegistry.getRegion(args[0]);
                if (region == null) {
                    sender.sendMessage(Lang.REGION_NOT_FOUND);
                } else {
                    Flag flag;
                    if (region.hasFlag(id)) {
                        flag = region.getFlag(id);
                    } else {
                        flag = FlagSet.get(id);
                    }
                    if (flag == null) {
                        sender.sendMessage(Lang.UNKNOWN_FLAG);
                    } else {
                        String notFulfilled = null;
                        List<String> dependencies = FlagSet.getDependencies(id);
                        for (int i = 0; notFulfilled == null && i < dependencies.size(); i++) {
                            String depend = dependencies.get(i);
                            if (!region.hasFlag(depend)) {
                                notFulfilled = depend;
                            }
                        }
                        if (notFulfilled == null) {
                            String value = String.join(" ", Arrays.stream(args).skip(2).collect(Collectors.toList()));
                            if (value.equalsIgnoreCase("unset")) {
                                region.clearFlag(id);
                                sender.sendMessage(Lang.FLAG_REMOVED);
                            } else {
                                Object val = null;
                                if (flag instanceof LocationFlag && value.equalsIgnoreCase("here")) {
                                    if (sender instanceof Player) {
                                        val = ((Player) sender).getLocation();
                                    } else {
                                        sender.sendMessage(Lang.PLAYER_ONLY_COMMAND);
                                    }
                                } else {
                                    val = flag.parse(value);
                                }
                                if (val == null) {
                                    sender.sendMessage(Lang.INCORRECT_FLAG_TYPE);
                                } else {
                                    flag.setValue(val);
                                    region.setFlag(id, flag);
                                    sender.sendMessage(Lang.FLAG_UPDATED);
                                }
                            }
                        } else {
                            sender.sendMessage(String.format(Lang.FLAG_DEPENDENCY_NOT_FULFILLED, notFulfilled));
                        }
                    }
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
        return "flag";
    }

    @Override
    public String getPermission() {
        return "region.flag.edit";
    }

    @Override
    public String getDescription() {
        return "This command allows you to set, change or remove flags from regions. More information on specific flags can be found on the last help pages.";
    }

    @Override
    String getUsage() {
        return "<region> <flag id> <value|unset|here>";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch(args.length) {
            case 1: return RegionRegistry.getRegions().stream().map(Region::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            case 2: return FlagSet.getFlags().stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            default: return new ArrayList<>(0);
        }
    }
}
