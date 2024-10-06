package com.xcue.mods.notautofisher;

import com.xcue.Keybinds;
import com.xcue.lib.AAAMod;
import com.xcue.lib.Captcha;
import com.xcue.lib.TickTimer;
import com.xcue.lib.events.CaptchaSolvedCallback;
import com.xcue.lib.events.island.IslandAreaFishedOutCallback;
import com.xcue.mixin.client.FishingBobberEntityAccessorMixin;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

import java.util.Random;

public class NotAutoFisherMod extends AAAMod {
    boolean canCast = true;
    boolean canReel = false;
    int castDelay = 15;
    int reelDelay = 15;
    private final TickTimer autoCastTimer = new TickTimer();

    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        IslandAreaFishedOutCallback.EVENT.register(() -> {
            // Play Sound
            assert client.player != null;
            client.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1, 1);

            // Start Fish Timer
            autoCastTimer.startWithSeconds(new Random().nextInt(60, 120), this::cast);
        });

        CaptchaSolvedCallback.EVENT.register(() -> {
           canCast = true;
           canReel = true;
        });
    }

    private void tick(MinecraftClient client) {
        if (Keybinds.NOT_AUTO_FISHER.wasPressed()) toggle();
        if (!enabled || client.player == null) return;

//        // Temporary captcha detection
//        if (getModSetting("strict-mode", true)) {
//            if (client.currentScreen instanceof HandledScreen<?>) return;
//        }

        autoCastTimer.tick();

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
        if (client.player == null || client.interactionManager == null || Captcha.isOpen()) return;

        if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        } else if (client.player.getOffHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.OFF_HAND);
            client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
        }
    }

    private void cast() {
        autoCastTimer.stop();
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
