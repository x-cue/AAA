
package com.xcue.lib;

import com.xcue.lib.configuration.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.logging.Logger;

public abstract class AAAMod {
    private boolean enabled;
    protected final MinecraftClient client;
    public final Logger logger;

    public <T> T getModSetting(String key, T def) {
        return Config.get(String.format("aaa.%s.%s", getName(), key), def);
    }

    public <T> void setModSetting(String key, T val) {
        Config.set(String.format("aaa.%s.%s", getName(), key), val);
    }

    public AAAMod() {
        this.enabled = getModSetting("enabled", false);
        this.client = MinecraftClient.getInstance();
        this.logger = Logger.getLogger("aaa." + getName());
    }

    public boolean isEnabled() {
        return Authentication.isAuthenticated() && this.enabled;
    }

    public void enable() {
        setModSetting("enabled", true);
        this.enabled = true;

        ClientPlayerEntity p = client.player;
        if (p == null) return;

        p.sendMessage(Text.of(String.format("Enabled %s", getName())));
    }

    public void disable() {
        setModSetting("enabled", false);
        this.enabled = false;

        ClientPlayerEntity p = client.player;
        if (p == null) return;

        p.sendMessage(Text.of(String.format("Disabled %s", getName())));
    }

    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public String getName() {
        return getClass().getSimpleName().toLowerCase();
    }

    public abstract void init();
}
