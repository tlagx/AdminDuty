package com.tlagx.unsubduty;

import org.bukkit.plugin.java.JavaPlugin;

import com.tlagx.unsubduty.commands.AHideCommand;
import com.tlagx.unsubduty.commands.AdminCommand;
import com.tlagx.unsubduty.commands.AdminsCommand;
import com.tlagx.unsubduty.commands.DutyCommand;
import com.tlagx.unsubduty.commands.DutyTabCompleter;
import com.tlagx.unsubduty.config.ConfigManager;
import com.tlagx.unsubduty.listeners.PlayerListener;
import com.tlagx.unsubduty.localization.LocaleManager;
import com.tlagx.unsubduty.services.DutyService;
import com.tlagx.unsubduty.services.HideService;
import com.tlagx.unsubduty.services.LuckPermsService;
import com.tlagx.unsubduty.services.PermissionManager;
import com.tlagx.unsubduty.storage.UserStorage;

public final class UnsubDuty extends JavaPlugin {
    private static UnsubDuty instance;
    private ConfigManager configManager;
    private DutyService dutyService;
    private HideService hideService;
    private UserStorage userStorage;
    private PermissionManager permissionManager;
    private LuckPermsService luckPermsService;
    private LocaleManager localeManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.localeManager = new LocaleManager(this);
        this.userStorage = new UserStorage(this);
        this.permissionManager = new PermissionManager(this);
        this.luckPermsService = new LuckPermsService();
        this.dutyService = new DutyService(configManager, permissionManager, luckPermsService);
        this.hideService = new HideService(userStorage);
        
        registerCommands();
        registerEvents();
        
        getLogger().info(getLocaleManager().get("plugin_enabled"));
    }

    @Override
    public void onDisable() {
        getLogger().info(getLocaleManager().get("plugin_disabled"));
    }

    private void registerCommands() {
        getCommand("alogin").setExecutor(new AdminCommand(dutyService, this));
        getCommand("admins").setExecutor(new AdminsCommand(dutyService, hideService, this));
        getCommand("duty").setExecutor(new DutyCommand(dutyService, hideService, this, permissionManager));
        getCommand("duty").setTabCompleter(new DutyTabCompleter(this));
        getCommand("ahide").setExecutor(new AHideCommand(hideService, this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, dutyService), this);
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
    }

    public static UnsubDuty getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DutyService getDutyService() {
        return dutyService;
    }

    public HideService getHideService() {
        return hideService;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }
}
