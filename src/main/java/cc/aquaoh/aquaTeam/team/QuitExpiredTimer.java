package cc.aquaoh.aquaTeam.team;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QuitExpiredTimer {
    private AquaTeam plugin;
    private BukkitTask countdownTaskTmp;
    private Map<String,BukkitTask> tasks;

    public QuitExpiredTimer(AquaTeam plugin) {

        this.plugin = plugin;
        tasks = new HashMap<>();
    }


    public boolean startQuitExpiredTimer(int quitExpiredTime, String playerName){
        AtomicInteger remainingTime = new AtomicInteger(quitExpiredTime);
        countdownTaskTmp = new BukkitRunnable() {

            @Override
            public void run() {
                if(remainingTime.get() <= 0){
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            AquaTeam.getTeam().removePlayer(playerName);
                            tasks.remove(playerName);
                            AquaTeam.getTeam().onQuitExpiredTime(Bukkit.getPlayer(playerName));
                        }
                    });

                    Bukkit.getScheduler().cancelTask(this.getTaskId());

                    return;
                }else {
                    remainingTime.decrementAndGet();
                }

            }
        }.runTaskTimerAsynchronously(plugin,0L,20L);
        tasks.put(playerName,countdownTaskTmp);

        return true;
    }

    public void cancelQuitExpiredTimer(String playerName){
        BukkitTask task = tasks.get(playerName);
        Bukkit.getScheduler().cancelTask(task.getTaskId());
        if(task != null){
            task.cancel();
            tasks.remove(playerName);
        }
    }
}

