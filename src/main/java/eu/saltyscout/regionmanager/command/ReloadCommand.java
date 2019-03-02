package eu.saltyscout.regionmanager.command;

import eu.saltyscout.regionmanager.RegionManagerPlugin;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import org.bukkit.command.CommandSender;

/**
 * Created by Peter on 17.11.2016.
 */
public class ReloadCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            RegionRegistry.initialize(RegionManagerPlugin.getInstance().getDataFolder());
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Lang.ERROR_OCCURRED);
        }
        sender.sendMessage(Lang.RELOAD_SUCCESSFUL);
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "region.reload";
    }

    @Override
    public String getDescription() {
        return "This command reloads all regions from configuration files.";
    }

    @Override
    String getUsage() {
        return "";
    }


}
