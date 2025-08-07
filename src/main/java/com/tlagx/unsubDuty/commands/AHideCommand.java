package com.tlagx.unsubduty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.services.HideService;

public class AHideCommand implements CommandExecutor {
    private final HideService hideService;
    private final UnsubDuty plugin;

    public AHideCommand(HideService hideService, UnsubDuty plugin) {
        this.hideService = hideService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLocaleManager().getColor("player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("unsubduty.hide")) {
            player.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
            return true;
        }

        // Toggle the hidden status
        boolean wasHidden = hideService.isHidden(player.getUniqueId());
        hideService.toggleHidden(player.getUniqueId());
        boolean isNowHidden = hideService.isHidden(player.getUniqueId());

        if (isNowHidden) {
            player.sendMessage(plugin.getLocaleManager().getColor("hide_success"));
        } else {
            player.sendMessage(plugin.getLocaleManager().getColor("show_success"));
        }

        return true;
    }
}
