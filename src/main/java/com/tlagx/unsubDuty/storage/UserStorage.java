package com.tlagx.unsubduty.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.tlagx.unsubduty.UnsubDuty;

public class UserStorage {
    private final UnsubDuty plugin;
    private final File usersFile;
    private FileConfiguration usersConfig;
    private final Map<UUID, Boolean> hiddenStatus = new HashMap<>();

    public UserStorage(UnsubDuty plugin) {
        this.plugin = plugin;
        this.usersFile = new File(plugin.getDataFolder(), "users.yml");
        loadUsers();
    }

    private void loadUsers() {
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create users.yml: " + e.getMessage());
            }
        }
        
        usersConfig = YamlConfiguration.loadConfiguration(usersFile);
        
        // Load hidden status for all users
        if (usersConfig.contains("hidden")) {
            for (String uuidStr : usersConfig.getConfigurationSection("hidden").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    boolean hidden = usersConfig.getBoolean("hidden." + uuidStr);
                    hiddenStatus.put(uuid, hidden);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in users.yml: " + uuidStr);
                }
            }
        }
    }

    public boolean isHidden(UUID uuid) {
        return hiddenStatus.getOrDefault(uuid, false);
    }

    public void setHidden(UUID uuid, boolean hidden) {
        hiddenStatus.put(uuid, hidden);
        usersConfig.set("hidden." + uuid.toString(), hidden);
        saveUsers();
    }

    public void toggleHidden(UUID uuid) {
        boolean current = isHidden(uuid);
        setHidden(uuid, !current);
    }

    private void saveUsers() {
        try {
            usersConfig.save(usersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save users.yml: " + e.getMessage());
        }
    }
}
