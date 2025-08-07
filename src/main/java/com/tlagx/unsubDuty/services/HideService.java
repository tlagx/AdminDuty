package com.tlagx.unsubduty.services;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public class HideService {
    private final Set<UUID> hiddenPlayers = new HashSet<>();

    public void setHidden(UUID uuid, boolean hidden) {
        if (hidden) {
            hiddenPlayers.add(uuid);
        } else {
            hiddenPlayers.remove(uuid);
        }
    }

    public boolean isHidden(UUID uuid) {
        return hiddenPlayers.contains(uuid);
    }

    public boolean isHidden(Player player) {
        return isHidden(player.getUniqueId());
    }
}
