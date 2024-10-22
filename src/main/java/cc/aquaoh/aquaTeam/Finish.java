package cc.aquaoh.aquaTeam;

import cc.aquaoh.aquaTeam.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.*;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.*;

public class Finish {

    private boolean finishRewardsLuckyblockEnable;
    private List<String> finishRewardsLuckyblockWiningTeam;
    private List<String> finishRewardsLuckyblockOthers;

    private boolean finishRewardsPvpEnable;
    private List<String> finishRewardsPvpWiningTeam;
    private List<String> finishRewardsPvpOthers;

    private String finishMessagesOnGameFinish;


    public Finish() {
        reloadConfig();
    }

    public void reloadConfig() {
        ConfigUtil configUtil = AquaTeam.getConfigUtil();


        this.finishRewardsLuckyblockEnable = (boolean) configUtil.getConfigValue("finish.rewards.luckyblock.enable");
        this.finishRewardsLuckyblockWiningTeam = (List<String>) configUtil.getConfigValue("finish.rewards.luckyblock.winingTeam");
        this.finishRewardsLuckyblockOthers = (List<String>) configUtil.getConfigValue("finish.rewards.luckyblock.others");

        this.finishRewardsPvpEnable = (boolean) configUtil.getConfigValue("finish.rewards.pvp.enable");
        this.finishRewardsPvpWiningTeam = (List<String>) configUtil.getConfigValue("finish.rewards.pvp.winingTeam");
        this.finishRewardsPvpOthers = (List<String>) configUtil.getConfigValue("finish.rewards.pvp.others");

        this.finishMessagesOnGameFinish = (String) configUtil.getConfigValue("finish.messages.onGameFinish");
    }

    public void reward(){
        msgAllPlayers(colorize(parsePrefix(parseTeamDirect(finishMessagesOnGameFinish,AquaTeam.getGlobalState().getWiningTeam(AquaTeam.getGlobalState().getStage())))));


        for(String player: AquaTeam.getTeam().getPlayers(AquaTeam.getGlobalState().getWiningTeam(AquaTeam.getGlobalState().getStage()))){
            if(finishRewardsLuckyblockEnable){
                consoleExecCommand(finishRewardsLuckyblockWiningTeam, Bukkit.getPlayer(player));
            }
            if(finishRewardsPvpEnable){
                consoleExecCommand(finishRewardsPvpWiningTeam, Bukkit.getPlayer(player));
            }
        }

        for(String player: AquaTeam.getTeam().getPlayers()){
            if( !AquaTeam.getTeam().getPlayers(AquaTeam.getGlobalState().getWiningTeam(AquaTeam.getGlobalState().getStage())).contains(player)){
                if(finishRewardsLuckyblockEnable){
                    consoleExecCommand(finishRewardsLuckyblockOthers, Bukkit.getPlayer(player));
                }
                if(finishRewardsPvpEnable){
                    consoleExecCommand(finishRewardsPvpOthers, Bukkit.getPlayer(player));
                }
            }
        }


    }
}