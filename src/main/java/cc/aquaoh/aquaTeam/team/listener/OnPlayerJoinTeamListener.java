package cc.aquaoh.aquaTeam.team.listener;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinTeamListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AquaTeam.getTeam().setScoreboard(player);
        AquaTeam.getTeam().updateScoreboard(player);


        if(AquaTeam.getTeam().isPlayerInTeam(player.getName())){
            AquaTeam.getTeam().getQuitExpiredTimer().cancelQuitExpiredTimer(player.getName());
            return;
        }
        if(AquaTeam.getGlobalState().isGameStart()){
            // 游戏已启动就运行MidtermJoin
            AquaTeam.getTeam().onMidtermJoin(player);
        }else {
            AquaTeam.getTeam().onWaitJoin(player);
        }

    }

}
