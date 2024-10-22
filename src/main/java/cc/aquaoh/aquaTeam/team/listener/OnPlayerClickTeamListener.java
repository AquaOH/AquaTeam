package cc.aquaoh.aquaTeam.team.listener;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.IOException;

public class OnPlayerClickTeamListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) throws IOException {
        AquaTeam.getTeam().handleMenu(event);
    }
}
