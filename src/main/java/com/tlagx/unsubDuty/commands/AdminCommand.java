package com.tlagx.unsubduty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.services.DutyService;

public class AdminCommand implements CommandExecutor {
    private final DutyService dutyService;

    public AdminCommand(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("unsubduty.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        dutyService.toggleDuty(player);
        return true;
    }
}
