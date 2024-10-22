package cc.aquaoh.aquaTeam.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ExecCommandUtil {
    public static void playerExecCommand(List<String> command, Player player) {
        if(command == null || command.isEmpty()){
            return;
        }
        for (String cmd : command) {
            if(cmd == null || cmd.isEmpty()){
                continue;
            }
            cmd = PlaceholderAPI.setPlaceholders(player, cmd);
            Bukkit.dispatchCommand(player, cmd);
        }

    }


    public static void consoleExecCommand(List<String> command, Player player) {
        if(command == null || command.isEmpty()){
            return;
        }
        for (String cmd : command) {
            if(cmd == null || cmd.isEmpty()){
                continue;
            }
            cmd = PlaceholderAPI.setPlaceholders(player, cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }

    public static void playerAllExecCommand(List<String> command) {
        if(command == null || command.isEmpty()){
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerExecCommand(command, player);
        }

    }

    public static void consoleAllExecCommand(List<String> command) {
        if(command == null || command.isEmpty()){
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            consoleExecCommand(command, player);
        }
    }

}
