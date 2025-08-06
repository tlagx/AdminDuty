package com.tlagx.unsubduty.config;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.models.DutyRank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

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
            for (String key : dutyLevels.getKeys(false)) {
                ConfigurationSection section = dutyLevels.getConfigurationSection(key);
                if (section != null) {
                    dutyRanks.put(key, new DutyRank(key, section));
                }
            }
        }
        
        defaultAccessMessage = config.getString("default-access-message", "&cТвоя роль не настроена.");
        adminsListFormat = config.getString("admins-list-format", "%status% %name% [%rank%] – %duty%");
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
