package cc.aquaoh.aquaTeam.util.scoreboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreBoardUtil {

    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public void createScoreBoard(Player player) {
        FastBoard board = new FastBoard(player);
        this.boards.put(player.getUniqueId(), board);
    }

    public void removeScoreBoard(Player player) {
        boards.remove(player.getUniqueId());
    }
    public FastBoard getBoard(Player player) {
        if (boards.containsKey(player.getUniqueId())) {
            return boards.get(player.getUniqueId());
        }
        return null;
    }

    public void editAllScoreBoards(String title, List<String> lines) {
        for (FastBoard board : boards.values()) {
            board.updateTitle(title);
            board.updateLines(lines);
        }
    }

    public void editScoreBoard(Player player, String title, List<String> lines) {
        FastBoard board = boards.get(player.getUniqueId());
        board.updateTitle(title);
        board.updateLines(lines);
    }

}
