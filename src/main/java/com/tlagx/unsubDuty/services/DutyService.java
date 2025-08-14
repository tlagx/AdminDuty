package com.tlagx.unsubduty.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.config.ConfigManager;
import com.tlagx.unsubduty.models.DutyRank;

public class DutyService {
    private final ConfigManager configManager;
    private final PermissionManager permissionManager;
    private final LuckPermsService luckPermsService;
    private boolean debug = false;

    public DutyService(ConfigManager configManager, PermissionManager permissionManager, LuckPermsService luckPermsService) {
        this.configManager = configManager;
        this.permissionManager = permissionManager;
        this.luckPermsService = luckPermsService;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void debugLog(String message) {
        if (debug) {
            System.out.println("[DEBUG] " + message);
        }
    }

    public boolean toggleDuty(Player player) {
        Optional<DutyRank> rank = findDutyRank(player);
        if (!rank.isPresent()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configManager.getDefaultAccessMessage()));
            return false;
        }

        DutyRank dutyRank = rank.get();
        
        if (dutyRank.getToGroup() == null || dutyRank.getToGroup().trim().isEmpty()) {
            player.sendMessage(ChatColor.RED + "Ошибка: группа для ранга не настроена.");
            System.out.println("Duty rank " + dutyRank.getKey() + " has null or empty toGroup!");
            return false;
        }

        // Handle special case for OP status
        if ("OP".equalsIgnoreCase(dutyRank.getToGroup())) {
            if (player.isOp()) {
                // Remove OP status
                player.setOp(false);
                String leaveMessage = dutyRank.getLeaveMessage();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage));
                debugLog("Player " + player.getName() + " OP status removed");
            } else {
                // Add OP status
                player.setOp(true);
                String joinMessage = dutyRank.getJoinMessage();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
                debugLog("Player " + player.getName() + " OP status granted");
            }
            return true;
        }

        // Handle regular LuckPerms groups
        boolean hasGroupInLuckPerms = luckPermsService.hasGroup(player, dutyRank.getToGroup());

        if (hasGroupInLuckPerms) {
            // Удаляем группу из LuckPerms
            if (luckPermsService.removeGroupFromPlayer(player, dutyRank.getToGroup())) {
                String leaveMessage = dutyRank.getLeaveMessage();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage));
                debugLog("Player " + player.getName() + " removed from LuckPerms group: " + dutyRank.getToGroup());
            } else {
                player.sendMessage(ChatColor.RED + "Ошибка при удалении группы из LuckPerms");
                return false;
            }
        } else {
            // Добавляем группу в LuckPerms
            if (luckPermsService.addGroupToPlayer(player, dutyRank.getToGroup())) {
                String joinMessage = dutyRank.getJoinMessage();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
                debugLog("Player " + player.getName() + " added to LuckPerms group: " + dutyRank.getToGroup());
            } else {
                player.sendMessage(ChatColor.RED + "Ошибка при добавлении группы в LuckPerms");
                return false;
            }
        }

        return true;
    }

    public Optional<DutyRank> findDutyRank(Player player) {
        String role = permissionManager.getUserRole(player.getName());
        if (role == null) {
            debugLog("Player " + player.getName() + " has no role assigned in perms.yml");
            return Optional.empty();
        }
        
        debugLog("Player " + player.getName() + " has role: " + role);
        
        Optional<DutyRank> dutyRank = configManager.getAllDutyRanksSorted().stream()
                .filter(rank -> rank.getKey().equalsIgnoreCase(role))
                .findFirst();
                
        if (!dutyRank.isPresent()) {
            debugLog("Role '" + role + "' for player " + player.getName() + " not found in config.yml duty-levels");
        }
        
        return dutyRank;
    }

    public boolean setAdminRank(UUID uuid, String rankKey) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;

        permissionManager.setUserRole(player.getName(), rankKey);
        return true;
    }

    public String getRankName(Player player) {
        String role = permissionManager.getUserRole(player.getName());
        if (role == null) return "Unknown";
        
        DutyRank rank = configManager.getDutyRankByKey(role).orElse(null);
        return rank != null ? rank.getRankName() : "Unknown";
    }

    public List<Player> getAllAdmins() {
        // Enhanced method to detect all admins including those hidden by third-party plugins
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> permissionManager.getUserRole(player.getName()) != null)
                .collect(Collectors.toList());
    }

    public boolean isPlayerInDuty(Player player, DutyRank rank) {
        if (rank == null) return false;
        
        // Handle special case for OP status
        if ("OP".equalsIgnoreCase(rank.getToGroup())) {
            return player.isOp();
        }
        
        return luckPermsService.hasGroup(player, rank.getToGroup());
    }
}
