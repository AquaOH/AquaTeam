package cc.aquaoh.aquaTeam.util;

import cc.aquaoh.aquaTeam.AquaTeam;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderExpansionUtil extends PlaceholderExpansion {

    private final AquaTeam plugin;
    public PlaceholderExpansionUtil(AquaTeam plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "aquateam";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AquaOH";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] parts = params.split("_");
        switch (parts[0]) {
            case "left":
                if(parts.length == 1) {
                    return String.valueOf(AquaTeam.getTeam().getPlayers().size());
                }
                if (parts.length == 2) {
                    return String.valueOf(AquaTeam.getTeam().getPlayers(parts[1].toUpperCase()).size());
                }
            case "stage":
                return AquaTeam.getGlobalState().getStage();


        }
        return null;
    }

}
