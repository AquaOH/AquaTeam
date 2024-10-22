package cc.aquaoh.aquaTeam.util;

import cc.aquaoh.aquaTeam.AquaTeam;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class WorldeditUtil {

    private String worldeditWorldFrom;
    private String worldeditWorldTo;
    private List<Integer> worldeditPointLow;
    private List<Integer> worldeditPointHigh;
    private List<Integer> worldeditPointPaste;
    private String worldeditDirection;
    private int worldeditGap;
    private List<Integer> worldeditPointRespawn;
    private List<List<Integer>> worldeditPointRespawnList;
    private float worldeditYaw;
    private float worldeditPitch;
    private List<Integer> worldeditPointEnd;

    private String mode;


    public WorldeditUtil(String mode,String worldeditWorldFrom,String worldeditWorldTo,List<Integer> worldeditPointLow, List<Integer> worldeditPointHigh,List<Integer> worldeditPointPaste,String worldeditDirection,int worldeditGap,List<Integer> worldeditPointRespawn,float worldeditYaw,float worldeditPitch,List<Integer> worldeditPointEnd){

        this.worldeditWorldFrom = worldeditWorldFrom;
        this.worldeditWorldTo = worldeditWorldTo;
        this.worldeditPointLow = worldeditPointLow;
        this.worldeditPointHigh = worldeditPointHigh;
        this.worldeditPointPaste = worldeditPointPaste;
        this.worldeditDirection = worldeditDirection;
        this.worldeditGap = worldeditGap;
        this.worldeditPointRespawn = worldeditPointRespawn;
        this.worldeditYaw = worldeditYaw;
        this.worldeditPitch = worldeditPitch;
        this.worldeditPointEnd = worldeditPointEnd;
        this.mode = mode;

    }

    public WorldeditUtil(String mode,String  worldeditWorldFrom,String worldeditWorldTo,List<Integer> worldeditPointLow,List<Integer> worldeditPointHigh,List<Integer> worldeditPointPaste,List<List<Integer>> worldeditPointRespawn){
        this.worldeditWorldFrom = worldeditWorldFrom;
        this.worldeditWorldTo = worldeditWorldTo;
        this.worldeditPointLow = worldeditPointLow;
        this.worldeditPointHigh = worldeditPointHigh;
        this.worldeditPointPaste = worldeditPointPaste;
        this.mode = mode;
        this.worldeditPointRespawnList = worldeditPointRespawn;
    }

    public void pasteStruce() throws IOException {
        // 检查列表是否为空
        if (worldeditPointLow.isEmpty() || worldeditPointHigh.isEmpty() || worldeditPointPaste.isEmpty()) {
            throw new IllegalArgumentException("配置文件中的坐标数据为空或格式不正确！");
        }

        World worldFromItem = FaweAPI.getWorld(worldeditWorldFrom);
        World worldToItem = FaweAPI.getWorld(worldeditWorldTo);

        if (worldFromItem == null || worldToItem == null) {
            Bukkit.getLogger().info("空的世界！！");
        }

        BlockVector3 min = BlockVector3.at(worldeditPointLow.get(0), worldeditPointLow.get(1), worldeditPointLow.get(2));
        BlockVector3 max = BlockVector3.at(worldeditPointHigh.get(0), worldeditPointHigh.get(1), worldeditPointHigh.get(2));
        CuboidRegion region = new CuboidRegion(worldFromItem, min, max);

        // 创建剪贴板对象并复制区域内容
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldFromItem)) {
            ForwardExtentCopy copy = new ForwardExtentCopy(
                    editSession, region, clipboard, region.getMinimumPoint()
            );
            Operations.complete(copy); // 执行复制操作
        }

        // 使用 calculatePosition 计算所有粘贴位置
        BlockVector3[] pastePositions = new BlockVector3[0];
        if (mode.equals("luckyblock")) {
            pastePositions = calculatePosition();
        }
        if (mode.equals("pvp")) {
            pastePositions = calculatePositionPVP();
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldToItem)) {
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            Map<String, BlockState> teamColorBlocks = createTeamColorBlockMap();

            for (int i = 0; i < pastePositions.length; i++) {
                BlockVector3 pasteTo = pastePositions[i];
                Operation operation = holder.createPaste(editSession).to(pasteTo).build();
                Operations.complete(operation);

                if (mode.equals("luckyblock")) {
                    String teamName = AquaTeam.getGlobalState().getTeamNameByOrder(i);
                    replaceBlueBlocksWithTeamColor(editSession, pasteTo, teamColorBlocks.get(teamName + "_CONCRETE"), teamColorBlocks.get(teamName + "_CARPET"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().info("AquaTeAm: 粘贴成功");
        if (mode.equals("luckyblock")) {
            replaceEndPointsWithFroglight();
        }

        repawnAllTeamPlayer();


    }

    public BlockVector3[] calculatePosition() {
        int numTeam = AquaTeam.getTeam().getSizeofTeamNum();

        // 计算结构的长宽高
        int length = Math.abs(worldeditPointHigh.get(0) - worldeditPointLow.get(0)) + 1;
        int width = Math.abs(worldeditPointHigh.get(2) - worldeditPointLow.get(2)) + 1;
        int height = Math.abs(worldeditPointHigh.get(1) - worldeditPointLow.get(1)) + 1;

        // 初始化粘贴位置数组
        BlockVector3[] positions = new BlockVector3[numTeam];

        // 初始粘贴位置
        BlockVector3 basePasteTo = BlockVector3.at(
                worldeditPointPaste.get(0),
                worldeditPointPaste.get(1),
                worldeditPointPaste.get(2)
        );

        int bigoffset = worldeditGap; // 或者设置为一个固定值，比如 10

        // 计算整个结构的边界
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        List<List<Integer>> pointRespawnList = new ArrayList<>();
        List<List<Integer>> pointEndList = new ArrayList<>();

        // 计算重生点与基准点的偏移
        int offsetX1 = worldeditPointRespawn.get(0) - worldeditPointLow.get(0);
        int offsetY1 = worldeditPointRespawn.get(1) - worldeditPointLow.get(1);
        int offsetZ1 = worldeditPointRespawn.get(2) - worldeditPointLow.get(2);

        // 计算结束点与基准点的偏移
        int offsetX2 = worldeditPointEnd.get(0) - worldeditPointLow.get(0);
        int offsetY2 = worldeditPointEnd.get(1) - worldeditPointLow.get(1);
        int offsetZ2 = worldeditPointEnd.get(2) - worldeditPointLow.get(2);

        for (int i = 0; i < numTeam; i++) {
            int offsetX = 0;
            int offsetZ = 0;

            // 根据方向计算偏移量
            switch (worldeditDirection.toUpperCase()) {
                case "N": offsetZ = -(width + worldeditGap) * i; break;
                case "S": offsetZ = (width + worldeditGap) * i; break;
                case "E": offsetX = (length + worldeditGap) * i; break;
                case "W": offsetX = -(length + worldeditGap) * i; break;
                default: throw new IllegalArgumentException("无效的方向：" + worldeditDirection);
            }

            // 计算新的粘贴位置
            BlockVector3 pastePos = basePasteTo.add(offsetX, 0, offsetZ);
            positions[i] = pastePos;

            // 更新边界
            minX = Math.min(minX, pastePos.x());
            minY = Math.min(minY, pastePos.y());
            minZ = Math.min(minZ, pastePos.z());
            maxX = Math.max(maxX, pastePos.x() + length);
            maxY = Math.max(maxY, pastePos.y() + height);
            maxZ = Math.max(maxZ, pastePos.z() + width);

            // 计算重生点
            List<Integer> respawnPoint = Arrays.asList(
                    pastePos.x() + offsetX1,
                    pastePos.y() + offsetY1,
                    pastePos.z() + offsetZ1
            );
            pointRespawnList.add(respawnPoint);

            // 计算结束点
            List<Integer> endPoint = Arrays.asList(
                    pastePos.x() + offsetX2,
                    pastePos.y() + offsetY2,
                    pastePos.z() + offsetZ2
            );
            pointEndList.add(endPoint);
        }

        // 设置包裹整个结构的点，包括额外的 bigoffset
        List<Integer> tempLowPoint = Arrays.asList(minX - bigoffset, minY - bigoffset, minZ - bigoffset);
        List<Integer> tempHighPoint = Arrays.asList(maxX + bigoffset, maxY + bigoffset, maxZ + bigoffset);

        Bukkit.getLogger().info("AquaTeAam: 开始设置LuckyBlock点位");
        Bukkit.getLogger().info(pointRespawnList.toString());
        Bukkit.getLogger().info(pointEndList.toString());
        Bukkit.getLogger().info("AquaTeAam: 结束设置LuckyBlock点位");

        AquaTeam.getGlobalState().setLuckyBlockStrucePointRespawn(pointRespawnList);
        AquaTeam.getGlobalState().setLuckyBlockStrucePointEnd(pointEndList);
        AquaTeam.getGlobalState().setLuckyBlockStrucePointLow(tempLowPoint);
        AquaTeam.getGlobalState().setLuckyBlockStrucePointHigh(tempHighPoint);

        return positions;
    }

    public BlockVector3[] calculatePositionPVP() {
        int numTeam = 1;

        // 计算结构的长宽高
        int length = Math.abs(worldeditPointHigh.get(0) - worldeditPointLow.get(0)) + 1;
        int width = Math.abs(worldeditPointHigh.get(2) - worldeditPointLow.get(2)) + 1;
        int height = Math.abs(worldeditPointHigh.get(1) - worldeditPointLow.get(1)) + 1;

        // 初始化粘贴位置数组
        BlockVector3[] positions = new BlockVector3[numTeam];

        // 初始粘贴位置
        BlockVector3 basePasteTo = BlockVector3.at(worldeditPointPaste.get(0), worldeditPointPaste.get(1), worldeditPointPaste.get(2));

        int bigoffset = 20; // 或者设置为一个固定值，比如 10

        // 计算结构的边界
        int minX = basePasteTo.x();
        int minY = basePasteTo.y();
        int minZ = basePasteTo.z();
        int maxX = minX + length;
        int maxY = minY + height;
        int maxZ = minZ + width;

        // 设置包裹整个结构的点，包括额外的 bigoffset
        List<Integer> tempLowPoint = Arrays.asList(minX - bigoffset, minY - bigoffset, minZ - bigoffset);
        List<Integer> tempHighPoint = Arrays.asList(maxX + bigoffset, maxY + bigoffset, maxZ + bigoffset);

        Bukkit.getLogger().info("AquaTeam: 结构Low Point: " + tempLowPoint);
        Bukkit.getLogger().info("AquaTeam: 结构High Point: " + tempHighPoint);
        AquaTeam.getGlobalState().setPVPStructurePointLow(tempLowPoint);
        AquaTeam.getGlobalState().setPVPStructurePointHigh(tempHighPoint);

        // 计算新的粘贴位置
        positions[0] = basePasteTo;

        List<List<Integer>> pointRespawnList = new ArrayList<>();

        for (List<Integer> respawnPoint : worldeditPointRespawnList) {
            // 计算重生点与基准点的偏移
            int offsetX = respawnPoint.get(0) - worldeditPointLow.get(0);
            int offsetY = respawnPoint.get(1) - worldeditPointLow.get(1);
            int offsetZ = respawnPoint.get(2) - worldeditPointLow.get(2);

            List<Integer> newRespawnPoint = Arrays.asList(
                    basePasteTo.x() + offsetX,
                    basePasteTo.y() + offsetY,
                    basePasteTo.z() + offsetZ
            );
            pointRespawnList.add(newRespawnPoint);
        }

        Bukkit.getLogger().info("AquaTeAam: 开始设置PVP点位");
        Bukkit.getLogger().info(pointRespawnList.toString());
        Bukkit.getLogger().info("AquaTeAam: 结束设置PVP点位");
        AquaTeam.getGlobalState().setPVPStructurePointRespawn(pointRespawnList);

        return positions;
    }

    public void clearStruceOfHighLowPoints() throws IOException {

        List<Integer> tempLowPoint = new ArrayList<>();
        List<Integer> tempHighPoint = new ArrayList<>();
        // 根据不同发起任务的对象获取低点和高点
        if(mode.equals("luckyblock")) {
            tempLowPoint = AquaTeam.getGlobalState().getLuckyBlockStrucePointLow();
            tempHighPoint = AquaTeam.getGlobalState().getLuckyBlockStrucePointHigh();
        }else if(mode.equals("pvp")) {
            tempLowPoint = AquaTeam.getGlobalState().getPVPStructurePointLow();
            tempHighPoint = AquaTeam.getGlobalState().getPVPStructurePointHigh();
        }


        // 检查世界
        World worldToItem = FaweAPI.getWorld(worldeditWorldTo);
        if (worldToItem == null) {
            Bukkit.getLogger().info("目标世界不存在！");
            return;
        }

        // 定义CuboidRegion区域
        BlockVector3 low = BlockVector3.at(tempLowPoint.get(0), tempLowPoint.get(1), tempLowPoint.get(2));
        BlockVector3 high = BlockVector3.at(tempHighPoint.get(0), tempHighPoint.get(1), tempHighPoint.get(2));
        CuboidRegion region = new CuboidRegion(worldToItem, low, high);

        // 使用 EditSession 来设置区域为空气
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldToItem)) {
            // 设置区域为空气
            editSession.setBlocks((Region) region, BlockTypes.AIR.getDefaultState());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void repawnAllTeamPlayer(){
        List<List<Integer>> pointRespawnList = new ArrayList<>();
        if(mode.equals("luckyblock")){
           pointRespawnList = AquaTeam.getGlobalState().getLuckyBlockStrucePointRespawn();
            int i;
            for(i=0; i<pointRespawnList.size(); i++){
                String teamName =AquaTeam.getGlobalState().getTeamNameByOrder(i);

                Location location = new Location(Bukkit.getWorld(worldeditWorldTo),pointRespawnList.get(i).get(0),pointRespawnList.get(i).get(1),pointRespawnList.get(i).get(2),worldeditYaw,worldeditPitch);
                for(String playername: AquaTeam.getTeam().getPlayers(teamName)){
                    Player player = Bukkit.getPlayer(playername);
                    player.teleport(location);
                    player.setBedSpawnLocation(location,true);
                }
            }
        }
    }

    public void replaceEndPointsWithFroglight() {
        List<List<Integer>> pointEndList = AquaTeam.getGlobalState().getLuckyBlockStrucePointEnd();
        if (pointEndList == null || pointEndList.isEmpty()) {
            Bukkit.getLogger().warning("EndPoints list is null or empty!");
            return;
        }

        World worldToItem = FaweAPI.getWorld(worldeditWorldTo);
        if (worldToItem == null) {
            Bukkit.getLogger().warning("Target world does not exist!");
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldToItem)) {
            for (List<Integer> point : pointEndList) {
                if (point.size() < 3) {
                    Bukkit.getLogger().warning("Invalid point in EndPoints list: " + point);
                    continue;
                }
                BlockVector3 location = BlockVector3.at(point.get(0), point.get(1), point.get(2));
                // 替换为蛙鸣灯（选择一种，这里用的是 pearlescent_froglight）
                editSession.setBlock(location, BlockTypes.PEARLESCENT_FROGLIGHT.getDefaultState());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, BlockState> createTeamColorBlockMap() {
        Map<String, BlockState> colorMap = new HashMap<>();
        colorMap.put("RED_CONCRETE", BlockTypes.RED_CONCRETE.getDefaultState());
        colorMap.put("RED_CARPET", BlockTypes.RED_CARPET.getDefaultState());
        colorMap.put("GREEN_CONCRETE", BlockTypes.GREEN_CONCRETE.getDefaultState());
        colorMap.put("GREEN_CARPET", BlockTypes.GREEN_CARPET.getDefaultState());
        colorMap.put("BLUE_CONCRETE", BlockTypes.BLUE_CONCRETE.getDefaultState());
        colorMap.put("BLUE_CARPET", BlockTypes.BLUE_CARPET.getDefaultState());
        colorMap.put("YELLOW_CONCRETE", BlockTypes.YELLOW_CONCRETE.getDefaultState());
        colorMap.put("YELLOW_CARPET", BlockTypes.YELLOW_CARPET.getDefaultState());
        colorMap.put("CYAN_CONCRETE", BlockTypes.CYAN_CONCRETE.getDefaultState());
        colorMap.put("CYAN_CARPET", BlockTypes.CYAN_CARPET.getDefaultState());
        colorMap.put("MAGENTA_CONCRETE", BlockTypes.MAGENTA_CONCRETE.getDefaultState());
        colorMap.put("MAGENTA_CARPET", BlockTypes.MAGENTA_CARPET.getDefaultState());
        colorMap.put("ORANGE_CONCRETE", BlockTypes.ORANGE_CONCRETE.getDefaultState());
        colorMap.put("ORANGE_CARPET", BlockTypes.ORANGE_CARPET.getDefaultState());
        colorMap.put("PINK_CONCRETE", BlockTypes.PINK_CONCRETE.getDefaultState());
        colorMap.put("PINK_CARPET", BlockTypes.PINK_CARPET.getDefaultState());
        colorMap.put("PURPLE_CONCRETE", BlockTypes.PURPLE_CONCRETE.getDefaultState());
        colorMap.put("PURPLE_CARPET", BlockTypes.PURPLE_CARPET.getDefaultState());
        colorMap.put("BROWN_CONCRETE", BlockTypes.BROWN_CONCRETE.getDefaultState());
        colorMap.put("BROWN_CARPET", BlockTypes.BROWN_CARPET.getDefaultState());
        colorMap.put("LIGHT_BLUE_CONCRETE", BlockTypes.LIGHT_BLUE_CONCRETE.getDefaultState());
        colorMap.put("LIGHT_BLUE_CARPET", BlockTypes.LIGHT_BLUE_CARPET.getDefaultState());
        colorMap.put("LIME_CONCRETE", BlockTypes.LIME_CONCRETE.getDefaultState());
        colorMap.put("LIME_CARPET", BlockTypes.LIME_CARPET.getDefaultState());
        colorMap.put("WHITE_CONCRETE", BlockTypes.WHITE_CONCRETE.getDefaultState());
        colorMap.put("WHITE_CARPET", BlockTypes.WHITE_CARPET.getDefaultState());
        colorMap.put("LIGHT_GRAY_CONCRETE", BlockTypes.LIGHT_GRAY_CONCRETE.getDefaultState());
        colorMap.put("LIGHT_GRAY_CARPET", BlockTypes.LIGHT_GRAY_CARPET.getDefaultState());
        colorMap.put("GRAY_CONCRETE", BlockTypes.GRAY_CONCRETE.getDefaultState());
        colorMap.put("GRAY_CARPET", BlockTypes.GRAY_CARPET.getDefaultState());
        return colorMap;
    }

    private void replaceBlueBlocksWithTeamColor(EditSession editSession, BlockVector3 pasteTo, BlockState teamConcrete, BlockState teamCarpet) {
        int length = Math.abs(worldeditPointHigh.get(0) - worldeditPointLow.get(0)) + 1;
        int width = Math.abs(worldeditPointHigh.get(2) - worldeditPointLow.get(2)) + 1;
        int height = Math.abs(worldeditPointHigh.get(1) - worldeditPointLow.get(1)) + 1;

        for (int x = pasteTo.x(); x < pasteTo.x() + length; x++) {
            for (int y = pasteTo.y(); y < pasteTo.y() + height; y++) {
                for (int z = pasteTo.z(); z < pasteTo.z() + width; z++) {
                    BlockVector3 pos = BlockVector3.at(x, y, z);
                    BlockState currentBlock = editSession.getBlock(pos);

                    if (currentBlock.equals(BlockTypes.BLUE_CONCRETE.getDefaultState())) {
                        editSession.setBlock(pos, teamConcrete);
                    } else if (currentBlock.equals(BlockTypes.BLUE_CARPET.getDefaultState())) {
                        editSession.setBlock(pos, teamCarpet);
                    }
                }
            }
        }
    }



}
