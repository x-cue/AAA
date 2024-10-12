package com.xcue.lib.api.request;

import com.google.gson.annotations.SerializedName;

public class LogServerJoinRequest {
    public LogServerJoinRequest(String serverIp, String playerName) {
        this.serverIp = serverIp;
        this.playerName = playerName;
    }

    @SerializedName("player")
    public String playerName;

    @SerializedName("server")
    public String serverIp;
}
