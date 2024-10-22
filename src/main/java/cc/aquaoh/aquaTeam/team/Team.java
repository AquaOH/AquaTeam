package cc.aquaoh.aquaTeam.team;

import cc.aquaoh.aquaTeam.AquaTeam;
import cc.aquaoh.aquaTeam.util.ConfigUtil;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.UnaryOperator;

import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.consoleExecCommand;
import static cc.aquaoh.aquaTeam.util.ExecCommandUtil.playerExecCommand;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.*;
import static cc.aquaoh.aquaTeam.util.NotifyUtil.colorize;


public class Team {
    private Map<String, Map<String,Map<String,Object>>> teamMap = new HashMap<>();
    private AquaTeam plugin;

    private boolean isEnable;
    private int maxTeam;
    private int maxPlayerPerTeam;
    private int quitExpiredTime;
    private String freePrefix;
    private boolean isMidtermJoin;

    private List<String> onPlayerChooseTeamPlayer;
    private List<String> onPlayerChooseTeamConsole;
    private List<String> onPlayerChooseTeamErrorPlayer;
    private List<String> onPlayerChooseTeamErrorConsole;
    private List<String> onQuitExpiredTimePlayer;
    private List<String> onQuitExpiredTimeConsole;
    private List<String> onWaitJoinInPlayer;
    private List<String> onWaitJoinInConsole;
    private List<String> onWaitJoinOutPlayer;
    private List<String> onWaitJoinOutConsole;
    private List<String> onMidtermJoinInPlayer;
    private List<String> onMidtermJoinInConsole;
    private List<String> onMidtermJoinOutPlayer;
    private List<String> onMidtermJoinOutConsole;

    // GUI菜单相关参数
    private String guiTitle;
    private int guiRows;
    private List<String> guiTeamIconMaterial;
    private List<String> guiTeamIconDisplayNames;
    private List<String> guiTeamIconLore;
    private List<Integer> guiTeamIconIndex;
    private String guiFillIconMaterial;
    private String guiFillIconDisplayName;
    private String guiFillIconLore;

    // 计分板相关参数
    private boolean scoreboardEnable;
    private List<String> scoreboardHead;
    private String scoreboardTitle;
    private String scoreboardBody;
    private List<String> scoreboardFoot;

    // 信息通知相关
    private String openMenuFailedMessage;
    private String onPlayerChooseTeamMessage;
    private String onPlayerChooseTeamErrorMessage;
    private String onQuitExpiredTimeMessage;
    private String onWaitJoinMessage;
    private String onMidtermJoinMessage;

    private String stage;

    private boolean friendlyFire;

    // 子模块
    private QuitExpiredTimer quitExpiredTimer;

    public Team(AquaTeam plugin) {

        this.plugin = plugin;

        reloadConfig();

    }

    public void reloadConfig() {
        // 清空 TeamMap 以确保重新加载配置
        teamMap.clear();

        // 获取配置工具类实例
        ConfigUtil configUtil = AquaTeam.getConfigUtil();

        // 注册子模块
        quitExpiredTimer = new QuitExpiredTimer(plugin);

        // 从 team 根节点读取相关配置
        this.isEnable = (boolean) configUtil.getConfigValue("team.enable");
        this.maxTeam = (int) configUtil.getConfigValue("team.maxTeam");
        this.maxPlayerPerTeam = (int) configUtil.getConfigValue("team.maxPlayerPerTeam");
        this.quitExpiredTime = (int) configUtil.getConfigValue("team.quitExpiredTime");
        this.freePrefix = (String) configUtil.getConfigValue("team.freePrefix");
        this.isMidtermJoin = (boolean) configUtil.getConfigValue("team.midtermJoin");

        // 与事件关联的命令
        this.onPlayerChooseTeamPlayer = (List<String>) configUtil.getConfigValue("team.event.onPlayerChooseTeam.player");
        this.onPlayerChooseTeamConsole = (List<String>) configUtil.getConfigValue("team.event.onPlayerChooseTeam.console");
        this.onPlayerChooseTeamErrorPlayer = (List<String>) configUtil.getConfigValue("team.event.onPlayerChooseTeamError.player");
        this.onPlayerChooseTeamErrorConsole = (List<String>) configUtil.getConfigValue("team.event.onPlayerChooseTeamError.console");
        this.onQuitExpiredTimePlayer = (List<String>) configUtil.getConfigValue("team.event.onQuitExpiredTime.player");
        this.onQuitExpiredTimeConsole = (List<String>) configUtil.getConfigValue("team.event.onQuitExpiredTime.console");
        this.onWaitJoinInPlayer = (List<String>) configUtil.getConfigValue("team.event.onWaitJoin.in.player");
        this.onWaitJoinInConsole = (List<String>) configUtil.getConfigValue("team.event.onWaitJoin.in.console");
        this.onMidtermJoinInPlayer = (List<String>) configUtil.getConfigValue("team.event.onMidtermJoin.in.player");
        this.onMidtermJoinInConsole = (List<String>) configUtil.getConfigValue("team.event.onMidtermJoin.in.console");

        this.onWaitJoinOutPlayer = (List<String>) configUtil.getConfigValue("team.event.onWaitJoin.out.player");
        this.onWaitJoinOutConsole = (List<String>) configUtil.getConfigValue("team.event.onWaitJoin.out.console");
        this.onMidtermJoinOutPlayer = (List<String>) configUtil.getConfigValue("team.event.onMidtermJoin.out.player");
        this.onMidtermJoinOutConsole = (List<String>) configUtil.getConfigValue("team.event.onMidtermJoin.out.console");

        // GUI 菜单相关参数
        this.guiTitle = (String) configUtil.getConfigValue("team.gui.title");
        this.guiRows = (Integer) configUtil.getConfigValue("team.gui.rows");
        this.guiTeamIconMaterial = (List<String>) configUtil.getConfigValue("team.gui.teamIcon.material");
        this.guiTeamIconDisplayNames = (List<String>) configUtil.getConfigValue("team.gui.teamIcon.displayNames");
        this.guiTeamIconLore = (List<String>) configUtil.getConfigValue("team.gui.teamIcon.lore");
        this.guiTeamIconIndex = (List<Integer>) configUtil.getConfigValue("team.gui.teamIcon.index");
        this.guiFillIconMaterial = (String) configUtil.getConfigValue("team.gui.fillIcon.material");
        this.guiFillIconDisplayName = (String) configUtil.getConfigValue("team.gui.fillIcon.displayName");
        this.guiFillIconLore = (String) configUtil.getConfigValue("team.gui.fillIcon.lore");

        // 计分板相关参数
        this.scoreboardEnable = (boolean) configUtil.getConfigValue("team.scoreboard.enable");
        this.scoreboardTitle = (String) configUtil.getConfigValue("team.scoreboard.title");
        this.scoreboardHead = (List<String>) configUtil.getConfigValue("team.scoreboard.head");
        this.scoreboardBody = (String) configUtil.getConfigValue("team.scoreboard.body");
        this.scoreboardFoot = (List<String>) configUtil.getConfigValue("team.scoreboard.foot");

        // 信息通知相关
        this.openMenuFailedMessage = (String) configUtil.getConfigValue("team.messages.openMenuFailed");
        this.onPlayerChooseTeamMessage = (String) configUtil.getConfigValue("team.messages.onPlayerChooseTeam");
        this.onPlayerChooseTeamErrorMessage= (String) configUtil.getConfigValue("team.messages.onPlayerChooseTeamError");
        this.onQuitExpiredTimeMessage = (String) configUtil.getConfigValue("team.messages.onQuitExpiredTime");
        this.onWaitJoinMessage = (String) configUtil.getConfigValue("team.messages.onWaitJoin");
        this.onMidtermJoinMessage = (String) configUtil.getConfigValue("team.messages.onMidtermJoin");

        this.stage = (String) configUtil.getConfigValue("team.stage");
        this.friendlyFire = (boolean) configUtil.getConfigValue("team.friendlyFire");
    }

    public Map<String,Object> createInitStateMap(){
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put("luckyblock.finfish", false);
        stateMap.put("pvp.death", 0);
        stateMap.put("pvp.out", false);
        return stateMap;
    }

    public boolean addPlayer(String teamName, String playerName) {
        // 如果队伍不存在，并且没有超过最大队伍数量，则创建新队伍
        if (!teamMap.containsKey(teamName)) {
            if (teamMap.size() >= maxTeam) {
                return false;   // 已达到最大队伍数
            }
            teamMap.put(teamName, new HashMap<>());
        }

        Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);

        // 如果玩家不在队伍中，并且没有超过每队最大玩家数量，则添加玩家
        if (!playerMap.containsKey(playerName)) {
            if (playerMap.size() >= maxPlayerPerTeam) {
                return false; // 已达到该队伍最大玩家数
            }
            playerMap.put(playerName, createInitStateMap());
            return true;
        }

        // 如果玩家已经在队伍中
        return false;
    }

    public boolean removePlayer(String playerName) {
        for (String teamName : teamMap.keySet()) {
            Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);
            if (playerMap.containsKey(playerName)) {
                playerMap.remove(playerName);

            }
            if(playerMap.size() <= 0) {
                teamMap.remove(teamName);
            }
            return true;
        }
        return false;
    }

    public boolean removePlayer(String teamName, String playerName) {
        if (!teamMap.containsKey(teamName)) {
            return false;
        }
        Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);
        if (!playerMap.containsKey(playerName)) {
            return false;
        }
        playerMap.remove(playerName);
        return true;
    }

    public boolean isPlayerInTeam(String playerName) {
        for(String teamName : teamMap.keySet()) {
            Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);

            if(playerMap.containsKey(playerName)) {
                return true;
            }
        }
        return false;
    }

    public boolean setPlayerState(String playerName,String stateName,Object object){
        for(String teamName : teamMap.keySet()) {
            Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);

            if(playerMap.containsKey(playerName)) {
                Map<String, Object> stateMap = playerMap.get(playerName);
                stateMap.put(stateName, object);
                return true;
            }
        }
        return false;
    }

    public Object
    getPlayerState(String playerName,String stateName){
        for(String teamName : teamMap.keySet()) {
            Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);
            if(playerMap.containsKey(playerName)) {
                Map<String, Object> stateMap = playerMap.get(playerName);
                return stateMap.get(stateName);

            }
        }
        return null;
    }

    public List<String> getTeams(){
        List<String> teams = new ArrayList<>();
        for(String teamName : teamMap.keySet()) {
            teams.add(teamName);
        }
        return teams;
    }

    public boolean removeTeam(String teamName) {
        if(!teamMap.containsKey(teamName)) {
            return false;
        }
        teamMap.remove(teamName);
        return true;
    }

    public boolean clearAllTeams() {
        teamMap.clear();
        return true;
    }

    public List<String> getPlayers(String teamName) {
        if(!teamMap.containsKey(teamName)) {
            return null;
        }
        Map<String, Map<String, Object>> playerMap = teamMap.get(teamName);
        return new ArrayList<>(playerMap.keySet());
    }

    public List<String> getPlayers() {
        List<String> players = new ArrayList<>();
        // 遍历 teamMap 的每个 teamName
        for(String teamName : teamMap.keySet()) {
            // 调用 getPlayers(teamName)，并将返回的列表加入到 players 列表中
            List<String> teamPlayers = getPlayers(teamName);
            if (teamPlayers != null) {
                players.addAll(teamPlayers);
            }
        }
        return players;  // 返回所有玩家的列表
    }

    public String getTeamName(String playerName) {
        for(String teamName : teamMap.keySet()) {
            List<String> teamPlayers = getPlayers(teamName);
            if (teamPlayers.contains(playerName)) {
                return teamName;
            }
        }
        return null;
    }

    public int getSizeofTeamNum() {
        return teamMap.size();
    }

    public int getSizeofATeamPlayerNum(String teamName) {
        if (teamMap.containsKey(teamName)) {
            return teamMap.get(teamName).size();
        }
        return 0;
    }

    // 从所有队伍中随机获取一个玩家
    public String getRandomPlayerFromAllTeams() {
        List<String> players = getPlayers();
        if(!players.isEmpty()) {
            Random rand = new Random();
            rand.nextInt(players.size());
        }
        return null;
    }




    // 拓展方法部分
    public void setPrefix(TabPlayer tabPlayer){

        NameTagManager nameTagManager = TabAPI.getInstance().getNameTagManager();
        TabListFormatManager tabListFormatManager = TabAPI.getInstance().getTabListFormatManager();

        // 获取玩家对象
        Player player = Bukkit.getPlayer(tabPlayer.getName());

        // 检测玩家是否在队伍中
        if(isPlayerInTeam(tabPlayer.getName())){
            // 如果在队伍里
            String teamName = getTeamName(tabPlayer.getName());
            int teamOrder =AquaTeam.getGlobalState().getTeamOrder(teamName);
            tabListFormatManager.setPrefix(tabPlayer, guiTeamIconDisplayNames.get(teamOrder));
            nameTagManager.setPrefix(tabPlayer, guiTeamIconDisplayNames.get(teamOrder));

        } else {
            // 如果不在队伍里
            tabListFormatManager.setPrefix(tabPlayer,freePrefix);
            nameTagManager.setPrefix(tabPlayer,freePrefix);

        }
    }

    /*
        scoreboard
     */
    public void setScoreboard(Player player){
        if(!scoreboardEnable){
            return;
        }
        AquaTeam.getScoreBoardUtil().createScoreBoard(player);
    }

    public void updateScoreboard() {
        if(!scoreboardEnable){
            return;
        }

        AquaTeam.getScoreBoardUtil().editAllScoreBoards(colorize(scoreboardTitle),scoreboardBuild());

    }

    public void updateScoreboard(Player player) {
        if(!scoreboardEnable){
            return;
        }

        AquaTeam.getScoreBoardUtil().editScoreBoard(player,colorize(scoreboardTitle),scoreboardBuild());
    }


    public List<String> scoreboardBuild(){
        if(!scoreboardEnable){
            return null;
        }
        List<String> linesList = new ArrayList<>();


        for (String line : parseStage(scoreboardHead)) {
            linesList.add(colorize(line));
        }
        linesList.addAll(parseScoreboardBody(scoreboardBody,guiTeamIconDisplayNames));
        for (String line : scoreboardFoot) {
            linesList.add(colorize(line));
        }

        return linesList;
    }


    /*
        onPlayerChooseTeam
        onPlayerChooseTeamError
     */

    public boolean onPlayerChooseTeam(Player player) {
        playerExecCommand(onPlayerChooseTeamPlayer,player);
        consoleExecCommand(onPlayerChooseTeamConsole,player);

        return true;
    }

    public boolean onPlayerChooseTeamError(Player player) {
        playerExecCommand(onPlayerChooseTeamErrorPlayer,player);
        consoleExecCommand(onPlayerChooseTeamErrorConsole,player);
        return true;
    }



    /*
        QuitExpiredTimer子模块
     */


    // 返回子模块
    public QuitExpiredTimer getQuitExpiredTimer() {
        return quitExpiredTimer;
    }

    // 参数补全
    public void startQuitTimer(String playerName) {
        quitExpiredTimer.startQuitExpiredTimer(quitExpiredTime,playerName);
    }


    // 玩家退出事件过长后的通知与执行
    public boolean onQuitExpiredTime(Player player){

        playerExecCommand(onQuitExpiredTimePlayer,player);
        consoleExecCommand(onQuitExpiredTimeConsole,player);

        msgAllPlayers(parseTeam(parsePrefix(colorize(onQuitExpiredTimeMessage)),player) );

        return true;
    }

    /*
        onWaitJoin
        onMidtermJoin
     */

    public void onWaitJoin(Player player){

            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            player.setBedSpawnLocation(Bukkit.getWorld("world").getSpawnLocation(),true);
            player.sendMessage(colorize(parsePrefix(onWaitJoinMessage)));
            if(isPlayerInTeam(player.getName())){

                playerExecCommand(onWaitJoinInPlayer,player);
                consoleExecCommand(onWaitJoinInConsole,player);
            }else {
                playerExecCommand(onWaitJoinOutPlayer,player);
                consoleExecCommand(onWaitJoinOutConsole,player);
            }

    }

    public boolean onMidtermJoin(Player player){
        if(AquaTeam.getGlobalState().isGameStart()){
            // 游戏未开始，什么都不做
            return false;
        }
        player.sendMessage(colorize(parsePrefix(onMidtermJoinMessage)));
        if(isPlayerInTeam(player.getName())){
            playerExecCommand(onMidtermJoinInPlayer,player);
            consoleExecCommand(onMidtermJoinInConsole,player);
        }else{
            playerExecCommand(onMidtermJoinOutPlayer,player);
            consoleExecCommand(onMidtermJoinOutConsole,player);
            if(isMidtermJoin){
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(Bukkit.getPlayer(getRandomPlayerFromAllTeams()));
                return true;
            }

            // 传送到主城
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        return true;

    }

    /*
        Menu 相关
     */

    public boolean buildMenu(Player player){
        // 如果Team功能没打开
        if(!isEnable){
            player.sendMessage(colorize(parsePrefix(openMenuFailedMessage)));
            return false;
        }
        // 游戏已经开始，不打开
        if(AquaTeam.getGlobalState().isGameStart()){
            player.sendMessage(colorize(parsePrefix(openMenuFailedMessage)));
            return false;
        }
        // 人数不够，不打开
        if(Bukkit.getOnlinePlayers().size() < maxPlayerPerTeam){
            player.sendMessage(colorize(parsePrefix(openMenuFailedMessage)));
            return false;
        }
        // 构建容器
        Inventory inventory = null;
        if (guiTitle!= null) {
            inventory = Bukkit.createInventory(null, guiRows*9, colorize(guiTitle));
        }

        int shouldTeamNum = Bukkit.getOnlinePlayers().size()/maxPlayerPerTeam;
        // 同时还需要比较和config里设定的最大人数，不能超过maxTeam
        if(shouldTeamNum > maxTeam){
            shouldTeamNum = maxTeam;
        }

        for(int i = 0 ; i < shouldTeamNum ; i++){
            String teamName = AquaTeam.getGlobalState().getTeamNameByOrder(i);
            String thatMaterial = guiTeamIconMaterial.get(i);
            String thatDisPlayName = guiTeamIconDisplayNames.get(i);


            // 创建ICON物品
            Material mat = Material.valueOf(thatMaterial);
            ItemStack itemStack = new ItemStack(mat, 1);
            //获取物品元数据
            ItemMeta meta = itemStack.getItemMeta();

            // 修改物品元数据
            if (meta != null) {
                meta.setDisplayName(colorize(thatDisPlayName));  // 设置显示名称为团队名称
                List<String> loreList = new ArrayList<>();
                for(String lore : guiTeamIconLore){
                    int index = AquaTeam.getGlobalState().getTeamOrder(teamName);
                    lore = lore.replace("{$team}",guiTeamIconDisplayNames.get(index));
                    lore = colorize(lore);
                    loreList.add(lore);
                }
                meta.setLore(loreList);
                itemStack.setItemMeta(meta);  // 应用元数据到物品上
            }

            // 按照 teamIcon 顺序放置在容器内
            int slotIndex = guiTeamIconIndex.get(i);// 获取放置位置
            if (inventory != null) {
                inventory.setItem(slotIndex, itemStack);  // 在指定位置放置物品
            }

        }

        for (int anotherIndex : getFillIndex(guiRows)){
            Material mat = Material.valueOf(guiFillIconMaterial);
            ItemStack itemStack = new ItemStack(mat, 1);
            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(colorize(guiFillIconDisplayName));
                List<String> loreList = new ArrayList<>();
                loreList.add(guiFillIconLore);
                meta.setLore(loreList);
                itemStack.setItemMeta(meta);
            }

            if (inventory != null) {
                inventory.setItem(anotherIndex, itemStack);
            }
        }


        player.openInventory(inventory);
        return true;


    }

    public List<Integer> getFillIndex(int rows) {
        List<Integer> fillIndex = new ArrayList<>();

        // 计算位置
        for (int row = 0; row < rows; row++) {
            if (row == 0 || row == rows - 1) {
                // 如果是第一行或最后一行，添加所有列
                for (int col = 0; col < 9; col++) {
                    fillIndex.add(row * 9 + col); // 注意：应该是行*9+列
                }
            } else {
                // 否则，只添加行的第一个位置和最后一个位置
                fillIndex.add(row * 9);       // 每行的第一个位置
                fillIndex.add(row * 9 + 8);   // 每行的最后一个位置
            }
        }
        return fillIndex;
    }

    public boolean handleMenu(InventoryClickEvent event){
        // 检查是否是玩家打开的库存
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return false; // 如果玩家没有点击任何库存，返回
        }
        // 检查菜单的标题是否匹配您要检测的菜单
        if (event.getView().getTitle().equals(colorize(guiTitle))) {
            event.setCancelled(true); // 取消事件，防止物品被拖动或取走

            // 获取被点击的物品
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                // 获取点击该物品的玩家
                Player player = (Player) event.getWhoClicked(); // 将 HumanEntity 转换为 Player

                // 检测物品的元数据或其他属性，执行相应的函数
                String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

                // 处理逻辑

                teamIconHandler(itemName, player); // 使用获取的玩家对象调用处理函数
                return true;
            }
        }

        return false;
    }

    public void teamIconHandler(String itemName, Player player){
        if(itemName.equals(colorize(guiFillIconDisplayName))){
            return;
        }

        int index = guiTeamIconDisplayNames.indexOf(decolorize(itemName));
        String teamName = AquaTeam.getGlobalState().getTeamNameByOrder(index);

        removePlayer(player.getName());
        if(addPlayer(teamName,player.getName())){
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getName());
            if (tabPlayer != null) {
                setPrefix(tabPlayer);


            }

            // 计分板部分
            updateScoreboard();

            onPlayerChooseTeam(player);
            String onPlayerChooseTeamMessageTmp;
            String displayTeamName = guiTeamIconDisplayNames.get(index);
            onPlayerChooseTeamMessageTmp = onPlayerChooseTeamMessage.replace("{$team}",displayTeamName);
            player.sendMessage(colorize(parsePrefix(onPlayerChooseTeamMessageTmp)));

        }else {
            onPlayerChooseTeamError(player);
            String onPlayerChooseTeamErrorMessageTmp;
            String displayTeamName = guiTeamIconDisplayNames.get(index);
            onPlayerChooseTeamErrorMessageTmp = onPlayerChooseTeamErrorMessage.replace("{$team}",displayTeamName);
            player.sendMessage(colorize(parsePrefix(onPlayerChooseTeamErrorMessageTmp)));
        }


    }

    public void initTeam(){
        teamMap.clear();
        AquaTeam.getGlobalState().setStage(stage);
    }

    public boolean arePlayersInSameTeam(String player1, String player2) {
        String team1 = getTeamName(player1);
        String team2 = getTeamName(player2);
        return team1 != null && team1.equals(team2);
    }

    public boolean isFriendlyFireAllowed() {
        return friendlyFire;
    }



}
