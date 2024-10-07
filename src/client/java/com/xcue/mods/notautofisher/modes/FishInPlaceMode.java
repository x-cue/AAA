package com.xcue.mods.notautofisher.modes;

import com.xcue.AAAClient;
import com.xcue.mods.notautofisher.NotAutoFisherMod;

import java.util.Random;

public class FishInPlaceMode extends NotAutoFisherMode {
    @Override
    public String getConfigKey() {
        return "fish-in-place";
    }

    @Override
    public void onAreaFishedOut() {
        NotAutoFisherMod notAF = (NotAutoFisherMod) AAAClient.mod("notautofishermod");

        timer.startWithSeconds(new Random().nextInt(60, 120), notAF::cast);
    }

    @Override
    public void onCaptchaSolved() {

    }

    @Override
    public void onCaptchaOpened() {

    }
}
