package com.xcue.mods.notafk;

import com.xcue.lib.Captcha;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

// Since no access modifier (private, protected, public) was specified, this class is package-private
// Meaning only classes in the same package can access it
class AutoClicker {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static int ticks = 0;
    private static BlockPos playerStartingPos;
    private static boolean enabled = false;


    public static void start(ClientPlayerEntity player) {
        ticks = 0;
        playerStartingPos = player.getBlockPos();
        enabled = true;
    }

    public static void stop(String reason) {
        enabled = false;
        playerStartingPos = null;

        if (client.player == null || reason.isBlank()) return;

        client.player.sendMessage(Text.literal("(!) Stopped AutoClicker. Reason: " + reason));
    }

    public static boolean isRunning() {
        return enabled;
    }

    public static void tick() {
        ticks++;

        if (ticks % 2 == 0) {
            // Left click again to cancel
            // TODO: Store a variable and make it so they have to click again after 20 ticks
            // OR say hasReleasedClick or something like that
            // TODO Then, update the cps to a configurable setting!
            if (client.mouse.wasLeftButtonClicked() && ticks != 0) {
                stop("Player manually stopped with left click");
                return;
            }

            click();
        }
    }

    public static BlockPos getPlayerStartingPos() {
        return playerStartingPos;
    }


    public static void click() {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        if (Captcha.isOpen()) {
            return;
        }

        HitResult hRes = client.crosshairTarget;

        if (hRes != null) {
            if (hRes.getType() == HitResult.Type.ENTITY) {
                Entity ent = ((EntityHitResult) hRes).getEntity();
                client.interactionManager.attackEntity(player, ent);
                return;
            }
        }

        stop("Player is no longer looking at an entity");
    }
}
