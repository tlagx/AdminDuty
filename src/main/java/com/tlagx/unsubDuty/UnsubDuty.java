package com.tlagx.unsubduty;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.tlagx.unsubduty.commands.AdminCommand;
import com.tlagx.unsubduty.commands.AdminsCommand;
import com.tlagx.unsubduty.commands.DutyCommand;
import com.tlagx.unsubduty.config.ConfigManager;
import com.tlagx.unsubduty.listeners.PlayerListener;
import com.tlagx.unsubduty.services.DutyService;
import com.tlagx.unsubduty.storage.AdminStorage;

import net.luckperms.api.LuckPerms;

public final class UnsubDuty extends JavaPlugin {
    private static UnsubDuty instance;
    private ConfigManager configManager;
    private AdminStorage adminStorage;
    private DutyService dutyService;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        instance = this;
        
        if (!setupLuckPerms()) {
            getLogger().severe("LuckPerms not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.adminStorage = new AdminStorage(this);
        this.dutyService = new DutyService(this, luckPerms, configManager, adminStorage);
        
        registerCommands();
        registerEvents();
        
        getLogger().info("UnsubDuty enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (adminStorage != null) {
            adminStorage.save();
        }
        getLogger().info("UnsubDuty disabled!");
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            return true;
        }
        return false;
    }

    private void registerCommands() {
        getCommand("alogin").setExecutor(new AdminCommand(dutyService));
        getCommand("admins").setExecutor(new AdminsCommand(dutyService));
        getCommand("duty").setExecutor(new DutyCommand(dutyService));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, dutyService), this);
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        adminStorage.reload();
    }

    public static UnsubDuty getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AdminStorage getAdminStorage() {
        return adminStorage;
    }

    public DutyService getDutyService() {
        return dutyService;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
