package cc.aquaoh.aquaTeam.team.listener;

import cc.aquaoh.aquaTeam.AquaTeam;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;

public class OnTABLoadTeamListener {
    private AquaTeam plugin;

    public OnTABLoadTeamListener(AquaTeam plugin) {
        this.plugin = plugin;
        //注册事件监听器
        TabAPI.getInstance().getEventBus().register(PlayerLoadEvent.class, this::onPlayerLoad);
    }

    private void onPlayerLoad(PlayerLoadEvent playerLoadEvent) {
        AquaTeam.getTeam().setPrefix(playerLoadEvent.getPlayer());

    }


}
