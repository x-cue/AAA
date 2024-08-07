package com.xcue.mods.notafk;

import com.xcue.mixin.client.ClientPlayerEntityExtension;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class NotAfkMod implements ClientModInitializer {
    private static final KeyBinding keyBinding;

    static {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "AAA", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_EQUAL, // The keycode of the key
                "Misc" // The translation key of the keybinding's category.
        ));
    }

    public NotAfkMod() {
        instance = this;
    }

    private static NotAfkMod instance;

    public static NotAfkMod getInstance() {
        return instance;
    }

    private boolean allowAttack = true;
    private boolean enabled = false;

    public void enable() {
        this.enabled = true;
    }

    public void enableAttacking() {
        this.allowAttack = true;
    }

    public void disableAttacking() {
        this.allowAttack = false;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isAttackingAllowed() {
        return this.allowAttack;
    }

    public static List<String> whitelist = List.of("me", "minecool", "largerbean", "xqgamers", "melissagrr", "botsdown", "imtight", "exalted173", "sale_sal", "fatmonkey101");

    @Override
    public void onInitializeClient() {
//        When staff "near" you stop hitting after few seconds
//        Someone messages the player, 10 diff responses (whatsup nig 0.01% chance)
//        After few seconds, stop clicking and message them.
//        Waits til they gone for a minute and
//        ~~If you teleported, wait a minute and a half or some shit and /home, and resume clicking~~
        // ^ NVM BC THE STAFF IS NEAR YOU THEN DUH
//        1% chance*
//                If near you for 5 mins just log off
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if (keyBinding.wasPressed()) {
                if (isEnabled()) {
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("Disabled AAA"));
                    disable();
                } else {
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("Enabled AAA"));
                    enable();
                }
            }

            if (!isEnabled()) return;

            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            if (p == null) return;

            List<Entity> nearbyPlayers = ((ClientPlayerEntityExtension) p).aAA$getNearbyEntities(p, 10, EntityType.PLAYER);

            // Toggle hit
            if (!nearbyPlayers.isEmpty()) {
                if (nearbyPlayers.stream().map(x -> ((x.getName().getString().toLowerCase()))).noneMatch(whitelist::contains)) {
                    MinecraftClient.getInstance().close();
                }

                disableAttacking();
            } else {
                enableAttacking();
            }
        }));

        // Message or Nearby
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> isAttackingAllowed() ? ActionResult.PASS : ActionResult.FAIL);
        AttackEntityCallback.EVENT.register(((player, world, hand, entity, hitResult) -> isAttackingAllowed() ? ActionResult.PASS : ActionResult.FAIL));
    }
}
