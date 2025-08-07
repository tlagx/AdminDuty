package com.tlagx.unsubduty.config;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.models.DutyRank;

public class ConfigManager {
    private final UnsubDuty plugin;
    private final Map<String, DutyRank> dutyRanks = new LinkedHashMap<>();
    private String defaultAccessMessage;
    private String adminsListFormat;
    private String hideFormat;

    public ConfigManager(UnsubDuty plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        
        dutyRanks.clear();
        
        ConfigurationSection dutyLevels = config.getConfigurationSection("duty-levels");
        if (dutyLevels != null) {
            plugin.getLogger().info("Loading duty levels from config.yml...");
            for (String key : dutyLevels.getKeys(false)) {
                ConfigurationSection section = dutyLevels.getConfigurationSection(key);
                if (section != null) {
                    String permission = section.getString("permission");
                    String toGroup = section.getString("to-group");
                    String rankName = section.getString("rank-name");
                    int priority = section.getInt("priority");
                    String joinMessage = section.getString("join-message");
                    String leaveMessage = section.getString("leave-message");
                    
                    plugin.getLogger().info("Loaded duty rank: " + key + 
                        " [permission=" + permission + 
                        ", to-group=" + toGroup + 
                        ", rank-name=" + rankName + 
                        ", priority=" + priority + "]");
                    
                    dutyRanks.put(key, new DutyRank(
                        key,
                        permission,
                        toGroup,
                        rankName,
                        priority,
                        joinMessage,
                        leaveMessage
                    ));
                }
            }
            plugin.getLogger().info("Total duty ranks loaded: " + dutyRanks.size());
        } else {
            plugin.getLogger().warning("No duty-levels section found in config.yml!");
        }
        
        defaultAccessMessage = config.getString("default-access-message", "&cТвоя роль не настроена.");
        adminsListFormat = config.getString("admins-list-format", "%status% %name% [%rank%&r] – %duty%");
        hideFormat = config.getString("hide-format", "&kСкрыт&f");
    }

    public Optional<DutyRank> getDutyRankByPermission(String permission) {
        return dutyRanks.values().stream()
                .filter(rank -> rank.getPermission().equals(permission))
                .findFirst();
    }

    public Optional<DutyRank> getDutyRankByKey(String key) {
        return Optional.ofNullable(dutyRanks.get(key));
    }

    public List<DutyRank> getAllDutyRanksSorted() {
        return dutyRanks.values().stream()
                .sorted(Comparator.comparingInt(DutyRank::getPriority))
                .collect(Collectors.toList());
    }

    public String getDefaultAccessMessage() {
        return defaultAccessMessage;
    }

    public String getAdminsListFormat() {
        return adminsListFormat;
    }

    public String getHideFormat() {
        return hideFormat;
    }
}
