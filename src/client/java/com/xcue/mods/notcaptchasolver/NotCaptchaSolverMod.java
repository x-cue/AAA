package com.xcue.mods.notcaptchasolver;

import com.xcue.Keybinds;
import com.xcue.lib.AAAMod;
import com.xcue.lib.Captcha;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

public class NotCaptchaSolverMod extends AAAMod {
    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (Keybinds.CAPTCHA_SOLVER.wasPressed()) toggle();

            if (isEnabled()) Captcha.tick();
        });
    }
}
