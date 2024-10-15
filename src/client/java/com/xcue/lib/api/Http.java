package com.xcue.lib.api;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

class Http {
    private static final Gson gson = new Gson();

    public static CompletableFuture<String> get(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BufferedReader reader = getBufferedReader(urlString);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } catch (Exception e) {
                throw new RuntimeException("Error during HTTP call", e);
            }
        });
    }

    public static CompletableFuture<String> post(String urlString, String jsonInputString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = getUrlConnection(urlString, jsonInputString);

                // Check response code
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_CREATED) {
                    throw new RuntimeException("Failed: HTTP error code: " + responseCode);
                }

                // Read response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } catch (Exception e) {
                throw new RuntimeException("Error during HTTP POST call", e);
            }
        });
    }

    private static @NotNull HttpURLConnection getUrlConnection(String urlString, String jsonInputString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Client-Sender", "AAA-fabric-mod");
        connection.setDoOutput(true);

        // Write the JSON input string to the connection
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.writeBytes(jsonInputString);
            writer.flush();
        }
        return connection;
    }

    private static @NotNull BufferedReader getBufferedReader(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Client-Sender", "AAA-fabric-mod");

        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed: HTTP error code: " + responseCode);
        }

        // Read response
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public static <T> CompletableFuture<T> getJson(String urlString, Class<T> clazz) {
        return get(urlString).thenApply(response -> gson.fromJson(response, clazz));
    }
}
