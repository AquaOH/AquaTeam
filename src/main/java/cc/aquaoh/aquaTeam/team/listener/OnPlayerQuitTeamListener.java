package cc.aquaoh.aquaTeam.team.listener;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitTeamListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AquaTeam.getScoreBoardUtil().removeScoreBoard(player);
        if (AquaTeam.getTeam().isPlayerInTeam(player.getName())) {
            AquaTeam.getTeam().startQuitTimer(event.getPlayer().getName());
        }
    }
}
