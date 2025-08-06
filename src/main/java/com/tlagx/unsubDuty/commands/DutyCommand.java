package com.tlagx.unsubduty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.services.DutyService;

public class DutyCommand implements CommandExecutor {
    private final DutyService dutyService;

    public DutyCommand(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /duty <hide|show|set|roles>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "hide":
                if (!player.hasPermission("unsubduty.hide")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                dutyService.setHideStatus(player.getUniqueId(), true);
                player.sendMessage(ChatColor.GREEN + "Your status is now hidden.");
                break;
                
            case "show":
                if (!player.hasPermission("unsubduty.hide")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                dutyService.setHideStatus(player.getUniqueId(), false);
                player.sendMessage(ChatColor.GREEN + "Your status is now visible.");
                break;
                
            case "set":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "Usage: /duty set <player> <rank>");
                break;
                
            case "roles":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Available duty roles would be listed here.");
                break;
                
            default:
                player.sendMessage(ChatColor.RED + "Usage: /duty <hide|show|set|roles>");
                break;
        }
        
        return true;
    }
}
