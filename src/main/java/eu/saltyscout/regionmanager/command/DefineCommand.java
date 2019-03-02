package eu.saltyscout.regionmanager.command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Peter on 17.11.2016.
 */
public class DefineCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            Player player = (Player) sender;
            WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            com.sk89q.worldedit.regions.Region selection;
            try {
                selection = wep.getSession(player).getRegionSelector((com.sk89q.worldedit.world.World) new BukkitWorld(player.getWorld())).getRegion();
                RegionRegistry.create(selection, AccessTokenFactory.wrap(player), args[0]);
                sender.sendMessage(Lang.REGION_DEFINED);
            } catch (IncompleteRegionException e) {
                sender.sendMessage(Lang.INCOMPLETE_SELECTION);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Lang.ERROR_OCCURRED);
            }
        } else {
            sender.sendMessage(String.format(Lang.COMMAND_SUMMARY, getName(), getUsage()));
        }
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "define";
    }

    @Override
    public String getPermission() {
        return "region.define";
    }

    @Override
    public String getDescription() {
        return "This command uses your current WorldEdit selection to create a IRegion. All types of WorldEdit selections are supported.";
    }

    @Override
    String getUsage() {
        return "<name>";
    }
}
