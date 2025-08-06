package com.tlagx.unsubduty.models;

public class AdminData {
    private String playerName;
    private String activeRank;
    private boolean inDuty;
    private boolean hideStatus;

    public AdminData(String playerName, String activeRank, boolean inDuty, boolean hideStatus) {
        this.playerName = playerName;
        this.activeRank = activeRank;
        this.inDuty = inDuty;
        this.hideStatus = hideStatus;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getActiveRank() {
        return activeRank;
    }

    public void setActiveRank(String activeRank) {
        this.activeRank = activeRank;
    }

    public boolean isInDuty() {
        return inDuty;
    }

    public void setInDuty(boolean inDuty) {
        this.inDuty = inDuty;
    }

    public boolean isHideStatus() {
        return hideStatus;
    }

    public void setHideStatus(boolean hideStatus) {
        this.hideStatus = hideStatus;
    }
}
