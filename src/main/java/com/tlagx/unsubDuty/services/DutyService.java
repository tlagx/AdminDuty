package com.tlagx.unsubduty.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.config.ConfigManager;
import com.tlagx.unsubduty.models.DutyRank;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;

public class DutyService {
    private final UnsubDuty plugin;
    private final LuckPerms luckPerms;
    private final ConfigManager configManager;
    private boolean debug = false;

    public DutyService(UnsubDuty plugin, LuckPerms luckPerms, ConfigManager configManager) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.configManager = configManager;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void debugLog(String message) {
        if (debug) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public boolean toggleDuty(Player player) {
        if (player.isOp()) {
            player.sendMessage(ChatColor.RED + "Плагин не может правильно обработать данного игрока.");
            return false;
        }

        Optional<DutyRank> rank = findDutyRank(player);
        if (!rank.isPresent()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configManager.getDefaultAccessMessage()));
            return false;
        }

        DutyRank dutyRank = rank.get();
        
        // Validate the toGroup is not null
        if (dutyRank.getToGroup() == null || dutyRank.getToGroup().trim().isEmpty()) {
            player.sendMessage(ChatColor.RED + "Ошибка: группа для ранга не настроена.");
            plugin.getLogger().warning("Duty rank " + dutyRank.getKey() + " has null or empty toGroup!");
            return false;
        }
        
        // Check current group
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            player.sendMessage(ChatColor.RED + "Ошибка при получении данных пользователя.");
            plugin.getLogger().warning("Could not get LuckPerms user for " + player.getName());
            return false;
        }

        // Check if player has the target group
        boolean hasTargetGroup = user.getNodes().stream()
                .filter(InheritanceNode.class::isInstance)
                .map(InheritanceNode.class::cast)
                .anyMatch(node -> node.getGroupName().equals(dutyRank.getToGroup()));

        if (hasTargetGroup) {
            // Remove the target group (leave duty)
            // Use the leave message of the currently assigned group, not the found rank
            // Find the actual group the player has from LuckPerms nodes
            String currentGroup = user.getNodes().stream()
                .filter(InheritanceNode.class::isInstance)
                .map(InheritanceNode.class::cast)
                .filter(node -> configManager.getAllDutyRanksSorted().stream()
                    .anyMatch(r -> r.getToGroup().equals(node.getGroupName())))
                .map(InheritanceNode::getGroupName)
                .findFirst()
                .orElse(dutyRank.getToGroup());

            // Find the DutyRank for the current group to get the correct leave message
            DutyRank currentRank = configManager.getAllDutyRanksSorted().stream()
                .filter(r -> r.getToGroup().equals(currentGroup))
                .findFirst()
                .orElse(dutyRank);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', currentRank.getLeaveMessage()));
            
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), u -> {
                u.data().remove(InheritanceNode.builder(currentGroup).build());
            });
            
            debugLog("Player " + player.getName() + " left duty group: " + currentGroup);
        } else {
            // Add the target group (enter duty)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', dutyRank.getJoinMessage()));
            
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), u -> {
                // Remove any other duty groups first
                configManager.getAllDutyRanksSorted().forEach(r -> {
                    if (r.getToGroup() != null && !r.getToGroup().trim().isEmpty()) {
                        u.data().remove(InheritanceNode.builder(r.getToGroup()).build());
                    }
                });
                // Add the target group without removing default
                u.data().add(InheritanceNode.builder(dutyRank.getToGroup()).build());
            });
            
            debugLog("Player " + player.getName() + " entered duty group: " + dutyRank.getToGroup());
        }

        return true;
    }

    public Optional<DutyRank> findDutyRank(Player player) {
        // Debug: Log all permissions the player has
        plugin.getLogger().info("Checking permissions for player: " + player.getName());
        
        // Get all ranks sorted by priority (highest first)
        List<DutyRank> validRanks = configManager.getAllDutyRanksSorted().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()))
                .filter(rank -> {
                    boolean hasPermission = player.hasPermission(rank.getPermission());
                    plugin.getLogger().info("Rank " + rank.getKey() + " (" + rank.getPermission() + "): " + hasPermission);
                    return hasPermission;
                })
                .collect(Collectors.toList());
        
        if (validRanks.isEmpty()) {
            plugin.getLogger().warning("No valid duty ranks found for player: " + player.getName());
        }
        
        return validRanks.stream().findFirst();
    }

    public boolean setAdminRank(UUID uuid, String rankKey) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;

        Optional<DutyRank> rank = configManager.getDutyRankByKey(rankKey);
        if (!rank.isPresent()) {
            plugin.getLogger().warning("Invalid rank key: " + rankKey);
            return false;
        }

        // Remove all existing duty role permissions
        configManager.getAllDutyRanksSorted().forEach(r -> {
            luckPerms.getUserManager().modifyUser(uuid, u -> {
                u.data().remove(PermissionNode.builder("unsubduty.role." + r.getKey()).build());
            });
        });

        // Add new role permission
        luckPerms.getUserManager().modifyUser(uuid, u -> {
            u.data().add(PermissionNode.builder("unsubduty.role." + rankKey).build());
            u.data().add(PermissionNode.builder("unsubduty.use").build());
        });

        plugin.getLogger().info("Set admin rank for " + player.getName() + " to " + rankKey);
        return true;
    }

    public List<Player> getAllDutyAdmins() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> findDutyRank(player).isPresent())
                .collect(Collectors.toList());
    }

    public String getRankName(Player player) {
        Optional<DutyRank> rank = findDutyRank(player);
        if (!rank.isPresent()) {
            plugin.getLogger().warning("No rank found for player: " + player.getName());
            return "Unknown";
        }
        
        String rankName = rank.get().getRankName();
        if (rankName == null || rankName.trim().isEmpty()) {
            plugin.getLogger().warning("Empty rank name for player: " + player.getName());
            return "Unknown";
        }
        
        return rankName;
    }

    public boolean isPlayerInDuty(Player player, DutyRank rank) {
        if (rank == null) return false;
        
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return false;
        
        return user.getNodes().stream()
                .filter(InheritanceNode.class::isInstance)
                .map(InheritanceNode.class::cast)
                .anyMatch(node -> node.getGroupName().equals(rank.getToGroup()));
    }
}
