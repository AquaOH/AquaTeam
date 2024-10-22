package cc.aquaoh.aquaTeam.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // 提供子命令的建议
            if (sender.hasPermission("aquaTeam.admin") || sender.isOp()) {
                suggestions.addAll(Arrays.asList("reload",  "match"));
            } else {
                suggestions.add("match");
            }

        } else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if ("paste".equals(subcommand)) {
                suggestions.add("<队伍数量>");  // 提供paste命令的参数提示
            }
        }

        return suggestions;
    }
}
