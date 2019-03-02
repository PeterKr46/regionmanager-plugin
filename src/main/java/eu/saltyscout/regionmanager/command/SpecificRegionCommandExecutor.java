package eu.saltyscout.regionmanager.command;

import com.google.common.collect.ImmutableSet;
import eu.saltyscout.regionmanager.lang.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Peter on 02.11.2014.
 */
public class SpecificRegionCommandExecutor implements RegionCommandExecutor, TabCompleter {

    private HashMap<String, RegionCommand> commands = new HashMap<>();

    public SpecificRegionCommandExecutor() {
        String[] classNames = new String[]{
                "DefineBooleCommand", "DefineCommand", "FlagCommand",
                "FlagsCommand", "HelpCommand", "InfoCommand",
                "PriorityCommand", "ReloadCommand", "FlagPurgeCommand",
                "ListCommand", "MemberCommand", "OwnerCommand",
                "RedefineCommand", "RemoveCommand", "RenameCommand",
                "RestoreCommand", "SaveCommand", "SelectCommand"
        };
        for (String className : classNames) {
            try {
                RegionCommand command = (RegionCommand) Class.forName(getClass().getPackage().getName() + "." + className).newInstance();
                command.setExecutor(this);
                commands.put(command.getName(), command);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public RegionCommand getCommand(String command) {
        return commands.get(command);
    }

    @Override
    public Set<RegionCommand> getCommands() {
        return ImmutableSet.copyOf(commands.values());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        String[] pass;
        if (args.length > 0) {
            if (args.length > 1) {
                pass = new String[args.length - 1];
                System.arraycopy(args, 1, pass, 0, pass.length);
            } else {
                pass = new String[0];
            }
            String command = args[0].toLowerCase();
            RegionCommand commandExecutor = getCommand(command);
            if (commandExecutor != null) {
                if (sender.hasPermission(commandExecutor.getPermission())) {
                    if(!commandExecutor.isPlayerOnly() || sender instanceof Player) {
                        return commandExecutor.onCommand(sender, pass);
                    } else {
                        sender.sendMessage(Lang.PLAYER_ONLY_COMMAND);
                    }
                } else {
                    sender.sendMessage(Lang.PERMISSION_DENIED);
                }
            } else {
                sender.sendMessage(Lang.UNRECOGNIZED_COMMAND);
            }
        } else {
            sender.sendMessage("§2Try §a/region help§2.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        // A subcommand has been selected
        if (args.length > 1) {
            // Cut out the first part (subcommand name)
            String[] passedArgs = new String[args.length - 1];
            System.arraycopy(args, 1, passedArgs, 0, passedArgs.length);

            RegionCommand commandExecutor = getCommand(args[0].toLowerCase());
            if (commandExecutor != null) {
                if (sender.hasPermission(commandExecutor.getPermission())) {
                    if (!commandExecutor.isPlayerOnly() || sender instanceof Player) {
                        return commandExecutor.onTabComplete(sender, cmd, alias, passedArgs);
                    }
                }
            }
            return new ArrayList<>(0);
        }
        // No subcommand has been selected, throw all commands at them?
        else {
            return commands.keySet().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
    }
}
