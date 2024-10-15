package com.xcue.lib;

import com.xcue.lib.api.AAApi;

public class Authentication {
    protected static boolean isAuthenticated = false;

    public static void reAuthenticate(String playerName) {
        AAApi.isWhitelisted(playerName).thenAccept(response -> {
            isAuthenticated = response.isWhitelisted;
        }).exceptionally(ex -> null);
    }

    public static boolean isAuthenticated() {
        return isAuthenticated;
    }
}
