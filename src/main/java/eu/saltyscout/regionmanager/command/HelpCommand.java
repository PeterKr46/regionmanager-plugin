package eu.saltyscout.regionmanager.command;


import com.google.common.collect.Streams;
import eu.saltyscout.regionmanager.flag.FlagSet;
import eu.saltyscout.regionmanager.lang.Lang;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 20-Nov-16.
 */
public class HelpCommand extends RegionCommand {
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String match;
        int page = 1;
        if(args.length == 1) {
            if(NumberUtils.isNumber(args[0])) {
                page = Integer.parseInt(args[0]);
                match = "";
            } else {
                match = args[0];
            }
        } else if(args.length == 2){
            if(NumberUtils.isNumber(args[1])) {
                page = Integer.parseInt(args[1]);
            }
            match = args[0];
        } else {
            match = "";
        }

        List<RegionCommand> commands = getExecutor().getCommands().stream().filter(s -> s.getName().contains(match)).collect(Collectors.toList());
        List<String> flags = FlagSet.getFlags().stream().filter(s -> s.equalsIgnoreCase(match) || s.contains(match)).collect(Collectors.toList());

        List<String> result = new ArrayList<>();

        // Calculate what items to show
        int perPage = 5;
        int totalPages = (commands.size() + flags.size()) / perPage + 1;
        page = Math.min(Math.max(0, page), totalPages);
        // Start index
        int startOffset = (page - 1)* perPage;
        result.add(String.format(Lang.SHOWING_PAGE_X_OF_Y, page, totalPages));
        for(int i = startOffset; i < commands.size() && perPage > 0; i++) {
            perPage--;
            startOffset++;
            result.addAll(buildDescription(commands.get(i)));
        }
        for(int i = startOffset - commands.size(); i < flags.size() && perPage > 0; i++) {
            perPage--;
            result.addAll(buildDescription(flags.get(i)));
        }
        sender.sendMessage(result.toArray(new String[result.size()]));
        return true;
    }

    private List<String> buildDescription(String flag) {
        List<String> data = new ArrayList<>(2);
        List<String> depend = FlagSet.getDependencies(flag);
        String dependencies = depend.size() > 0 ? "(Depends on " + String.join(", ", depend) + ")" : "";
        String type = FlagSet.get(flag).getTypeDescription();
        String description = FlagSet.getDescription(flag);
        data.add(String.format(Lang.FLAG_SUMMARY, flag, dependencies, type));
        data.add(String.format(Lang.FLAG_DESCRIPTION, description));
        return data;
    }

    private List<String> buildDescription(RegionCommand command) {
        List<String> data = new ArrayList<>(2);
        data.add(String.format(Lang.COMMAND_SUMMARY, command.getName(), command.getUsage()));
        data.add(String.format(Lang.COMMAND_DESCRIPTION, command.getDescription()));
        return data;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "region.help";
    }

    @Override
    public String getDescription() {
        return "If you're lost on this, I really don't know what to tell you..";
    }

    @Override
    String getUsage() {
        return " [search] [page number]";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1 ) {
            return Streams.concat(getExecutor().getCommands().stream().map(RegionCommand::getName), FlagSet.getFlags().stream()).filter(name -> name.startsWith(args[0])).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }
}
