package cc.aquaoh.aquaTeam.pvp;

import cc.aquaoh.aquaTeam.AquaTeam;
import cc.aquaoh.aquaTeam.util.ConfigUtil;
import cc.aquaoh.aquaTeam.util.WorldeditUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.consoleExecCommand;
import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.playerExecCommand;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.*;

public class PVP {

    private boolean isEnable;
    private int priority;
    private int endType;
    private int maxTeamRespawnLife;
    private int maxPlayerRespawnLife;
    private boolean randomRespawn;

    private String worldeditWorldFrom;
    private String worldeditWorldTo;
    private List<Integer> worldeditPointLow;
    private List<Integer> worldeditPointHigh;
    private List<Integer> worldeditPointPaste;
    private List<List<Integer>> worldeditPointRespawn;

    private List<String> onStartPVPPlayer;
    private List<String> onStartPVPConsole;
    private List<String> onEndPVPPlayer;
    private List<String> onEndPVPConsole;

    private String onStartPVPMessage;
    private String onEndPVPMessage;
    private String onPersonOutMessage;
    private String onTeamOutMessage;
    private String onWiningTeamMessage;
    private String onPersonOutReasonMaxPlayerRespawnLifeMessage;
    private String onPersonOutMaxTeamRespawnLifeMessage;
    private String onPersonRespawnMessage;

    private WorldeditUtil worldeditUtil;

    private String stage;

    private AquaTeam plugin;

    public PVP(AquaTeam plugin) {
        reloadConfig();
        this.plugin = plugin;
    }

    public void reloadConfig() {
        // 获取配置工具类实例
        ConfigUtil configUtil = AquaTeam.getConfigUtil();

        this.isEnable = (boolean) configUtil.getConfigValue("pvp.enable");
        this.priority = (Integer) configUtil.getConfigValue("pvp.priority");
        this.endType = (Integer) configUtil.getConfigValue("pvp.endType");
        this.maxTeamRespawnLife = (Integer) configUtil.getConfigValue("pvp.maxTeamRespawnLife");
        this.maxPlayerRespawnLife = (Integer) configUtil.getConfigValue("pvp.maxPlayerRespawnLife");
        this.randomRespawn = (boolean) configUtil.getConfigValue("pvp.randomRespawn");

        this.worldeditWorldFrom = (String) configUtil.getConfigValue("pvp.worldedit.worldFrom");
        this.worldeditWorldTo = (String) configUtil.getConfigValue("pvp.worldedit.worldTo");
        this.worldeditPointLow = (List<Integer>) configUtil.getConfigValue("pvp.worldedit.pointLow");
        this.worldeditPointHigh = (List<Integer>) configUtil.getConfigValue("pvp.worldedit.pointHigh");
        this.worldeditPointPaste = (List<Integer>) configUtil.getConfigValue("pvp.worldedit.Pointpaste");
        this.worldeditPointRespawn = (List<List<Integer>>) configUtil.getConfigValue("pvp.worldedit.pointRespawn");

        this.onStartPVPPlayer = (List<String>) configUtil.getConfigValue("pvp.event.onStartPVP.player");
        this.onStartPVPConsole = (List<String>) configUtil.getConfigValue("pvp.event.onStartPVP.console");
        this.onEndPVPPlayer = (List<String>) configUtil.getConfigValue("pvp.event.onEndPVP.player");
        this.onEndPVPConsole = (List<String>) configUtil.getConfigValue("pvp.event.onEndPVP.console");

        this.onStartPVPMessage = (String) configUtil.getConfigValue("pvp.messages.onStartPVP");
        this.onEndPVPMessage = (String) configUtil.getConfigValue("pvp.messages.onEndPVP");
        this.onPersonOutMessage = (String) configUtil.getConfigValue("pvp.messages.onPersonOut");
        this.onTeamOutMessage = (String) configUtil.getConfigValue("pvp.messages.onTeamOut");
        this.onWiningTeamMessage = (String) configUtil.getConfigValue("pvp.messages.onWiningTeam");
        this.onPersonOutReasonMaxPlayerRespawnLifeMessage = (String) configUtil.getConfigValue("pvp.messages.onPersonOutReasonMaxPlayerRespawnLife");
        this.onPersonOutMaxTeamRespawnLifeMessage = (String) configUtil.getConfigValue("pvp.messages.onPersonOutMaxTeamRespawnLife");
        this.onPersonRespawnMessage = (String) configUtil.getConfigValue("pvp.messages.onPersonRespawn");

        this.stage = (String) configUtil.getConfigValue("pvp.stage");
    }

    public boolean start() throws IOException {
        if(!isEnable){
            return false;
        }
        // 执行复制结构
        worldeditUtil = new WorldeditUtil("pvp",worldeditWorldFrom,worldeditWorldTo,worldeditPointLow,worldeditPointHigh,worldeditPointPaste,worldeditPointRespawn);
        worldeditUtil.pasteStruce();

        for (String playerName: AquaTeam.getTeam().getPlayers()){
            Player player = Bukkit.getPlayer(playerName);
            Location respawnLocation = getStructureRespawnLocation(player);
            player.setBedSpawnLocation(respawnLocation,true);
            player.teleport(respawnLocation);
        }
        // 执行启动命令，并发送消息
        for(String playername: AquaTeam.getTeam().getPlayers()){
            Player player = Bukkit.getPlayer(playername);
            onStartPVP(player);
            player.sendMessage(colorize(parsePrefix(onStartPVPMessage)));
        }


        AquaTeam.getGlobalState().setStage(this.stage);
        AquaTeam.getTeam().updateScoreboard();

        return true;
    }

    public boolean end() throws IOException {
        if (!isEnable) {
            return false;
        }

        // 执行结束命令，并发送消息
        for (String playerName : AquaTeam.getTeam().getPlayers()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                onEndPVP(player);
                player.sendMessage(colorize(parsePrefix(onEndPVPMessage)));
            }
        }

        // 清除地图
        worldeditUtil.clearStruceOfHighLowPoints();

        // 通知任务调度器，执行下一周期的命令
        AquaTeam.getPriorityTaskScheduler().executeTasks();

        return true;
    }

    public boolean onStartPVP(Player player){
        playerExecCommand(onStartPVPPlayer,player);
        consoleExecCommand(onStartPVPConsole,player);

        return true;
    }

    public boolean onEndPVP(Player player){
        playerExecCommand(onEndPVPPlayer,player);
        consoleExecCommand(onEndPVPConsole,player);

        return true;
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isEnable) {
            return;
        }

        Player player = event.getEntity();
        String playerName = player.getName();
        String teamName = AquaTeam.getTeam().getTeamName(playerName);

        if (teamName == null) {
            return; // 玩家不在任何队伍中，忽略此事件
        }

        int deathCount = (int) AquaTeam.getTeam().getPlayerState(playerName, "pvp.death") + 1;
        AquaTeam.getTeam().setPlayerState(playerName, "pvp.death", deathCount);

        if (deathCount > maxPlayerRespawnLife) {
            // 玩家已达到最大重生次数
            player.sendMessage(colorize(parsePrefix(onPersonOutReasonMaxPlayerRespawnLifeMessage)));
            msgAllPlayers(colorize(parsePrefix(parsePlayer(onPersonOutMessage,playerName) )));

            eliminatePlayer(player, playerName);
        } else {
            // 检查队伍总重生次数
            int teamDeathCount = AquaTeam.getTeam().getPlayers(teamName).stream()
                    .mapToInt(member -> (int) AquaTeam.getTeam().getPlayerState(member, "pvp.death"))
                    .sum();

            if (teamDeathCount > maxTeamRespawnLife) {
                // 队伍已达到最大重生次数
                player.sendMessage(colorize(parsePrefix(onPersonOutMaxTeamRespawnLifeMessage)));
                eliminatePlayer(player, playerName);
            } else {
                // 玩家可以重生
                respawnPlayer(player);
            }
            if (teamDeathCount > maxTeamRespawnLife + 1){
                //队伍已经全部阵亡
                msgAllPlayers(colorize(parsePrefix(parseTeam(onTeamOutMessage,player))));
            }
        }

        // 更新计分板
        AquaTeam.getTeam().updateScoreboard();
        // 检查游戏是否结束
        checkGameEnd();
    }

    private void eliminatePlayer(Player player, String playerName) {
        // 设置玩家为旁观者模式
        player.setGameMode(GameMode.SPECTATOR);
        // 更新玩家状态
        AquaTeam.getTeam().setPlayerState(playerName, "pvp.out", true);
        // 可以添加其他淘汰逻辑
    }

    private void respawnPlayer(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                // 执行重生
                player.spigot().respawn();

                // 获取并传送到重生位置
                Location respawnLocation = getStructureRespawnLocation(player);
                player.teleport(respawnLocation);

                // 发送重生消息
                player.sendMessage(colorize(parsePrefix(parsePlayerLife(parseTeamLife(onPersonRespawnMessage,AquaTeam.getTeam().getTeamName(player.getName())) ,player.getName()))));

                // 更新记分板或其他游戏状态
                AquaTeam.getTeam().updateScoreboard();
            }
        }, 1L);
    }

    private Location getStructureRespawnLocation(Player player) {
        List<List<Integer>> respawnPoints = AquaTeam.getGlobalState().getPVPStructurePointRespawn();

        if (respawnPoints == null || respawnPoints.isEmpty()) {
            // 如果没有设置重生点，返回玩家当前世界的出生点
            return player.getWorld().getSpawnLocation();
        }

        List<Integer> chosenPoint;
        if (randomRespawn) {
            // 随机选择一个重生点
            chosenPoint = respawnPoints.get(new Random().nextInt(respawnPoints.size()));
        } else {
            // 使用队伍序号选择重生点
            String teamName = AquaTeam.getTeam().getTeamName(player.getName());
            Integer teamOrder = AquaTeam.getGlobalState().getTeamOrder(teamName);

            if (teamOrder == null || teamOrder >= respawnPoints.size()) {
                // 如果队伍序号无效或超出范围，使用第一个重生点
                chosenPoint = respawnPoints.get(0);
            } else {
                chosenPoint = respawnPoints.get(teamOrder);
            }
        }

        // 直接使用选中的重生点坐标
        return new Location(player.getWorld(), chosenPoint.get(0), chosenPoint.get(1), chosenPoint.get(2));
    }
    private void checkGameEnd() {
        if (endType == 0) {
            // 检查是否有队伍的所有玩家都被淘汰
            for (String teamName : AquaTeam.getTeam().getTeams()) {
                List<String> teamPlayers = AquaTeam.getTeam().getPlayers(teamName);
                boolean allEliminated = teamPlayers.stream()
                        .allMatch(playerName -> (boolean) AquaTeam.getTeam().getPlayerState(playerName, "pvp.out"));
                if (allEliminated) {
                    endGame(getWinningTeam());
                    return;
                }
            }
        } else if (endType == 1) {
            // 检查是否只剩一个队伍有未被淘汰的玩家
            long teamsWithAlivePlayers = AquaTeam.getTeam().getTeams().stream()
                    .filter(teamName -> AquaTeam.getTeam().getPlayers(teamName).stream()
                            .anyMatch(playerName -> !(boolean) AquaTeam.getTeam().getPlayerState(playerName, "pvp.out")))
                    .count();
            if (teamsWithAlivePlayers == 1) {
                endGame(getWinningTeam());
            }
        }
    }
    public boolean checkTeamOut(String teamName) {
        List<String> teamPlayers = AquaTeam.getTeam().getPlayers(teamName);

        // 如果队伍没有玩家，我们认为它已经被淘汰
        if (teamPlayers.isEmpty()) {
            return true;
        }

        // 检查队伍中的所有玩家是否都被淘汰
        return teamPlayers.stream()
                .allMatch(playerName -> (boolean) AquaTeam.getTeam().getPlayerState(playerName, "pvp.out"));
    }

    private String getWinningTeam() {
        return AquaTeam.getTeam().getTeams().stream()
                .filter(teamName -> AquaTeam.getTeam().getPlayers(teamName).stream()
                        .anyMatch(playerName -> !(boolean) AquaTeam.getTeam().getPlayerState(playerName, "pvp.out")))
                .findFirst()
                .orElse(null);
    }

    private void endGame(String winningTeam) {
        // 实现游戏结束逻辑
        msgAllPlayers(colorize(parsePrefix(parseTeamDirect(onWiningTeamMessage,winningTeam))));
        if(AquaTeam.getGlobalState().getWiningTeam(stage)==null){
            AquaTeam.getGlobalState().setWiningTeam(stage,winningTeam);
        }
        try {
            end();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerRespawnLife(String playerName) {
        int deathCount = (int) AquaTeam.getTeam().getPlayerState(playerName, "pvp.death");
        return Math.max(0, maxPlayerRespawnLife - deathCount);
    }

    public int getTeamRespawnLife(String teamName) {
        List<String> teamPlayers = AquaTeam.getTeam().getPlayers(teamName);
        int teamDeathCount = teamPlayers.stream()
                .mapToInt(player -> (int) AquaTeam.getTeam().getPlayerState(player, "pvp.death"))
                .sum();
        return Math.max(0, maxTeamRespawnLife - teamDeathCount);
    }



}
