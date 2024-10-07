package com.xcue.mods.notautofisher.modes;

import com.xcue.AAAClient;
import com.xcue.mods.notautofisher.NotAutoFisherMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FishLeftRightMode extends NotAutoFisherMode {
    private boolean isMovingLeft = true;

    @Override
    public String getConfigKey() {
        return "left-right";
    }

    @Override
    public void onAreaFishedOut() {
        if (startMoving()) {
            timer.startWithTicks(18, this::stopMovingAndCast);
        } else {
            AAAClient.LOGGER.info("Player is enclosed. Stopped moving to fish.");
        }
        // If block to left, move right
        // Store boolean isMovingLeft to keep track of the line
        // If block to right, move left
        // Now set isMovingLeft to false
        // If block on both sides of player, stop running

        // Hold left/right for 10 ticks
    }

    @Override
    public void onCaptchaSolved() {
        MinecraftClient client = MinecraftClient.getInstance();

        // When captcha is solved, teleport home and then cast after some ticks.
        if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD || client.player.getOffHandStack().getItem() == Items.FISHING_ROD) {
            client.player.networkHandler.sendChatCommand("home");

            stopMovingAndCast();
        }

    }

    @Override
    public void onCaptchaOpened() {
        stopTimer();
    }

    /**
     * Start moving left or right
     *
     * @return Whether the action was performed
     */
    private boolean startMoving() {
        if (isMovingLeft) {
            if (tryMoveLeft()) {
                return true;
            } else {
                return tryMoveRight();
            }
        } else {
            if (tryMoveRight()) {
                return true;
            } else {
                return tryMoveLeft();
            }
        }
    }

    private boolean tryMoveLeft() {
        if (noBlocksLeft()) {
            isMovingLeft = true;
            MinecraftClient.getInstance().options.leftKey.setPressed(true);

            return true;
        }

        return false;
    }

    private boolean tryMoveRight() {
        if (noBlocksRight()) {
            isMovingLeft = false;
            MinecraftClient.getInstance().options.rightKey.setPressed(true);

            return true;
        }

        return false;
    }

    private boolean noBlocksRight() {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        ClientPlayerEntity player = client.player;
        BlockPos feetPos = player.getBlockPos(); // Player's feet position

        // Get the player's facing direction
        Direction direction = player.getHorizontalFacing();

        // Calculate the BlockPos to the right of the player's feet
        BlockPos rightBlockFeet = feetPos.offset(direction.rotateYClockwise()).toImmutable();

        // Calculate the BlockPos to the right of the player's head
        BlockPos rightBlockHead =
                rightBlockFeet.up(); // Offset by one block height for the head position

        // Log the positions for debugging
        System.out.println("Right Block Feet: " + rightBlockFeet);
        System.out.println("Right Block Head: " + rightBlockHead);

        // Check if both blocks are air
        return world.getBlockState(rightBlockHead).isAir() && world.getBlockState(rightBlockFeet).isAir();
    }


    private boolean noBlocksLeft() {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        ClientPlayerEntity player = client.player;
        BlockPos feetPos = player.getBlockPos(); // Player's feet position

        // Get the player's facing direction
        Direction direction = player.getHorizontalFacing();

        // Calculate the BlockPos to the left of the player's feet
        BlockPos leftBlockFeet = feetPos.offset(direction.rotateYCounterclockwise()).toImmutable();

        // Calculate the BlockPos to the left of the player's head
        BlockPos leftBlockHead =
                leftBlockFeet.up(); // Offset by one block height for the head position

        // Log the positions for debugging
        System.out.println("Left Block Feet: " + leftBlockFeet);
        System.out.println("Left Block Head: " + leftBlockHead);

        // Check if both blocks are air
        return world.getBlockState(leftBlockHead).isAir() && world.getBlockState(leftBlockFeet).isAir();
    }

    @Override
    public void stopTimer() {
        stopMoving();

        timer.stop();
    }

    private void stopMoving() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
    }

    private void stopMovingAndCast() {
        stopMoving();

        timer.startWithSeconds(1,  ((NotAutoFisherMod) AAAClient.mod("notautofishermod"))::cast);
    }
}
