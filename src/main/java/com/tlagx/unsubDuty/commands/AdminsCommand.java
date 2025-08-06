package com.tlagx.unsubduty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tlagx.unsubduty.services.DutyService;

public class AdminsCommand implements CommandExecutor {
    private final DutyService dutyService;

    public AdminsCommand(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("unsubduty.view")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "=== Duty Admins ===");
        sender.sendMessage(ChatColor.YELLOW + "Admin list functionality would be implemented here.");
        
        return true;
    }
}
