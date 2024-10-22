package cc.aquaoh.aquaTeam;

import cc.aquaoh.aquaTeam.command.Commands;
import cc.aquaoh.aquaTeam.command.CommandsTabCompleter;
import cc.aquaoh.aquaTeam.luckyblock.LuckyBlock;
import cc.aquaoh.aquaTeam.luckyblock.listener.PlayerInteractListener;
import cc.aquaoh.aquaTeam.pvp.PVP;
import cc.aquaoh.aquaTeam.pvp.listener.PVPDeathListener;
import cc.aquaoh.aquaTeam.pvp.listener.TeamDamageListener;
import cc.aquaoh.aquaTeam.team.listener.OnPlayerClickTeamListener;
import cc.aquaoh.aquaTeam.team.listener.OnPlayerJoinTeamListener;
import cc.aquaoh.aquaTeam.team.listener.OnPlayerQuitTeamListener;
import cc.aquaoh.aquaTeam.team.listener.OnTABLoadTeamListener;
import cc.aquaoh.aquaTeam.timer.Timer;
import cc.aquaoh.aquaTeam.timer.listener.OnPlayerJoinTimerListener;
import cc.aquaoh.aquaTeam.timer.listener.OnPlayerQuitTimerListener;
import cc.aquaoh.aquaTeam.util.ConfigUtil;
import cc.aquaoh.aquaTeam.team.Team;
import cc.aquaoh.aquaTeam.util.GlobalState;
import cc.aquaoh.aquaTeam.util.scoreboard.ScoreBoardUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AquaTeam extends JavaPlugin {
    public static ConfigUtil configUtil;
    public static Timer timer;
    public static Team team;
    private static GlobalState globalState;
    public static LuckyBlock luckyBlock;
    public static PriorityTaskScheduler priorityTaskScheduler;
    public static PVP pvp;
    public static ScoreBoardUtil scoreBoardUtil;
    public static Finish finish;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig(); // 保存默认的config.yml文件（如果不存在）

        configUtil = new ConfigUtil(this);
        configUtil.readAllConfigKeys(); // 自动读取所有键值对并存入哈希表
        globalState = new GlobalState();
        globalState.initGlobalState();


        // 启用倒计时
        timer = new Timer(this);
        // 启用队伍管理
        team = new Team(this);
        // 启动任务调度
        priorityTaskScheduler = new PriorityTaskScheduler(this);
        // 启动幸运方块模块
        luckyBlock = new LuckyBlock();
        // 启动PVP模块
        pvp = new PVP(this);
        // 启动计分板
        scoreBoardUtil = new ScoreBoardUtil();
        // 启动奖励程序
        finish = new Finish();


        // 注册监听器
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinTimerListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerQuitTimerListener(),this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinTeamListener(),this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerQuitTeamListener(),this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerClickTeamListener(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(),this);
        Bukkit.getPluginManager().registerEvents(new PVPDeathListener(),this);
        Bukkit.getPluginManager().registerEvents(new TeamDamageListener(),this);

        // 注册Tab插件事件监听器
        new OnTABLoadTeamListener(this);  // 实例化事件监听器


        // 注册命令
        this.getCommand("aquateam").setExecutor(new Commands());
        // 注册TAB补全
        this.getCommand("aquateam").setTabCompleter(new CommandsTabCompleter());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public static GlobalState getGlobalState(){return globalState;}

    public static Timer getTimer() {
        return timer;
    }

    public static Team getTeam() {return team;}

    public static PriorityTaskScheduler getPriorityTaskScheduler() {
        return priorityTaskScheduler;
    }

    public static LuckyBlock getLuckyBlock() {
        return luckyBlock;
    }

    public static PVP getPVP() {
        return pvp;
    }
    public static ScoreBoardUtil getScoreBoardUtil(){
        return scoreBoardUtil;
    }
    public static Finish getFinish() {
        return finish;
    }
    public static FileConfiguration getConfiguration() {

        return getConfiguration();
    }

}
