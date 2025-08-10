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
import com.tlagx.unsubduty.services.PermissionManager;

public class DutyCommand implements CommandExecutor {
    private final DutyService dutyService;
    private final HideService hideService;
    private final UnsubDuty plugin;
    private final PermissionManager permissionManager;

    public DutyCommand(DutyService dutyService, HideService hideService, UnsubDuty plugin, PermissionManager permissionManager) {
        this.dutyService = dutyService;
        this.hideService = hideService;
        this.plugin = plugin;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLocaleManager().getColor("player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(plugin.getLocaleManager().getColor("duty_title").replace("%version%", plugin.getDescription().getVersion()));
            player.sendMessage(plugin.getLocaleManager().getColor("duty_available_commands"));
            player.sendMessage(plugin.getLocaleManager().getColor("duty_help_admins"));
            player.sendMessage(plugin.getLocaleManager().getColor("duty_help_set"));
            player.sendMessage(plugin.getLocaleManager().getColor("duty_help_remove"));
            player.sendMessage(plugin.getLocaleManager().getColor("duty_help_ahide"));
            player.sendMessage(plugin.getLocaleManager().getColor("duty_help_roles"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "hide":
                player.sendMessage(plugin.getLocaleManager().getColor("duty_usage"));
                break;

            case "show":
                player.sendMessage(plugin.getLocaleManager().getColor("duty_usage"));
                break;

            case "set":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
                    return true;
                }

                if (args.length < 3) {
                    player.sendMessage(plugin.getLocaleManager().getColor("set_usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(plugin.getLocaleManager().getColor("player_not_found").replace("%player%", args[1]));
                    return true;
                }

                String rankKey = args[2].toLowerCase();
                if (!plugin.getConfigManager().getDutyRankByKey(rankKey).isPresent()) {
                    player.sendMessage(plugin.getLocaleManager().getColor("invalid_rank").replace("%rank%", args[2]));
                    return true;
                }

                if (dutyService.setAdminRank(target.getUniqueId(), rankKey)) {
                    player.sendMessage(plugin.getLocaleManager().getColor("set_success").replace("%target%", target.getName()).replace("%rank%", rankKey));
                    target.sendMessage(plugin.getLocaleManager().getColor("set_target_message").replace("%rank%", rankKey));
                } else {
                    player.sendMessage(plugin.getLocaleManager().getColor("set_failed").replace("%target%", target.getName()));
                }
                break;

            case "roles":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
                    return true;
                }

                List<DutyRank> ranks = plugin.getConfigManager().getAllDutyRanksSorted();
                if (ranks.isEmpty()) {
                    player.sendMessage(plugin.getLocaleManager().getColor("no_ranks_configured"));
                    return true;
                }

                player.sendMessage(plugin.getLocaleManager().getColor("available_roles"));
                ranks.forEach(rank -> {
                    String rankName = rank.getRankName() != null ? rank.getRankName() : "Unknown";
                    String key = rank.getKey() != null ? rank.getKey() : "unknown";
                    
                    // Apply color translation to the rank name itself
                    String coloredRankName = org.bukkit.ChatColor.translateAlternateColorCodes('&', rankName);
                    
                    String message = plugin.getLocaleManager().getColor("role_format")
                            .replace("%name%", coloredRankName)
                            .replace("%key%", key);
                    player.sendMessage(message);
                });
                break;

            case "remove":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /duty remove <username>");
                    return true;
                }

                String targetName = args[1];
                Player targetPlayer = Bukkit.getPlayer(targetName);
                
                if (targetPlayer == null) {
                    player.sendMessage(plugin.getLocaleManager().getColor("player_not_found").replace("%player%", targetName));
                    return true;
                }

                permissionManager.removeUserRole(targetName);
                player.sendMessage(ChatColor.GREEN + "Removed all duty permissions from " + targetName);
                
                if (targetPlayer.isOnline()) {
                    targetPlayer.sendMessage(ChatColor.YELLOW + "Your duty permissions have been removed by an administrator.");
                }
                break;

            case "reload":
                if (!player.hasPermission("unsubduty.admin")) {
                    player.sendMessage(plugin.getLocaleManager().getColor("no_permission"));
                    return true;
                }

                try {
                    plugin.reload();
                    player.sendMessage(plugin.getLocaleManager().getColor("reload_success"));
                } catch (Exception e) {
                    player.sendMessage(plugin.getLocaleManager().getColor("reload_failed"));
                    plugin.getLogger().warning("Failed to reload configs: " + e.getMessage());
                }
                break;

            default:
                player.sendMessage(plugin.getLocaleManager().getColor("duty_usage"));
                break;
        }

        return true;
    }
}
