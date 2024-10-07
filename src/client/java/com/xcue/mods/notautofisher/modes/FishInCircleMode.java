package com.xcue.mods.notautofisher.modes;

import com.xcue.AAAClient;
import com.xcue.mods.notautofisher.NotAutoFisherMod;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class FishInCircleMode extends NotAutoFisherMode {
    @Override
    public String getConfigKey() {
        return "fish-in-circle";
    }

    @Override
    public void onAreaFishedOut() {
        // Turn Player 90 degrees
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        amountTurned = 0;
        startingYaw = client.player.getYaw();
        AAAClient.LOGGER.info("StartingYaw {}", startingYaw);

        endingYaw = subtractWithWrap((int)startingYaw, (int)TURN_BY, -179, 180);

        AAAClient.LOGGER.info("EndingYaw:  {}", endingYaw);
        timer.startWithTicks(1, this::moveYaw);
    }

    @Override
    public void onCaptchaSolved() {

    }

    @Override
    public void onCaptchaOpened() {

    }

    double startingYaw;
    double endingYaw;
    double amountTurned;
    final static double TURN_BY = 90;

    public void moveYaw() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        float tickDelta = client.getTickDelta();
        AAAClient.LOGGER.info("tickDelta {}", tickDelta);
        // So we have our ending yaw...
        // Now turn the player each tick a random amount
        // TODO: Vary pitch up/down by ~5-10?

        // Turn Player 90 degrees
        if (client.player == null) return;

        double newYaw = subtractWithWrap((int) client.player.getYaw(), new Random().nextInt(5, 15), -180,
                (int) endingYaw);

        AAAClient.LOGGER.info("pre-calc newYaw {}", newYaw);

        if (endingYaw > 0) {
            newYaw = Math.max(endingYaw, newYaw);
        } else {
            newYaw = Math.min(endingYaw, newYaw);
        }

        AAAClient.LOGGER.info("newYaw {}", newYaw);

        /*float lerpedYaw = (float) (tickDelta == 1.0F ? newYaw : MathHelper.lerp(tickDelta, client.player.prevYaw,
                newYaw));
        AAAClient.LOGGER.info("lerpedYaw {}", lerpedYaw);
        client.player.setYaw(lerpedYaw);

        // Update Amount Turned
        double amountTurned = lerpedYaw - startingYaw;*/
        client.player.setYaw((float) newYaw);
        double amountTurned = newYaw - startingYaw;
        AAAClient.LOGGER.info("Turned pre-calc {}", amountTurned);
        if (amountTurned > 180) {
            amountTurned = -180 + (amountTurned - 180);
        } else if (amountTurned < -180) {
            amountTurned = 180 - (amountTurned + 180);
        }

        AAAClient.LOGGER.info("Turned {}", amountTurned);
        this.amountTurned += Math.abs(amountTurned);
        AAAClient.LOGGER.info("Total Turned {}", this.amountTurned);

        if (this.amountTurned < 90) {
            timer.startWithTicks(2, this::moveYaw);
        } else {
            ((NotAutoFisherMod) AAAClient.mod("notautofishermod")).cast();
        }
    }

    // A little off, but it gets the job done...
    // TODO: Update to work properly, and move to lib
    private static int subtractWithWrap(int value, int subtractAmount, int min, int max) {
        // Check if the provided range is valid
        if (min >= max) {
            throw new IllegalArgumentException("Invalid range: min must be less than max.");
        }

        // Calculate the range size
        int rangeSize = max - min + 1;

        // Adjust the value to be within the range
        value = (value - min + rangeSize) % rangeSize + min;

        // Perform the subtraction
        int result = value - subtractAmount;

        // Wrap around if the result is below min
        // Instead of just one wrap, we need to correctly wrap multiple times
        if (result < min) {
            // Calculate how many full ranges to add to result
            int wraps = (min - result + rangeSize - 1) / rangeSize; // Add one extra if it's negative
            result += wraps * rangeSize + 1; // Adjust result by the number of wraps
        }

        return result;
    }
}
