package com.tlagx.unsubduty.models;

public class DutyRank {
    private final String key;
    private final String permission;
    private final String toGroup;
    private final String rankName;
    private final int priority;
    private final String joinMessage;
    private final String leaveMessage;

    public DutyRank(String key, String permission, String toGroup, String rankName, 
                   int priority, String joinMessage, String leaveMessage) {
        this.key = key;
        this.permission = permission;
        this.toGroup = toGroup;
        this.rankName = rankName;
        this.priority = priority;
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
    }

    public String getKey() {
        return key;
    }

    public String getPermission() {
        return permission;
    }

    public String getToGroup() {
        return toGroup;
    }

    public String getRankName() {
        return rankName;
    }

    public int getPriority() {
        return priority;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }
}
