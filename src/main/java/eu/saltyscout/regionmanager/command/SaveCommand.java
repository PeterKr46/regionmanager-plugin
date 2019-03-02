package eu.saltyscout.regionmanager.command;

import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.command.CommandSender;

/**
 * Created by Peter on 17.11.2016.
 */
public class SaveCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            RegionRegistry.save();
            sender.sendMessage(Lang.REGIONS_SAVED);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Lang.ERROR_OCCURRED);
        }
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getPermission() {
        return "region.save";
    }

    @Override
    public String getDescription() {
        return "This command saves all regions in their current state.";
    }

    @Override
    String getUsage() {
        return "";
    }

}
