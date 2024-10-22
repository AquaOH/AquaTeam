package cc.aquaoh.aquaTeam.util;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.aquaoh.aquaTeam.AquaTeam.configUtil;

public class GlobalState {
    private static Map<String,Object> globalState;

    public GlobalState() {
        globalState = new HashMap<String,Object>();
    }

    public void initGlobalState(){
        globalState.clear();
        globalState.put("isGameStart",false);
        Map<String,Integer> teamOrder = new HashMap<>();
        teamOrder.put("RED",0);
        teamOrder.put("GREEN",1);
        teamOrder.put("BLUE",2);
        teamOrder.put("YELLOW",3);
        teamOrder.put("CYAN",4);
        teamOrder.put("MAGENTA",5);
        teamOrder.put("ORANGE",6);
        teamOrder.put("PINK",7);
        teamOrder.put("PURPLE",8);
        teamOrder.put("BROWN",9);
        teamOrder.put("LIGHT_BLUE",10);
        teamOrder.put("LIME",11);
        teamOrder.put("WHITE",12);
        teamOrder.put("LIGHT_GRAY",13);
        teamOrder.put("GRAY",14);
        globalState.put("teamOrder",teamOrder);
        globalState.put("stage",((String) configUtil.getConfigValue("team.stage")));
        Map<String,String> winingTeams = new HashMap<>();
        globalState.put("winingTeams",winingTeams);
    }



    public boolean setGlobalState(String key,Object value){
        globalState.put(key,value);
        return true;
    }

    public Object getGlobalState(String key){
        if(globalState.containsKey(key)){
            return globalState.get(key);
        }
        return null;
    }


    public boolean isGameStart(){
        return (boolean)globalState.get("isGameStart");
    }

    public void setGameStart(boolean isGameStart){
        globalState.put("isGameStart",isGameStart);
    }

    public Integer getTeamOrder(String teamName){
        Map<String,Integer> teamOrder = (Map<String, Integer>) globalState.get("teamOrder");

        return teamOrder.get(teamName);
    }

    public String getTeamNameByOrder(Integer order) {
        Map<String, Integer> teamOrder = (Map<String, Integer>) globalState.get("teamOrder");

        for (Map.Entry<String, Integer> entry : teamOrder.entrySet()) {
            if (entry.getValue().equals(order)) {
                return entry.getKey(); // 返回匹配的键（teamName）
            }
        }

        return null; // 如果没有找到匹配的值，返回 null
    }
    public void setStage(String stage){
        globalState.put("stage",stage);
    }

    public String getStage(){
        return (String) globalState.get("stage");
    }

    // 完成队伍
    public void setWiningTeam(String stage,String winingTeam){

        Map<String,String> winingTeams = (Map<String, String>) globalState.get("winingTeams");
        winingTeams.put(stage,winingTeam);
    }

    public String getWiningTeam(String stage){
        Map<String,String> winingTeams = (Map<String, String>) globalState.get("winingTeams");
        if(winingTeams.get(stage)== null){
            return null;
        }else {
            return winingTeams.get(stage);
        }
    }

    // LuckyBlock部分

    public void setLuckyBlockStrucePointLow(List<Integer> luckyBlockStrucePointLow){
        globalState.put("luckyBlockStrucePointLow",luckyBlockStrucePointLow);
    }
    public List<Integer> getLuckyBlockStrucePointLow(){
        if(globalState.get("luckyBlockStrucePointLow") != null){
            return (List<Integer>) globalState.get("luckyBlockStrucePointLow");
        }
        return null;
    }

    public void setLuckyBlockStrucePointHigh(List<Integer> luckyBlockStrucePointHigh){
        globalState.put("luckyBlockStrucePointHigh",luckyBlockStrucePointHigh);
    }

    public List<Integer> getLuckyBlockStrucePointHigh(){
        if(globalState.get("luckyBlockStrucePointHigh") != null){
            return (List<Integer>) globalState.get("luckyBlockStrucePointHigh");
        }
        return null;
    }

    public void setLuckyBlockStrucePointRespawn(List<List<Integer>> pointRespawnList){
        globalState.put("luckyBlockStrucePointRespawn",pointRespawnList);
    }

    public List<List<Integer>> getLuckyBlockStrucePointRespawn(){
        if(globalState.get("luckyBlockStrucePointRespawn") != null){
            return (List<List<Integer>>) globalState.get("luckyBlockStrucePointRespawn");
        }
        return null;
    }

    public void setLuckyBlockStrucePointEnd(List<List<Integer>> pointEndList){
        globalState.put("luckyBlockStrucePointEnd",pointEndList);
    }
    public List<List<Integer>> getLuckyBlockStrucePointEnd(){
        if(globalState.get("luckyBlockStrucePointEnd") != null){
            return (List<List<Integer>>) globalState.get("luckyBlockStrucePointEnd");
        }
        return null;
    }

    //PVP部分
    public void setPVPStructurePointLow(List<Integer> pvpStructurePointLow) {
        globalState.put("pvpStructurePointLow", pvpStructurePointLow);
    }

    public List<Integer> getPVPStructurePointLow() {
        if (globalState.get("pvpStructurePointLow") != null) {
            return (List<Integer>) globalState.get("pvpStructurePointLow");
        }
        return null;
    }

    public void setPVPStructurePointHigh(List<Integer> pvpStructurePointHigh) {
        globalState.put("pvpStructurePointHigh", pvpStructurePointHigh);
    }

    public List<Integer> getPVPStructurePointHigh() {
        if (globalState.get("pvpStructurePointHigh") != null) {
            return (List<Integer>) globalState.get("pvpStructurePointHigh");
        }
        return null;
    }

    public void setPVPStructurePointRespawn(List<List<Integer>> pointRespawnList) {
        globalState.put("pvpStructurePointRespawn", pointRespawnList);
    }

    public List<List<Integer>> getPVPStructurePointRespawn() {
        if (globalState.get("pvpStructurePointRespawn") != null) {
            return (List<List<Integer>>) globalState.get("pvpStructurePointRespawn");
        }
        return null;
    }

}
