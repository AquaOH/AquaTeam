package cc.aquaoh.aquaTeam.util;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.aquaoh.aquaTeam.AquaTeam.configUtil;

public class NotifyUtil {

    public static void msgAllPlayers(String msg){
        msg = colorize(msg);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playNote(player.getLocation(), Instrument.PIANO, new Note(0, Note.Tone.C,true));   // C
            player.sendMessage(msg);
        }
    }

    public static void titleAllPlayers(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        title = colorize(title);
        subtitle = colorize(subtitle);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playNote(player.getLocation(), Instrument.PIANO, new Note(0, Note.Tone.C,true));   // C
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void timerMsgNotifyAllPlayers(String msg){
        msg = parsePrefix(msg);
        msgAllPlayers(msg);
    }

    public static void timerMsgNotifyAllPlayers(int time, String msg, List<Integer> list) {
        if(!list.contains(time)){
            return;
        }
        msg = parsePrefix(msg);
        msg = parseLeft(msg, time);
        msgAllPlayers(msg);
    }

    public static void timerTitleNotifyAllPlayers(int time, String title, String subtitle, int fadeIn, int stay, int fadeOut, List<Integer> list){
        if(!list.contains(time)){
            return;
        }
        title = parsePrefix(title);
        subtitle = parsePrefix(subtitle);

        title = parseLeft(title, time);
        subtitle = parseLeft(subtitle, time);

        titleAllPlayers(title, subtitle, fadeIn, stay, fadeOut);
    }

    public static String parseTeam(String msg,Player player){
        String teamName = AquaTeam.getTeam().getTeamName(player.getName());
        int index = AquaTeam.getGlobalState().getTeamOrder(teamName);
        List<String> displayNames = (List<String>) configUtil.getConfigValue("team.gui.teamIcon.displayNames");
        String displayName = displayNames.get(index);
        return msg.replace("{$team}", displayName);
    }

    public static String parseTeamDirect(String msg,String teamName){
        int index = AquaTeam.getGlobalState().getTeamOrder(teamName);
        List<String> displayNames = (List<String>) configUtil.getConfigValue("team.gui.teamIcon.displayNames");
        String displayName = displayNames.get(index);
        return msg.replace("{$team}", displayName);
    }




    public static String parsePrefix(String msg){
        String prefix = (String) AquaTeam.getConfigUtil().getConfigValue("prefix");

        // 使用 replace 方法将 {$prefix} 替换为 prefix 的值
        return msg.replace("{$prefix}", prefix);
    }

    public static String parseLeft(String msg, int time){

        return msg.replace("{$left}", String.valueOf(time));
    }

    public static String parsePlayer(String msg, String playerName){

        return msg.replace("{$player}", String.valueOf(playerName));
    }

    public static String parsePlayerLife(String msg, String playerName){
        String pLife = String.valueOf(AquaTeam.getPVP().getPlayerRespawnLife(playerName));
        return msg.replace("{$pLife}", pLife);
    }

    public static String parseTeamLife(String msg, String teamName){
        String tLife = String.valueOf(AquaTeam.getPVP().getTeamRespawnLife(teamName));
        return msg.replace("{$tLife}", tLife);
    }

    public static List<String> parseStage(List<String> messages){
        String stage = (String) AquaTeam.getGlobalState().getStage();
        List<String> tmp = new ArrayList<>();
        for(String message : messages){
            if(message.contains("{$stage}")){
                tmp.add(message.replace("{$stage}", stage));
            }else {
                tmp.add(message);
            }

        }
        return tmp;

    }



    public static List<String> parseScoreboardBody(String scoreboardBody,List<String> displayNames){
        List<String> bodyList = new ArrayList<>();

        for(String teamName : AquaTeam.getTeam().getTeams()) {
            String tmp = scoreboardBody;
            int index = AquaTeam.getGlobalState().getTeamOrder(teamName);
            String displayName = displayNames.get(index);
            tmp = tmp.replace("{$team}", displayName);
            tmp = tmp.replace("{$playernum}", AquaTeam.getTeam().getSizeofATeamPlayerNum(teamName)+"");
            if(AquaTeam.getGlobalState().getStage().equals(((String) configUtil.getConfigValue("team.stage")))){
                tmp = tmp.replace("{$additional}", " ");
            }else if (AquaTeam.getGlobalState().getStage().equals(((String) configUtil.getConfigValue("luckyblock.stage")))){
                if(AquaTeam.getLuckyBlock().isTeamFinished(teamName)){
                    tmp = tmp.replace("{$additional}", "&a完成");
                }else{
                    tmp = tmp.replace("{$additional}", "&c游戏中");
                }

            }else if (AquaTeam.getGlobalState().getStage().equals(((String) configUtil.getConfigValue("pvp.stage")))){
                if(AquaTeam.getPVP().checkTeamOut(teamName)){
                    tmp = tmp.replace("{$additional}", "&c淘汰");
                }else {
                    tmp = tmp.replace("{$additional}", "&a存活:"+getTeamAlive(teamName));
                }
            }
            tmp = colorize(tmp);
            bodyList.add(tmp);
        }
        return bodyList;
    }

    public static String getTeamAlive(String teamName){
        int count = 0;
        for(String playerName : AquaTeam.getTeam().getPlayers(teamName)){
            if(!(boolean)AquaTeam.getTeam().getPlayerState(playerName, "pvp.out")){
                count+=1;
            }
        }
        return String.valueOf(count);

    }






    // 支持 & 颜色符和 {#RRGGBB} 的 RGB 颜色替换
    public static String colorize(String message) {
        // 正则表达式匹配 {#RRGGBB} 的 RGB 颜色格式
        Pattern pattern = Pattern.compile("\\{#([A-Fa-f0-9]{6})\\}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        // 对每个匹配进行处理，转换为 §x§R§R§G§G§B§B 的形式
        while (matcher.find()) {
            String hex = matcher.group(1); // 获取捕获组中的 RGB 颜色值
            String replacement = "§x" +
                    "§" + hex.charAt(0) +
                    "§" + hex.charAt(1) +
                    "§" + hex.charAt(2) +
                    "§" + hex.charAt(3) +
                    "§" + hex.charAt(4) +
                    "§" + hex.charAt(5);
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        // 将&符号转换为§，以启用颜色
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    // 反向处理，将 §x§R§R§G§G§B§B 转换为 {#RRGGBB}，并将 § 转换回 &
    public static String decolorize(String message) {
        // 匹配 §x§R§R§G§G§B§B 的格式
        Pattern pattern = Pattern.compile("§x(§[A-Fa-f0-9]){6}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        // 对每个匹配进行处理，转换为 {#RRGGBB} 的形式
        while (matcher.find()) {
            String hex = matcher.group().replaceAll("§x|§", ""); // 去掉§和x，留下RGB值
            String replacement = "{#" + hex + "}";
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        // 将 § 符号转换回 &，以支持常规颜色符号
        return buffer.toString().replace('§', '&');
    }



}
