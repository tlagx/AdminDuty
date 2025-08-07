package com.tlagx.unsubduty.services;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.tlagx.unsubduty.storage.UserStorage;

public class HideService {
    private final UserStorage userStorage;

    public HideService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void setHidden(UUID uuid, boolean hidden) {
        userStorage.setHidden(uuid, hidden);
    }

    public void toggleHidden(UUID uuid) {
        userStorage.toggleHidden(uuid);
    }

    public boolean isHidden(UUID uuid) {
        return userStorage.isHidden(uuid);
    }

    public boolean isHidden(Player player) {
        return isHidden(player.getUniqueId());
    }
}
