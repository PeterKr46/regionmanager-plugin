package eu.saltyscout.regionmanager.command;


import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Peter on 17.11.2016.
 */
public class FlagsCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Stream<Region> regions = RegionRegistry.getRegionsAtLocation(((Player) sender).getLocation().getBlock().getLocation()).stream();
        Map<String, String> flags = new HashMap<>();
        regions.forEach(
                region -> region.getSetFlags().forEach(
                        (id, flag) -> flags.put(id, String.format(Lang.FLAG_SUMMARY_LIST, id, flag.toChatString(), region.getName()))
                )
        );
        String[] messages =
                new String[]{
                        Lang.FLAGS_AT_LOCATION,
                        String.join(
                                ", ", flags.values()
                        )
                };
        sender.sendMessage(messages);
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "flags";
    }

    @Override
    public String getPermission() {
        return "region.flag.view";
    }

    @Override
    public String getDescription() {
        return "This command supplies a list of flags and their values as they are set at your location. More information on specific flags can be found on the last help pages.";
    }

    @Override
    String getUsage() {
        return "";
    }

}
