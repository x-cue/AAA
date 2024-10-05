package com.xcue.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xcue.AAAClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Captcha {
    private static TickTimer timer = new TickTimer();
    private static boolean isOpen;
    private static String itemToClick;

    public static boolean isOpen() {
        return isOpen;
    }

    private static boolean isCaptchaOpen() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.currentScreen == null || client.player == null) return false;
        Text title = client.currentScreen.getTitle();

        Pattern pattern = Pattern.compile("^CHALLENGE: ([\\w_]+)$");
        Matcher matcher = pattern.matcher(title.getString());

        if (!matcher.matches()) {
            isOpen = false;
            AAAClient.LOGGER.info("{} Not Matches", title.getString());
        } else {
            itemToClick = matcher.group(1);
            isOpen = client.currentScreen instanceof HandledScreen<?> screen;

            AAAClient.LOGGER.info("Click {}", itemToClick);
        }

        return isOpen;
    }

    public static void tick() {
        if (!isOpen && isCaptchaOpen()) {
            // First tick it's open
            // TODO play sound?
            // TODO cheater mode & highlighter?
            AAAClient.LOGGER.info("Starting Timer");
            timer.startWithSeconds(new Random().nextInt(4, 10), Captcha::solve);
        } else if (isOpen) {
            // Not the first tick it's open
            timer.tick();
            AAAClient.LOGGER.info("Ticking");
        }
    }

    public static void solve() {
        MinecraftClient client = MinecraftClient.getInstance();
        AAAClient.LOGGER.info("Starting Solve");

        if (isOpen) {
            HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;
            ScreenHandler handler = screen.getScreenHandler();
            List<ItemStack> stacks = handler.getStacks();

            AAAClient.LOGGER.info("Is handled screen");

            Optional<ItemStack> stack =
                    stacks.stream().filter(x -> x.getItem().getName().getString().equalsIgnoreCase(itemToClick)).findFirst();

            stacks.stream().map(x -> x.getItem().getName().getLiteralString()).forEach(AAAClient.LOGGER::info);
            // TODO change to match *most closely*
            if (stack.isPresent()) {
                AAAClient.LOGGER.info("Stack Present");

                int i = stacks.indexOf(stack.get());
                AAAClient.LOGGER.info("Index: {}", i);

                // Click the slot (0-indexed)
                client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 2,
                        SlotActionType.PICKUP, client.player);
            } else {
                AAAClient.LOGGER.warn("AAA: Could not find stack for {}", itemToClick);
            }
            // Find the index of the item, and click that slot
        }
    }
}
