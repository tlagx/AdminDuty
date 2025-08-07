package com.tlagx.unsubduty.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.services.DutyService;
import com.tlagx.unsubduty.services.HideService;

public class AdminsCommand implements CommandExecutor {
    private final DutyService dutyService;
    private final HideService hideService;
    private final UnsubDuty plugin;

    public AdminsCommand(DutyService dutyService, HideService hideService, UnsubDuty plugin) {
        this.dutyService = dutyService;
        this.hideService = hideService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("unsubduty.view")) {
            sender.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
            return true;
        }

        List<Player> admins = dutyService.getAllDutyAdmins();
        
        if (admins.isEmpty()) {
            sender.sendMessage(plugin.getLocaleManager().getColor("no_duty_admins"));
            return true;
        }

        sender.sendMessage(plugin.getLocaleManager().getColor("admins_title"));
        
        for (Player admin : admins) {
            String rankName = dutyService.getRankName(admin);
            boolean isInDuty = dutyService.isPlayerInDuty(admin, dutyService.findDutyRank(admin).orElse(null));
            boolean isHidden = hideService.isHidden(admin);
            
            String status = isInDuty ? plugin.getLocaleManager().getColor("in_duty") : plugin.getLocaleManager().getColor("not_in_duty");
            String name = admin.getName();
            
            if (isHidden && !sender.hasPermission("unsubduty.admin") && !sender.hasPermission("unsubduty.see-hidden")) {
                name = plugin.getLocaleManager().getColor("hidden_name");
            } else if (isHidden && sender.hasPermission("unsubduty.admin")) {
                name = plugin.getLocaleManager().getColor("hidden_admin").replace("%name%", admin.getName());
            }
            
            String format = ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfigManager().getAdminsListFormat()
                    .replace("%status%", isInDuty ? "●" : "○")
                    .replace("%name%", name)
                    .replace("%rank%", rankName)
                    .replace("%duty%", status));
            
            sender.sendMessage(format);
        }
        
        return true;
    }
}
