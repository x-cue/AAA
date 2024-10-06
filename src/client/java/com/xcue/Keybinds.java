package com.xcue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public class Keybinds {
    public static void init(Logger logger) {
        logger.info("Registering AAA keybinds");
    }
    public static final KeyBinding NOT_PEACEFUL_SKILLING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Peaceful Skilling", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_O, // The keycode of the key
            "AAA" // The translation key of the keybinding's category.
    ));

     public static final KeyBinding NOT_AUTO_FISHER = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle AutoFishing", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_J, // The keycode of the key
            "AAA" // The translation key of the keybinding's category.
    ));

     public static final KeyBinding NOT_AFK = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle AFK Grinding", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_K, // The keycode of the key
            "AAA" // The translation key of the keybinding's category.
    ));

    public static final KeyBinding CAPTCHA_SOLVER = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Auto-Captcha Solver", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_SEMICOLON, // The keycode of the key
            "AAA" // The translation key of the keybinding's category.
    ));

    public static final KeyBinding NOT_AUTO_FISHER_SWAP_MODES = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Swap AutoFish Modes", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_COMMA, // The keycode of the key
            "AAA" // The translation key of the keybinding's category.
    ));
}
