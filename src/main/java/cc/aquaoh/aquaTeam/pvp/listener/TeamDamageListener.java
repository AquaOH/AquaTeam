package cc.aquaoh.aquaTeam.pvp.listener;

import cc.aquaoh.aquaTeam.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static cc.aquaoh.aquaTeam.AquaTeam.team;

public class TeamDamageListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            // 检查两个玩家是否都在队伍中
            if (team.isPlayerInTeam(damager.getName()) && team.isPlayerInTeam(victim.getName())) {
                // 如果两个玩家在同一个队伍中，取消伤害
                if (team.arePlayersInSameTeam(damager.getName(), victim.getName())) {
                    event.setCancelled(true);
                }
                // 如果玩家不在同一个队伍中，允许伤害
            } else {
                // 如果其中一个玩家不在任何队伍中，允许伤害
                // 这里可以根据你的游戏规则来决定是否允许无队伍玩家参与战斗
            }
        }
    }
}