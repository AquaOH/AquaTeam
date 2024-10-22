package cc.aquaoh.aquaTeam.luckyblock;

import cc.aquaoh.aquaTeam.AquaTeam;
import cc.aquaoh.aquaTeam.util.ConfigUtil;
import cc.aquaoh.aquaTeam.util.WorldeditUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.consoleExecCommand;
import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.playerExecCommand;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.*;

public class LuckyBlock {

    private boolean isEnable;
    private int priority;
    private int endType;

    private String worldeditWorldFrom;
    private String worldeditWorldTo;
    private List<Integer> worldeditPointLow;
    private List<Integer> worldeditPointHigh;
    private List<Integer> worldeditPointPaste;
    private String worldeditDirection;
    private int worldeditGap;
    private List<Integer> worldeditPointRespawn;
    private float worldeditYaw;
    private float worldeditPitch;
    private List<Integer> worldeditPointEnd;

    private List<String> onStartLuckyBlockPlayer;
    private List<String> onStartLuckyBlockConsole;
    private List<String> onEndLuckyBlockPlayer;
    private List<String> onEndLuckyBlockConsole;

    private String onStartLuckyBlockMessage;
    private String onEndLuckyBlockMessage;
    private String onPersonEndLuckyBlockMessage;
    private String onTeamEndLuckyBlockMessage;
    private String onWiningTeamMessage;

    private String stage;

    private WorldeditUtil worldeditUtil;



    public LuckyBlock() {

        reloadConfig();


    }

    public void reloadConfig(){
        // 获取配置工具类实例
        ConfigUtil configUtil = AquaTeam.getConfigUtil();

        this.isEnable = (boolean) configUtil.getConfigValue("luckyblock.enable");
        this.priority = (Integer) configUtil.getConfigValue("luckyblock.priority");
        this.endType = (Integer) configUtil.getConfigValue("luckyblock.endType");
        this.worldeditWorldFrom = (String) configUtil.getConfigValue("luckyblock.worldedit.worldFrom");
        this.worldeditWorldTo = (String) configUtil.getConfigValue("luckyblock.worldedit.worldTo");
        this.worldeditPointLow = (List<Integer>) configUtil.getConfigValue("luckyblock.worldedit.pointLow");
        this.worldeditPointHigh = (List<Integer>) configUtil.getConfigValue("luckyblock.worldedit.pointHigh");
        this.worldeditPointPaste = (List<Integer>) configUtil.getConfigValue("luckyblock.worldedit.pointPaste");
        this.worldeditDirection = (String) configUtil.getConfigValue("luckyblock.worldedit.direction");
        this.worldeditGap = (Integer) configUtil.getConfigValue("luckyblock.worldedit.gap");
        this.worldeditPointRespawn = (List<Integer>) configUtil.getConfigValue("luckyblock.worldedit.pointRespawn");
        this.worldeditYaw = ((Double) configUtil.getConfigValue("luckyblock.worldedit.yaw")).floatValue();
        this.worldeditPitch = ((Double) configUtil.getConfigValue("luckyblock.worldedit.pitch")).floatValue();
        this.worldeditPointEnd = (List<Integer>) configUtil.getConfigValue("luckyblock.worldedit.pointEnd");

        this.onStartLuckyBlockPlayer = (List<String>) configUtil.getConfigValue("luckyblock.event.onStartLuckyBlock.player");
        this.onStartLuckyBlockConsole = (List<String>) configUtil.getConfigValue("luckyblock.event.onStartLuckyBlock.console");
        this.onEndLuckyBlockPlayer = (List<String>) configUtil.getConfigValue("luckyblock.event.onEndLuckyBlock.player");
        this.onEndLuckyBlockConsole = (List<String>) configUtil.getConfigValue("luckyblock.event.onEndLuckyBlockConsole");

        this.onStartLuckyBlockMessage = (String) configUtil.getConfigValue("luckyblock.messages.onStartLuckyBlock");
        this.onEndLuckyBlockMessage = (String) configUtil.getConfigValue("luckyblock.messages.onEndLuckyBlock");
        this.onTeamEndLuckyBlockMessage = (String) configUtil.getConfigValue("luckyblock.messages.onTeamEndLuckyBlock");
        this.onPersonEndLuckyBlockMessage = (String) configUtil.getConfigValue("luckyblock.messages.onPersonEndLuckyBlock");
        this.onWiningTeamMessage = (String) configUtil.getConfigValue("luckyblock.messages.onWiningTeam");


        this.stage = (String) configUtil.getConfigValue("luckyblock.stage");

    }

    public boolean start() throws IOException {
        if(!isEnable){
            //LuckyBlock模块未开启
            return false;
        }
        Bukkit.getLogger().info("AquaTeAam: 开始运行LuckyBlock");
        // 执行复制结构
        worldeditUtil = new WorldeditUtil("luckyblock",worldeditWorldFrom,worldeditWorldTo,worldeditPointLow,worldeditPointHigh,worldeditPointPaste,worldeditDirection,worldeditGap,worldeditPointRespawn,worldeditYaw,worldeditPitch,worldeditPointEnd);
        worldeditUtil.pasteStruce();

        // 执行启动命令，并发送消息
        for(String playername: AquaTeam.getTeam().getPlayers()){
            Player player = Bukkit.getPlayer(playername);
            onStartLuckyBlock(player);
            player.sendMessage(colorize(parsePrefix(onStartLuckyBlockMessage)));
        }
        AquaTeam.getGlobalState().setStage(this.stage);
        AquaTeam.getTeam().updateScoreboard();
        return true;
    }

    public boolean end() throws IOException {
        if(!isEnable){
            return false;
        }

        // 执行启动命令，并发送消息
        for(String playername: AquaTeam.getTeam().getPlayers()){
            Player player = Bukkit.getPlayer(playername);
            onEndLuckyBlock(player);
            player.sendMessage(colorize(parsePrefix(onEndLuckyBlockMessage)));
        }

        // 清除地图
        worldeditUtil.clearStruceOfHighLowPoints();

        // 通知任务调度器，执行下一周期的命令
        AquaTeam.getPriorityTaskScheduler().executeTasks();

        return true;
    }

    /*
       onStartLuckyBlock
       onEndLuckyBlock
     */

    public boolean onStartLuckyBlock(Player player) {
        playerExecCommand(onStartLuckyBlockPlayer,player);
        consoleExecCommand(onStartLuckyBlockConsole,player);

        return true;
    }

    public boolean onEndLuckyBlock(Player player) {
        playerExecCommand(onEndLuckyBlockPlayer,player);
        consoleExecCommand(onEndLuckyBlockConsole,player);

        return true;
    }



    public void OnPlayerInteract(PlayerInteractEvent event) throws IOException {
        if(!isEnable){
            return;
        }
        // 检查玩家的动作是否为右键或左键点击方块
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(!AquaTeam.getGlobalState().isGameStart()){
                return;
            }
            Block clickedBlock = event.getClickedBlock();  // 获取玩家点击的方块
            Player player = event.getPlayer();

            if (clickedBlock != null) {

                Location clickedLocation = clickedBlock.getLocation();  // 获取点击方块的位置
                int targetX = clickedLocation.getBlockX() ;
                int targetY = clickedLocation.getBlockY() ;
                int targetZ = clickedLocation.getBlockZ() ;

                String teamName = AquaTeam.getTeam().getTeamName(player.getName());

                List<Integer> endPoint = AquaTeam.getGlobalState().getLuckyBlockStrucePointEnd().get(AquaTeam.getGlobalState().getTeamOrder(teamName));


                // 检查点击的方块位置是否与指定位置相同
                if(endPoint.get(0)==targetX && endPoint.get(1)==targetY && endPoint.get(2)==targetZ) {
                    event.getPlayer().sendMessage(colorize(parsePrefix(onPersonEndLuckyBlockMessage)));  // 发送消息给玩家
                    AquaTeam.getTeam().setPlayerState(player.getName(),"luckyblock.finfish",true);
                }

                // 检测队伍内玩家是否完成比赛
                // 更新计分板
                if(isTeamFinished(teamName)){
                    msgAllPlayers(colorize(parseTeam(parsePrefix(onTeamEndLuckyBlockMessage),player)));
                    AquaTeam.getTeam().updateScoreboard();
                    if(AquaTeam.getGlobalState().getWiningTeam(stage)==null){
                        AquaTeam.getGlobalState().setWiningTeam(stage,teamName);
                        msgAllPlayers(colorize(parseTeam(parsePrefix(onWiningTeamMessage),player)));
                    }
                }



                // 检测所有玩家是否完成比赛.如果完成比赛，则复原重生点，设置玩家模式，清除地图
                if(isEverythingFinished()){
                    end();

                }


            }
        }
    }

    public boolean isEverythingFinished(){
        for(String teamName:AquaTeam.getTeam().getTeams()){
            if(!isTeamFinished(teamName)){
                return false;
            }
        }
        return true;
    }

    public boolean isTeamFinished(String teamName) {
        switch (endType) {
            case 0:
                for (String player : AquaTeam.getTeam().getPlayers(teamName)) {
                    if ((boolean) AquaTeam.getTeam().getPlayerState(player, "luckyblock.finfish")) {
                        return true;
                    }
                }
                return false;

            case 1:
                for (String player : AquaTeam.getTeam().getPlayers(teamName)) {
                    if(!(boolean) AquaTeam.getTeam().getPlayerState(player, "luckyblock.finfish")){
                        return false;
                    }
                }
                return true;
        }
        return false;
    }




}
