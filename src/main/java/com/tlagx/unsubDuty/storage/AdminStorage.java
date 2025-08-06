package com.tlagx.unsubduty.storage;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.models.AdminData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminStorage {
    private final UnsubDuty plugin;
    private final Map<UUID, AdminData> adminData = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public AdminStorage(UnsubDuty plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "admins.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create admins.yml!");
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        if (dataConfig.contains("admins")) {
            for (String uuidStr : dataConfig.getConfigurationSection("admins").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                String playerName = dataConfig.getString("admins." + uuidStr + ".name");
                String activeRank = dataConfig.getString("admins." + uuidStr + ".rank");
                boolean inDuty = dataConfig.getBoolean("admins." + uuidStr + ".duty");
                boolean hideStatus = dataConfig.getBoolean("admins." + uuidStr + ".hide");
                
                adminData.put(uuid, new AdminData(playerName, activeRank, inDuty, hideStatus));
            }
        }
    }

    public void save() {
        dataConfig.set("admins", null);
        
        for (Map.Entry<UUID, AdminData> entry : adminData.entrySet()) {
            String path = "admins." + entry.getKey().toString();
            AdminData data = entry.getValue();
            
            dataConfig.set(path + ".name", data.getPlayerName());
            dataConfig.set(path + ".rank", data.getActiveRank());
            dataConfig.set(path + ".duty", data.isInDuty());
            dataConfig.set(path + ".hide", data.isHideStatus());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save admins.yml!");
        }
    }

    public AdminData getAdminData(UUID uuid) {
        return adminData.get(uuid);
    }

    public void setAdminData(UUID uuid, AdminData data) {
        adminData.put(uuid, data);
    }

    public void removeAdminData(UUID uuid) {
        adminData.remove(uuid);
    }

    public Map<UUID, AdminData> getAllAdminData() {
        return new HashMap<>(adminData);
    }

    public void reload() {
        adminData.clear();
        load();
    }
}
