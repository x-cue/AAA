package com.xcue.mods.notautofisher.modes;

import com.xcue.lib.TickTimer;

public abstract class NotAutoFisherMode {
    public NotAutoFisherMode() {
        this.timer = new TickTimer();
    }

    public void stopTimer() {
        this.timer.stop();
    }

    public final void tick() {
        this.timer.tick();
    }

    protected TickTimer timer;
    public abstract String getConfigKey();
    public abstract void onAreaFishedOut();
    public abstract void onCaptchaSolved();
    public abstract void onCaptchaOpened();
}
