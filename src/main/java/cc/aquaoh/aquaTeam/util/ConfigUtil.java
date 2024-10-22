package cc.aquaoh.aquaTeam.util;

import cc.aquaoh.aquaTeam.AquaTeam;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigUtil {
    private AquaTeam aquaTeam;
    private Map<String, Object> configMap = new HashMap<>(); // 用于存储键值对的哈希表

    public ConfigUtil(AquaTeam aquaTeam) {
        this.aquaTeam = aquaTeam;
    }

    // 获取配置对象
    public FileConfiguration getConfig() {
        return aquaTeam.getConfig();
    }

    // 获取配置的值
    public Object getConfigValue(String key) {
        return configMap.get(key);
    }

    // 自动读取所有键值对并存入哈希表
    public void readAllConfigKeys() {
        FileConfiguration config = getConfig();
        readKeys(config, "");
    }

    // 重载配置
    public void reloadAllConfigKeys() {
        aquaTeam.reloadConfig();
        FileConfiguration config = aquaTeam.getConfig();
        configMap.clear();
        readKeys(config, "");

    }

    // 获取哈希表
    public Map<String, Object> getConfigMap() {
        return configMap;
    }

    // 递归函数：读取某一层级的所有键值对并存入哈希表
    private void readKeys(ConfigurationSection section, String parentKey) {
        Set<String> keys = section.getKeys(false); // 获取当前层级的所有键

        for (String key : keys) {
            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key; // 构建完整键路径

            Object value = section.get(key);

            // 如果该值是另一个ConfigurationSection，继续递归
            if (value instanceof ConfigurationSection) {
                readKeys((ConfigurationSection) value, fullKey); // 递归读取嵌套配置
            } else {
                // 将键值对存入哈希表
                configMap.put(fullKey, value);
            }
        }
    }
}
