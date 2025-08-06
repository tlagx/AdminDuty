package com.tlagx.unsubduty.models;

import org.bukkit.configuration.ConfigurationSection;

public class DutyRank {
    private final String key;
    private final String permission;
    private final String fromGroup;
    private final String toGroup;
    private final String rankName;
    private final String joinMessage;
    private final String leaveMessage;
    private final int priority;

    public DutyRank(String key, ConfigurationSection config) {
        this.key = key;
        this.permission = config.getString("permission");
        this.fromGroup = config.getString("from-group");
        this.toGroup = config.getString("to-group");
        this.rankName = config.getString("rank-name");
        this.joinMessage = config.getString("join-message");
        this.leaveMessage = config.getString("leave-message");
        this.priority = config.getInt("priority");
    }

    public String getKey() { return key; }
    public String getPermission() { return permission; }
    public String getFromGroup() { return fromGroup; }
    public String getToGroup() { return toGroup; }
    public String getRankName() { return rankName; }
    public String getJoinMessage() { return joinMessage; }
    public String getLeaveMessage() { return leaveMessage; }
    public int getPriority() { return priority; }
}
