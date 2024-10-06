package com.xcue.mods.notcaptchasolver;

import com.xcue.Keybinds;
import com.xcue.lib.AAAMod;
import com.xcue.lib.Captcha;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class NotCaptchaSolverMod extends AAAMod {
    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (Keybinds.CAPTCHA_SOLVER.wasPressed()) toggle();

            if (isEnabled()) Captcha.tick();
        });
    }
}
