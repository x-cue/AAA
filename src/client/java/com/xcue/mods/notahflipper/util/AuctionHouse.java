package com.xcue.mods.notahflipper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class AuctionHouse {
    private static boolean scanItems = false;

    private AuctionHouse() {

    }

    public static void enable() {
        scanItems = true;
    }

    public static void disable(){
        scanItems = false;
    }

    public static boolean isEnabled(){
        return scanItems;
    }

    public static boolean isOpen(@NotNull MinecraftClient client) {
        if(!scanItems || client.currentScreen == null) return false;
        Text title = client.currentScreen.getTitle();
        if(!Objects.equals(title.getString(), "Cosmic Auction House")) {
            return false;
        }
        return client.currentScreen instanceof HandledScreen<?>;
    }

    public static boolean isHistoryOpen(@NotNull MinecraftClient client) {
        if(!scanItems || client.currentScreen == null) return false;
        Text title = client.currentScreen.getTitle();
        if(!Objects.equals(title.getString(), "Recently Sold Auction Items")) {
            return false;
        }

        client.player.sendMessage(Text.literal("Yeah this MIGHT be history tab"));
        return client.currentScreen instanceof HandledScreen<?>;
    }
}
