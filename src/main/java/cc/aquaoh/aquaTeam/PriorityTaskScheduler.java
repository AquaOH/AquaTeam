package cc.aquaoh.aquaTeam;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Comparator;

public class PriorityTaskScheduler {
    private PriorityQueue<Task> taskQueue;
    private int luckyblockPriority;
    private int pvpPriority;
    private AquaTeam plugin;

    public PriorityTaskScheduler(AquaTeam plugin) {
        this.luckyblockPriority = (int) AquaTeam.getConfigUtil().getConfigValue("luckyblock.priority");
        this.pvpPriority = (int) AquaTeam.getConfigUtil().getConfigValue("pvp.priority");
        this.taskQueue = new PriorityQueue<>(Comparator.comparingInt(Task::getPriority));
        this.plugin = plugin;
        initializeTasks();
    }


    private void initializeTasks() {
        // LuckyblockTask
        addTask(new Task(luckyblockPriority) {
            @Override
            public void run() {
                Bukkit.getLogger().info("Executing LuckyblockTask,Luckyblock priority: " + luckyblockPriority);

                // 运行逻辑
                try {
                    AquaTeam.getLuckyBlock().start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        // PvPTask
        addTask(new Task(pvpPriority) {
            @Override
            public void run() {
                System.out.println("Executing PvPTask");
                // Add your PvPTask logic here
                // 运行逻辑
                try {
                    AquaTeam.getPVP().start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void addTask(Task task) {
        taskQueue.offer(task);
    }

    public boolean executeTasks() {
        if (!taskQueue.isEmpty()) {
            Bukkit.getLogger().info("Executing tasks before"+taskQueue.toString());
            Task task = taskQueue.poll();
            Bukkit.getLogger().info("Executing tasks after"+taskQueue.toString());
            task.runTask(plugin);
            return true;
        }else {
            Bukkit.getLogger().info("Executing final"+taskQueue.toString());
            //收尾工作
            AquaTeam.getFinish().reward();
            //初始化，开启再次匹配

            AquaTeam.getGlobalState().initGlobalState();
            AquaTeam.getTimer().resumeTimer();
            AquaTeam.getTeam().initTeam();
            updatePriorities();
            AquaTeam.getTeam().updateScoreboard();
            for (Player player : Bukkit.getOnlinePlayers()) {
                AquaTeam.getTeam().onWaitJoin(player);
                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getName());
                AquaTeam.getTeam().setPrefix(tabPlayer);
            }
            return false;
        }
    }

    public static abstract class Task extends BukkitRunnable {
        private int priority;

        public Task(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    // Method to update priorities and reinitialize tasks
    public void updatePriorities() {
        this.luckyblockPriority = (int) AquaTeam.getConfigUtil().getConfigValue("luckyblock.priority");
        this.pvpPriority = (int) AquaTeam.getConfigUtil().getConfigValue("pvp.priority");
        taskQueue.clear();
        initializeTasks();
    }
}
