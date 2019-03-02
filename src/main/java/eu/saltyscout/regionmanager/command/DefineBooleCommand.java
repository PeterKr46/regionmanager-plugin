package eu.saltyscout.regionmanager.command;


import eu.saltyscout.booleregion.exception.InvalidBooleanLogicException;
import eu.saltyscout.booleregion.region.BooleRegion;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.user.AccessTokenFactory;
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
public class DefineBooleCommand extends RegionCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length > 1) {
            String name = args[0];
            String boole = String.join(" ", Arrays.stream(args).skip(1).collect(Collectors.toSet()));
            try {
                BooleRegion booleRegion = BooleParser.parse(boole);
                RegionRegistry.create(booleRegion, AccessTokenFactory.wrap(player), name);
                sender.sendMessage(Lang.REGION_DEFINED);
            } catch (InvalidBooleanLogicException e) {
                sender.sendMessage(e.getMessage());
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
        return "defineboole";
    }

    @Override
    public String getPermission() {
        return "region.define.boole";
    }

    @Override
    public String getDescription() {
        return "This command allows the creation of Regions via boolean logic. Use the operators +, * and \\ for Union, Intersection and Difference. Wrap operations in brackets to chain them. eg. (a\\b)*c or ((a*b)\\c)+d.";
    }

    @Override
    String getUsage() {
        return "<name> <boolean logic>";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 2) {
            String booleLogic = args[1];
            String[] booleNames = booleLogic.split("[\\*\\+\\(\\)\\\\]", -1);
            String keptLogicOnComplete = booleLogic.substring(0, booleLogic.length() -  booleNames[booleNames.length - 1].length());
            System.out.println("Boole: " + Arrays.toString(booleNames));
            return RegionRegistry.getRegions().stream().map(Region::getName).filter(s -> s.startsWith(booleNames[booleNames.length-1])).map(s -> keptLogicOnComplete + s).collect(Collectors.toList());
        } else {
            return new ArrayList<>(0);
        }
    }
}
