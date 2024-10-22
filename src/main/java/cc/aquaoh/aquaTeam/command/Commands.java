package cc.aquaoh.aquaTeam.command;


import cc.aquaoh.aquaTeam.AquaTeam;
import cc.aquaoh.aquaTeam.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false; // 没有指定子命令
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                return handleReload(sender);
            case "setrespawn":

            case "respawnall":
                return handleRespawnAll(sender);
            case "match":
                return handleMatch(sender);
            case "paste":
                //return handlePaste(sender, args);
            case "forcefinish":
                try {
                    return handleForceFinish(sender);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            default:
                return false; // 未知的子命令
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            AquaTeam.getConfigUtil().reloadAllConfigKeys();
            AquaTeam.getTeam().reloadConfig();
            AquaTeam.getTimer().reloadConfig();
            AquaTeam.getGlobalState().initGlobalState();
            AquaTeam.getLuckyBlock().reloadConfig();
            AquaTeam.getPVP().reloadConfig();
            AquaTeam.getFinish().reloadConfig();
            Bukkit.getLogger().info("[AquaTeam] 重载配置完成");
            return true;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("aquateam.reload") || player.isOp()) {
                AquaTeam.getConfigUtil().reloadAllConfigKeys();
                AquaTeam.getTeam().reloadConfig();
                AquaTeam.getTimer().reloadConfig();
                AquaTeam.getGlobalState().initGlobalState();
                AquaTeam.getLuckyBlock().reloadConfig();
                AquaTeam.getPVP().reloadConfig();
                AquaTeam.getFinish().reloadConfig();
                Bukkit.getLogger().info("[AquaTeam] 重载配置完成");
                return true;
            } else {
                player.sendMessage("你无执行该命令的权限");
                return false;
            }
        }
        return false;
    }



    private boolean handleRespawnAll(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("aquaTeam.respawnall") || player.isOp()) {
                // 重生所有玩家的逻辑
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.teleport(p.getWorld().getSpawnLocation());
                }
                player.sendMessage("[AquaTeam] 所有玩家已重生!");
                return true;
            } else {
                player.sendMessage("你无执行该命令的权限");
                return false;
            }
        }
        return false;
    }

    private boolean handleMatch(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        AquaTeam.getTeam().buildMenu(player);
        return true;
    }

    private boolean handleForceFinish(CommandSender sender) throws IOException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("aquaTeam.forcefinish") || player.isOp()) {
                AquaTeam.getLuckyBlock().end();

                return true;
            } else {
                player.sendMessage("你无执行该命令的权限");
                return false;
            }
        }
        return false;
    }





}

