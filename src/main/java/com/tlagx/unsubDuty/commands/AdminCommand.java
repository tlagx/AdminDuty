package com.tlagx.unsubduty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.services.DutyService;

public class AdminCommand implements CommandExecutor {
    private final DutyService dutyService;
    private final UnsubDuty plugin;

    public AdminCommand(DutyService dutyService, UnsubDuty plugin) {
        this.dutyService = dutyService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLocaleManager().getColor("player_only"));
            return true;
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("unsubduty.use")) {
            player.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
            return true;
        }

        dutyService.toggleDuty(player);
        return true;
    }
}
