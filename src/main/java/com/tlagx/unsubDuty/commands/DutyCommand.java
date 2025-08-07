package com.tlagx.unsubduty.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.models.DutyRank;
import com.tlagx.unsubduty.services.DutyService;
import com.tlagx.unsubduty.services.HideService;

public class DutyCommand implements CommandExecutor {
    private final DutyService dutyService;
    private final HideService hideService;
    private final UnsubDuty plugin;

    public DutyCommand(DutyService dutyService, HideService hideService, UnsubDuty plugin) {
        this.dutyService = dutyService;
        this.hideService = hideService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.GREEN + "=-=UnsubDuty " + plugin.getDescription().getVersion() + "=-=");
            player.sendMessage(ChatColor.GREEN + "Available commands:");
            player.sendMessage(ChatColor.GREEN + "/duty hide - Hide your nickname");
            player.sendMessage(ChatColor.GREEN + "/duty show - Show your nickname");
            player.sendMessage(ChatColor.GREEN + "/duty set <player> <rank> - Set rank for a player");
            player.sendMessage(ChatColor.GREEN + "/duty roles - List all available ranks");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "hide":
                if (!player.hasPermission("unsubduty.use")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                hideService.setHidden(player.getUniqueId(), true);
                player.sendMessage(ChatColor.GREEN + "Your nickname is now hidden in /admins.");
                break;

            case "show":
                if (!player.hasPermission("unsubduty.use")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                hideService.setHidden(player.getUniqueId(), false);
                player.sendMessage(ChatColor.GREEN + "Your nickname is now visible in /admins.");
                break;

            case "set":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }

                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /duty set <player> <rank>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                    return true;
                }

                String rankKey = args[2].toLowerCase();
                if (!plugin.getConfigManager().getDutyRankByKey(rankKey).isPresent()) {
                    player.sendMessage(ChatColor.RED + "Invalid rank: " + args[2]);
                    return true;
                }

                if (dutyService.setAdminRank(target.getUniqueId(), rankKey)) {
                    player.sendMessage(ChatColor.GREEN + "Successfully set " + target.getName() + "'s rank to " + rankKey);
                    target.sendMessage(ChatColor.GREEN + "Your duty rank has been set to " + rankKey);
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to set rank for " + target.getName());
                }
                break;

            case "roles":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }

                List<DutyRank> ranks = plugin.getConfigManager().getAllDutyRanksSorted();
                if (ranks.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "No duty roles configured!");
                    return true;
                }

                player.sendMessage(ChatColor.GREEN + "Available duty roles:");
                ranks.forEach(rank -> {
                    String rankName = rank.getRankName() != null ? rank.getRankName() : "Unknown";
                    String key = rank.getKey() != null ? rank.getKey() : "unknown";
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "  " + rankName + " &7(" + key + ")"));
                });
                break;

            default:
                player.sendMessage(ChatColor.RED + "Usage: /duty <hide|show|set|roles>");
                break;
        }

        return true;
    }
}
