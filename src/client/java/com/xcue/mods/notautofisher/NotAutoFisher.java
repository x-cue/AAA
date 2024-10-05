package com.xcue.mods.notautofisher;

import com.xcue.Keybinds;
import com.xcue.lib.AAAMod;
import com.xcue.mixin.client.FishingBobberEntityAccessorMixin;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.Random;

public class NotAutoFisher extends AAAMod {
    boolean canCast = true;
    boolean canReel = false;
    int castDelay = 15;
    int reelDelay = 15;

    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        if (Keybinds.NOT_AUTO_FISHER.wasPressed()) toggle();
        if (!enabled || client.player == null) return;

        // Temporary captcha detection
        if (getModSetting("strict-mode", true)) {
            if (client.currentScreen instanceof HandledScreen<?>) return;
        }

        if (canCast) {
            // Cast Logic
            if (client.player.fishHook == null && castDelay == 0) {
                cast();
            } else if (client.player.fishHook == null) {
                castDelay--;
            }
        }

        if (canReel) {
            // Reel Logic
            // Fish is on hook
            if (client.player.fishHook != null && ((FishingBobberEntityAccessorMixin) client.player.fishHook).getCaughtFish()) {
                // Reel timer is up
                if (reelDelay == 0) {
                    reel();
                } else if (reelDelay > 0) {
                    reelDelay--;
                }
            }
        } else if (client.player.fishHook == null) {
            canReel = true;
        } else {
            canCast = true;
        }
    }

    public void useRod() {
        if (client.player == null || client.interactionManager == null) return;

        if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        } else if (client.player.getOffHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.OFF_HAND);
            client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
        }
    }

    private void cast() {
        canCast = false;
        canReel = true;
        useRod();
        castDelay = new Random().nextInt(6, 16);
    }

    private void reel() {
        canReel = false;
        canCast = true;
        useRod();
        reelDelay = new Random().nextInt(6, 12);
    }
}
