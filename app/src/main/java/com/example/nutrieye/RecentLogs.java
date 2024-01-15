package com.example.nutrieye;

public class RecentLogs {

    private String logsIcon;
    private String actionLabel;
    private String actionCategory;
    private String logsTimestamp;

    public String getLogsIcon() {
        return logsIcon;
    }

    public void setLogsIcon(String logsIcon) {
        this.logsIcon = logsIcon;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public String getActionCategory() {
        return actionCategory;
    }

    public void setActionCategory(String actionCategory) {
        this.actionCategory = actionCategory;
    }

    public String getLogsTimestamp() {
        return logsTimestamp;
    }

    public void setLogsTimestamp(String logsTimestamp) {
        this.logsTimestamp = logsTimestamp;
    }

    public RecentLogs(String logsIcon, String actionLabel, String actionCategory, String logsTimestamp) {
        this.logsIcon = logsIcon;
        this.actionLabel = actionLabel;
        this.actionCategory = actionCategory;
        this.logsTimestamp = logsTimestamp;
    }

    public RecentLogs() {
    }
}

