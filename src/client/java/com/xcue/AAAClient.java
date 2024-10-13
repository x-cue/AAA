package com.xcue;

import com.xcue.lib.AAAMod;
import com.xcue.lib.Authentication;
import com.xcue.lib.api.AAApi;
import com.xcue.lib.configuration.Config;
import com.xcue.lib.encryption.Encryption;
import com.xcue.mods.notafk.NotAfkMod;
import com.xcue.mods.notautofisher.NotAutoFisherMod;
import com.xcue.mods.notcaptchasolver.NotCaptchaSolverMod;
import com.xcue.mods.notpeacefulskilling.NotPeacefulSkillingMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

public class AAAClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("aaa");
    private static final Map<String, AAAMod> mods = new HashMap<>();

    public static AAAMod mod(String name) {
        return mods.get(name);
    }

    public void onInitializeClient() {
        Config.load();
        Keybinds.init(LOGGER);
        // This is the entry-point for the client

        // Queue of mods (will initialize in the order they are created)
        // Suppliers are nice, they are essentially a factory function that will create
        // The object only when running .get()
        Queue<Supplier<AAAMod>> mods = new LinkedList<>() {{
            // This is a fancy way to create a new collection where you can quickly
            // Reference it. Outside of these braces, you would need to type mods.add
            add(NotAfkMod::new);
            add(NotPeacefulSkillingMod::new);
            add(NotAutoFisherMod::new);
            add(NotCaptchaSolverMod::new);
            //   ^ Shorthand lambda expression/func --> same as doing () -> new NotAfkMod()
            // You can use lambdas like that where you reference a class and which method to call
            // It's very useful!
        }};

        LOGGER.info("Initialising AAA mods...");

        // Initialize the mods in order
        while (mods.peek() != null) {
            // This is a weird way to do it, but I think it's neat, and it re-introduces
            // you to collections
            AAAMod mod = mods.poll().get();

            LOGGER.info("Initialising AAA.{}", mod.getName());

            mod.init();

            LOGGER.info("Done w/ AAA.{}", mod.getName());

            AAAClient.mods.put(mod.getName(), mod);
        }

        LOGGER.info("DONE initialising AAA Mods!");

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerInfo serverInfo = handler.getServerInfo();
            ClientPlayerEntity p = client.player;

            String address = serverInfo == null ? "unknown" : serverInfo.address;
            String playerName = p == null ? "unknown" : p.getName().getString();

            AAApi.logServerJoin(address, playerName).exceptionally(ex -> {
                LOGGER.info("Error occurred while joining server");
                return null;
            });

            Authentication.reAuthenticate(playerName);
        });
    }
}