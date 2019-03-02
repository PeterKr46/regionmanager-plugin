package eu.saltyscout.regionmanager.command;

import com.sk89q.worldedit.IncompleteRegionException;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.worldguard.WorldGuardAdapter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

/**
 * Created by Peter on 17.11.2016.
 */
public class RestoreCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (WorldGuardAdapter.hasWorldGuardData()) {
            sender.sendMessage(Lang.BEGINNING_WORLDGUARD_RESTORE);
            try {
                int restored = WorldGuardAdapter.loadWorldGuardData();
                sender.sendMessage(String.format(Lang.NUM_WORLDGUARD_REGIONS_RESTORED, restored));
            } catch (IOException | InvalidConfigurationException | IncompleteRegionException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(Lang.NO_WORLDGUARD_DATA);
        }
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "restore";
    }

    @Override
    public String getPermission() {
        return "region.restore";
    }

    @Override
    public String getDescription() {
        return "This command restores all regions from WorldGuard configuration files. WorldGuard does not need to be running.";
    }

    @Override
    String getUsage() {
        return "";
    }


}
