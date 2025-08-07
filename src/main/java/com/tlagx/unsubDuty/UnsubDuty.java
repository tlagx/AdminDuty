package com.tlagx.unsubduty;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.tlagx.unsubduty.commands.AdminCommand;
import com.tlagx.unsubduty.commands.AdminsCommand;
import com.tlagx.unsubduty.commands.DutyCommand;
import com.tlagx.unsubduty.config.ConfigManager;
import com.tlagx.unsubduty.listeners.PlayerListener;
import com.tlagx.unsubduty.localization.LocaleManager;
import com.tlagx.unsubduty.services.DutyService;
import com.tlagx.unsubduty.services.HideService;

import net.luckperms.api.LuckPerms;

public final class UnsubDuty extends JavaPlugin {
    private static UnsubDuty instance;
    private ConfigManager configManager;
    private DutyService dutyService;
    private HideService hideService;
    private LuckPerms luckPerms;
    private LocaleManager localeManager;

    @Override
    public void onEnable() {
        instance = this;
        
        if (!setupLuckPerms()) {
            getLogger().severe(getLocaleManager().get("luckperms_not_found"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.localeManager = new LocaleManager(this);
        this.dutyService = new DutyService(this, luckPerms, configManager);
        this.hideService = new HideService();
        
        registerCommands();
        registerEvents();
        
        getLogger().info(getLocaleManager().get("plugin_enabled"));
    }

    @Override
    public void onDisable() {
        getLogger().info(getLocaleManager().get("plugin_disabled"));
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
        getCommand("alogin").setExecutor(new AdminCommand(dutyService, this));
        getCommand("admins").setExecutor(new AdminsCommand(dutyService, hideService, this));
        getCommand("duty").setExecutor(new DutyCommand(dutyService, hideService, this));
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

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }
}
