package cc.aquaoh.aquaTeam.luckyblock.listener;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 500; // 500 milliseconds cooldown

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws IOException {
        if(!AquaTeam.getGlobalState().getStage().equals(AquaTeam.getConfigUtil().getConfigValue("luckyblock.stage"))){
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(playerUUID) && currentTime - cooldowns.get(playerUUID) < COOLDOWN_TIME) {
            // Still in cooldown, ignore this event
            return;
        }

        // Update the cooldown time
        cooldowns.put(playerUUID, currentTime);

        // Process the event
        AquaTeam.getLuckyBlock().OnPlayerInteract(event);
    }
}