package com.tlagx.unsubduty.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionManager {
    private final JavaPlugin plugin;
    private FileConfiguration permsConfig;
    private File permsFile;
    private final Map<String, String> userRoles = new ConcurrentHashMap<>();

    public PermissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadPerms();
    }

    public void loadPerms() {
        permsFile = new File(plugin.getDataFolder(), "perms.yml");
        if (!permsFile.exists()) {
            plugin.saveResource("perms.yml", false);
        }
        permsConfig = YamlConfiguration.loadConfiguration(permsFile);
        loadUserRoles();
    }

    private void loadUserRoles() {
        userRoles.clear();
        
        // Load direct username:role mappings from root
        for (String username : permsConfig.getKeys(false)) {
            if (!username.equals("admins")) { // Skip if there's an admins section
                String role = permsConfig.getString(username);
                if (role != null) {
                    userRoles.put(username.toLowerCase(), role.toLowerCase());
                }
            }
        }
        
        // Also check for admins section for backward compatibility
        if (permsConfig.contains("admins")) {
            for (String key : permsConfig.getConfigurationSection("admins").getKeys(false)) {
                String role = permsConfig.getString("admins." + key);
                if (role != null) {
                    userRoles.put(key.toLowerCase(), role.toLowerCase());
                }
            }
        }
    }

    public String getUserRole(String username) {
        if (username == null) return null;
        return userRoles.get(username.toLowerCase());
    }

    public boolean hasRole(String username, String role) {
        if (username == null || role == null) return false;
        String userRole = getUserRole(username);
        return role.equalsIgnoreCase(userRole);
    }

    public void setUserRole(String username, String role) {
        if (username == null || role == null) return;
        userRoles.put(username.toLowerCase(), role.toLowerCase());
        permsConfig.set(username.toLowerCase(), role.toLowerCase());
        savePerms();
    }

    public void removeUserRole(String username) {
        if (username == null) return;
        userRoles.remove(username.toLowerCase());
        permsConfig.set(username.toLowerCase(), null);
        savePerms();
    }

    private void savePerms() {
        try {
            permsConfig.save(permsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save perms.yml: " + e.getMessage());
        }
    }

    public List<String> getAllAdminUsernames() {
        List<String> admins = new ArrayList<>();
        
        // Получаем всех пользователей из конфигурации
        for (String username : permsConfig.getKeys(false)) {
            if (!username.equals("admins")) {
                String role = permsConfig.getString(username);
                if (role != null) {
                    admins.add(username);
                }
            }
        }
        
        // Также проверяем секцию admins для обратной совместимости
        if (permsConfig.contains("admins")) {
            admins.addAll(permsConfig.getConfigurationSection("admins").getKeys(false));
        }
        
        return admins;
    }
}
