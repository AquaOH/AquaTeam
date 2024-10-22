package cc.aquaoh.aquaTeam.pvp.listener;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class PVPDeathListener implements Listener {


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if(!AquaTeam.getGlobalState().getStage().equals(AquaTeam.getConfigUtil().getConfigValue("pvp.stage"))){
            return;
        }
        AquaTeam.getPVP().onPlayerDeath(event);
    }


}