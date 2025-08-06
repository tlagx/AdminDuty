package com.tlagx.unsubduty.services;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.tlagx.unsubduty.UnsubDuty;
import com.tlagx.unsubduty.config.ConfigManager;
import com.tlagx.unsubduty.models.AdminData;
import com.tlagx.unsubduty.models.DutyRank;
import com.tlagx.unsubduty.storage.AdminStorage;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;

public class DutyService {
    private final UnsubDuty plugin;
    private final LuckPerms luckPerms;
    private final ConfigManager configManager;
    private final AdminStorage adminStorage;

    public DutyService(UnsubDuty plugin, LuckPerms luckPerms, ConfigManager configManager, AdminStorage adminStorage) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.configManager = configManager;
        this.adminStorage = adminStorage;
    }

    public boolean toggleDuty(Player player) {
        UUID uuid = player.getUniqueId();
        AdminData data = adminStorage.getAdminData(uuid);
        
        if (data == null) {
            Optional<DutyRank> rank = findDutyRank(player);
            if (!rank.isPresent()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    configManager.getDefaultAccessMessage()));
                return false;
            }
            
            data = new AdminData(player.getName(), rank.get().getKey(), false, false);
            adminStorage.setAdminData(uuid, data);
        }

        DutyRank rank = configManager.getDutyRankByKey(data.getActiveRank()).orElse(null);
        if (rank == null) return false;

        if (data.isInDuty()) {
            // Leave duty
            data.setInDuty(false);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', rank.getLeaveMessage()));
            
            // Move back to from-group
            UserManager userManager = luckPerms.getUserManager();
            User user = userManager.getUser(player.getUniqueId());
            if (user != null) {
                luckPerms.getUserManager().modifyUser(player.getUniqueId(), u -> {
                    u.data().add(InheritanceNode.builder(rank.getFromGroup()).build());
                    u.data().remove(InheritanceNode.builder(rank.getToGroup()).build());
                });
            }
        } else {
            // Enter duty
            data.setInDuty(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', rank.getJoinMessage()));
            
            // Move to duty group
            UserManager userManager = luckPerms.getUserManager();
            User user = userManager.getUser(player.getUniqueId());
            if (user != null) {
                luckPerms.getUserManager().modifyUser(player.getUniqueId(), u -> {
                    u.data().add(InheritanceNode.builder(rank.getToGroup()).build());
                    u.data().remove(InheritanceNode.builder(rank.getFromGroup()).build());
                });
            }
        }
        
        adminStorage.save();
        return true;
    }

    public Optional<DutyRank> findDutyRank(Player player) {
        return configManager.getAllDutyRanksSorted().stream()
                .filter(rank -> player.hasPermission(rank.getPermission()))
                .findFirst();
    }

    public void setAdminRank(UUID uuid, String rankKey) {
        AdminData data = adminStorage.getAdminData(uuid);
        if (data == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                data = new AdminData(player.getName(), rankKey, false, false);
            } else {
                return;
            }
        }
        
        data.setActiveRank(rankKey);
        adminStorage.save();
    }

    public void setHideStatus(UUID uuid, boolean hide) {
        AdminData data = adminStorage.getAdminData(uuid);
        if (data != null) {
            data.setHideStatus(hide);
            adminStorage.save();
        }
    }

    public Map<UUID, AdminData> getAllAdminData() {
        return adminStorage.getAllAdminData();
    }
}
