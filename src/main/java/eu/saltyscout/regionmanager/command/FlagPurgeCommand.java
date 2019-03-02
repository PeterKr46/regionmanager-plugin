package eu.saltyscout.regionmanager.command;


import eu.saltyscout.regionmanager.flag.Flag;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Created by Peter on 17.11.2016.
 */
public class FlagPurgeCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(Lang.BEGINNING_FLAG_PURGE);
        int purged = 0;
        for(Region region : RegionRegistry.getRegions()) {
            for(Map.Entry<String,Flag> pair : region.getSetFlags().entrySet()) {
                if(!FlagSet.exists(pair.getKey())) {
                    region.clearFlag(pair.getKey());
                    purged++;
                }
            }
        }
        sender.sendMessage(String.format(Lang.FLAG_PURGE_COMPLETE, purged));
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "purgeflags";
    }

    @Override
    public String getPermission() {
        return "region.purgeflags";
    }

    @Override
    public String getDescription() {
        return "This command Purges all unrecognized flags from all regions.";
    }

    @Override
    String getUsage() {
        return "";
    }
}
