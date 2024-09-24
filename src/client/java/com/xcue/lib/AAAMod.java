
package com.xcue.lib;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public abstract class AAAMod {
    protected boolean enabled;
    protected MinecraftClient client;

    public AAAMod() {
        //TODO:  Extract to config
        this.enabled = false;
        this.client = MinecraftClient.getInstance();
    }
    public boolean isEnabled() {
        return this.enabled;
    }

    public void enable() {
        //TODO:  Extract to config
        this.enabled = true;

        ClientPlayerEntity p = client.player;
        if (p == null) return;

        p.sendMessage(Text.of(String.format("Enabled %s", getName())));
    }

    public void disable() {
        //TODO:  Extract to config
        this.enabled = false;

        ClientPlayerEntity p = client.player;
        if (p == null) return;

        p.sendMessage(Text.of(String.format("Disabled %s", getName())));
    }
    public void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }
    public String getName() {
        return getClass().getName();
    }
    public abstract void init();
}
