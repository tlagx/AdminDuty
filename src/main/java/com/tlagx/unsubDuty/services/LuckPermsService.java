package com.tlagx.unsubduty.services;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;

public class LuckPermsService {
    private final LuckPerms luckPerms;
    private final UserManager userManager;

    public LuckPermsService() {
        this.luckPerms = LuckPermsProvider.get();
        this.userManager = luckPerms.getUserManager();
    }

    public boolean addGroupToPlayer(Player player, String groupName) {
        try {
            CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
            User user = userFuture.get();
            
            InheritanceNode node = InheritanceNode.builder(groupName).value(true).build();
            user.data().add(node);
            
            userManager.saveUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGroupFromPlayer(Player player, String groupName) {
        try {
            CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
            User user = userFuture.get();
            
            InheritanceNode node = InheritanceNode.builder(groupName).value(true).build();
            user.data().remove(node);
            
            userManager.saveUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasGroup(Player player, String groupName) {
        try {
            CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
            User user = userFuture.get();
            
            return user.getNodes().stream()
                    .filter(node -> node instanceof InheritanceNode)
                    .map(node -> (InheritanceNode) node)
                    .anyMatch(node -> node.getGroupName().equalsIgnoreCase(groupName));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
