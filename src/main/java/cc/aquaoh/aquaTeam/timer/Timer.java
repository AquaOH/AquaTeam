package cc.aquaoh.aquaTeam.timer;

import cc.aquaoh.aquaTeam.AquaTeam;
import cc.aquaoh.aquaTeam.util.ConfigUtil;
import cc.aquaoh.aquaTeam.util.NotifyUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.*;
import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.playerAllExecCommand;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.timerMsgNotifyAllPlayers;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.timerTitleNotifyAllPlayers;

public class Timer {
    private boolean isEnabled;
    private int totalTime;
    private boolean isEmptyStop;
    private boolean isLoop;
    private List<Integer> chatTipsTime;
    private List<Integer> titleTipsTime;

    private List<String> onTimeCountdownInPlayer;
    private List<String> onTimeCountdownInConsole;
    private List<String> onTimeCountdownOutPlayer;
    private List<String> onTimeCountdownOutConsole;

    private String onPlayerJoinSuccessMessage;
    private String onPlayerJoinFailMessage;
    private String onPlayerQuitFailMessage;
    private String onTimeCountdownMessage;
    private String chatTips;
    private String titleTipsTitle;
    private String titleTipsSubtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    private ConfigUtil configUtil;
    private AquaTeam plugin;

    private int maxPlayerPerTeam;
    private final Object lock = new Object();
    private boolean isPaused = false;
    private BukkitTask countdownTask;  // 用于保存倒计时任务的引用
    private BukkitTask countdownTaskTmp;

    private boolean isStarted = false; //记录是否已经开启倒计时

    public Timer(AquaTeam plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig(){
        this.configUtil = AquaTeam.getConfigUtil();
        this.isEnabled = (boolean) configUtil.getConfigValue("timer.enable");
        this.totalTime = (int) configUtil.getConfigValue("timer.totalTime");
        this.isEmptyStop = (boolean) configUtil.getConfigValue("timer.emptyStop");
        this.isLoop = (boolean) configUtil.getConfigValue("timer.loop");
        this.chatTipsTime = (List<Integer>) configUtil.getConfigValue("timer.chatTipsTime");
        this.titleTipsTime = (List<Integer>) configUtil.getConfigValue("timer.titleTipsTime");

        this.onPlayerJoinSuccessMessage = (String) configUtil.getConfigValue("timer.messages.onPlayerJoin.success");
        this.onPlayerJoinFailMessage =  (String) configUtil.getConfigValue("timer.messages.onPlayerJoin.fail");
        this.onPlayerQuitFailMessage =  (String) configUtil.getConfigValue("timer.messages.onPlayerQuit.fail");
        this.onTimeCountdownMessage = (String) configUtil.getConfigValue("timer.messages.onTimeCountdown");
        this.chatTips = (String) configUtil.getConfigValue("timer.messages.chatTips");
        this.titleTipsTitle = (String) configUtil.getConfigValue("timer.messages.titleTips.title");
        this.titleTipsSubtitle = (String) configUtil.getConfigValue("timer.messages.titleTips.subtitle");
        this.fadeIn = (int) configUtil.getConfigValue("timer.messages.titleTips.fadeIn");
        this.stay = (int) configUtil.getConfigValue("timer.messages.titleTips.stay");
        this.fadeOut = (int) configUtil.getConfigValue("timer.messages.titleTips.fadeOut");

        this.maxPlayerPerTeam = (int) configUtil.getConfigValue("team.maxPlayerPerTeam");

        this.onTimeCountdownInPlayer = (List<String>) configUtil.getConfigValue("timer.event.onTimeCountdown.in.player");
        this.onTimeCountdownInConsole = (List<String>) configUtil.getConfigValue("timer.event.onTimeCountdown.in.console");
        this.onTimeCountdownOutPlayer = (List<String>) configUtil.getConfigValue("timer.event.onTimeCountdown.out.player");
        this.onTimeCountdownOutPlayer = (List<String>) configUtil.getConfigValue("timer.event.onTimeCountdown.out.console");
    }

    public boolean startCountdown(){
        AtomicInteger remainingTime = new AtomicInteger(totalTime);

        if(!isEnabled){
            return false;
        }

        int playerNum = Bukkit.getOnlinePlayers().size();


        countdownTaskTmp = new BukkitRunnable() {
            @Override
            public void run() {
                if(remainingTime.get() <= 0){

                    Bukkit.getLogger().info("AquaTeam Timer"+ remainingTime.get() + " seconds remaining");

                    NotifyUtil.timerMsgNotifyAllPlayers(onTimeCountdownMessage);

                    //执行event任务，回到主线程执行，可以调用BukkitAPI
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        onTimeCountdown();
                    });

                    // 调整GlobalState为游戏已开始
                    AquaTeam.getGlobalState().setGameStart(true);

                    // 调用任务队列
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        AquaTeam.getPriorityTaskScheduler().executeTasks();
                    });

                    this.cancel();


                } else {
                    remainingTime.decrementAndGet();
                    timerMsgNotifyAllPlayers(remainingTime.get(),chatTips,chatTipsTime); //未来要的通知模块
                    timerTitleNotifyAllPlayers(remainingTime.get(),titleTipsTitle,titleTipsSubtitle,fadeIn,stay,fadeOut,titleTipsTime); //未来要的通知模块

                }

            }
        }.runTaskTimerAsynchronously(plugin,0L,20L);
        // 未达到启动标准
        if(playerNum < maxPlayerPerTeam){
            // 通知未达到启动标准
            NotifyUtil.timerMsgNotifyAllPlayers(onPlayerJoinFailMessage);
            countdownTaskTmp.cancel();
            return false;
        }

        // 通知达到启动标准
        NotifyUtil.timerMsgNotifyAllPlayers(onPlayerJoinSuccessMessage);

        // 达到启动标准，但是已经启动了一个倒计时线程
        if(isStarted){
            countdownTaskTmp.cancel();

            return false;
        }

        // 如果顺利启动，持久化保存任务引用
        countdownTask = countdownTaskTmp;
        // 如果启动成功，标记
        isStarted = true;

        return true;
    }


    // 用来给玩家退出时检测是否人数又下降到小于maxPlayerPerTeam
    public boolean cancelCountdown() {
        // 模块没启动就直接返回
        if(!isEnabled){
            return false;
        }
        int playerNum = Bukkit.getOnlinePlayers().size()-1;

        // 达到启动标准
        if(playerNum >= maxPlayerPerTeam){
            return false;   //达标，不取消
        }
        // 未达到启动标准
        timerMsgNotifyAllPlayers(onPlayerQuitFailMessage);
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.cancel();  // 取消任务
            countdownTask = null;    // 重置任务引用，避免重复取消
            isStarted = false;      // 重置为未启动标记位
            return true;             // 取消成功
        }

        return false;
    }

    /*
        event-OnTimeCountdown
     */
    public boolean onTimeCountdown(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(AquaTeam.getTeam().isPlayerInTeam(player.getName())){
                playerExecCommand(onTimeCountdownInPlayer,player);
                consoleExecCommand(onTimeCountdownInConsole,player);
            }else {
                playerExecCommand(onTimeCountdownOutPlayer,player);
                consoleExecCommand(onTimeCountdownOutConsole,player);
            }
        }
        return true;
    }

    // 唤醒
    public void resumeTimer() {
        if(isLoop){
            isStarted = false;
            countdownTask = null;
            startCountdown();
        }


    }

}
