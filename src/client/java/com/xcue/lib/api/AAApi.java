package com.xcue.lib.api;

import com.google.gson.Gson;
import com.xcue.AAAClient;
import com.xcue.lib.api.request.LogServerJoinRequest;
import com.xcue.lib.api.response.CheckWhitelistResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AAApi {
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "https://aaaapi.us-east-2.elasticbeanstalk.com/api";

    public static CompletableFuture<CheckWhitelistResult> isWhitelisted(@NotNull String playerName) {
        String url = BASE_URL + "/statistics/check-whitelist/" + playerName;

        return Http.getJson(url, CheckWhitelistResult.class);
    }

    public static CompletableFuture<String> logServerJoin(@NotNull String serverIp, @NotNull String playerName) {
        String url = BASE_URL + "/statistics/server-join";
        String jsonRequest = gson.toJson(new LogServerJoinRequest(serverIp, playerName));
        AAAClient.LOGGER.info("Server join json: {}", jsonRequest);
        return Http.post(url, jsonRequest);
    }
}
