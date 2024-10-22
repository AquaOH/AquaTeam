package cc.aquaoh.aquaTeam.timer.listener;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinTimerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(AquaTeam.getGlobalState().isGameStart()){
            // 游戏已启动就什么都不做
            return;
        }
        AquaTeam.getTimer().startCountdown();

    }
}
