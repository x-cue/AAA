package com.xcue.mods.notpeacefulskilling;

import com.xcue.lib.AAAMod;
import com.xcue.lib.extensions.ClientPlayerEntityExtension;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Random util to make it easier to grind
 * Hides nearby when holding tools
 */
public class NotPeacefulSkillingMod extends AAAMod {
    private static final int RADIUS = 5;
    private static List<Entity> hiddenPlayers = List.of();
    private static final List<String> keywords = List.of("hoe", "shovel", "sword", "axe");
    private static final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Nearby Players", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_O, // The keycode of the key
            "AAA" // The translation key of the keybinding's category.
    ));
    public static List<Entity> getHiddenPlayers() {
        return hiddenPlayers;
    }

    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if (keyBinding.wasPressed()) toggle();
            if (!enabled) return;

            ClientPlayerEntity p = client.player;
            if (p == null) return;

            String heldItem = p.getMainHandStack().getItem().getName().getString().toLowerCase();
            if (keywords.stream().anyMatch(heldItem::contains)) {
                hiddenPlayers = ((ClientPlayerEntityExtension) p).aAA$getNearbyEntities(RADIUS, EntityType.PLAYER);
            } else {
                hiddenPlayers = List.of();
            }
        }));
    }
}
