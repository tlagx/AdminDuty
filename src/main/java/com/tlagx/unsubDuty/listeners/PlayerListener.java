package com.tlagx.unsubduty.listeners;

import com.tlagx.unsubduty.services.DutyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {
    private final JavaPlugin plugin;
    private final DutyService dutyService;

    public PlayerListener(JavaPlugin plugin, DutyService dutyService) {
        this.plugin = plugin;
        this.dutyService = dutyService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Implementation for player join event
    }
}
