package com.xcue.mods.notautofisher.modes;

import com.xcue.AAAClient;

import com.xcue.lib.Captcha;
import com.xcue.mods.notautofisher.NotAutoFisherMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FishLeftRightMode extends NotAutoFisherMode {
    private List<Integer> slotsToDrop;
    private boolean isMovingLeft = true;
    MinecraftClient client = MinecraftClient.getInstance();
    Boolean inServer = false;
    Boolean isFishing = false;
    Boolean readyToFish = false;


    @Override
    public String getConfigKey() {
        return "left-right";
    }

    @Override
    public void onAreaFishedOut() {
        if (startMoving()) {
            timer.startWithTicks(18, this::stopMovingAndCast);
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
                if (!hasDroppedInv) {
                    // Drop inventory except fishing rod
                    List<ItemStack> invItems = client.player.getInventory().main;
                    slotsToDrop =
                            invItems.stream()
                                    .filter(x -> x.getItem() != Items.FISHING_ROD && !x.isEmpty())
                                    .map(invItems::indexOf)
                                    .filter(i -> i > 8)
                                    .collect(Collectors.toCollection(ArrayList::new));
                    //slotsToDrop.forEach(x -> AAAClient.LOGGER.info("{}", x));
                    // TODO update item whitelist

                    if (!slotsToDrop.isEmpty()) {
                        return false;
                    }
                }

                hasDroppedInv = false;

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
            client.options.leftKey.setPressed(true);

            return true;
        }

        return false;
    }

    private boolean tryMoveRight() {
        if (noBlocksRight()) {
            isMovingLeft = false;
            client.options.rightKey.setPressed(true);

            return true;
        }

        return false;
    }

    private boolean noBlocksRight() {
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
        World world = client.world;
        ClientPlayerEntity player = client.player;
        BlockPos feetPos = player.getBlockPos(); // Player's feet position

        // Get the player's facing direction
        Direction direction = player.getHorizontalFacing();

        // Calculate the BlockPos to the left of the player's feet
        BlockPos leftBlockFeet = feetPos.offset(direction.rotateYCounterclockwise()).toImmutable();

        // Calculate the BlockPos to the left of the player's head
        BlockPos leftBlockHead = leftBlockFeet.up(); // Offset by one block height for the head position

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
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
    }

    private void stopMovingAndCast() {
        stopMoving();

        NotAutoFisherMod notAFMod = ((NotAutoFisherMod) AAAClient.mod("notautofishermod"));
        timer.startWithSeconds(1, notAFMod::resetCastAttempts);
    }
    /////////////////////////// Move / organize
    public void joinCommand() {
        client.player.networkHandler.sendChatCommand("join");
        inServer = false;
        readyToFish = false;
        isFishing = false;
    }

    public void homeCommand() {
        if (!inServer) {
            inServer = true;
            client.player.networkHandler.sendChatCommand("home");
        } else {
            readyToFish = true;
            client.player.networkHandler.sendChatCommand("home");
        }
    }

    public void startFishing() {
        isFishing = true;
        stopMovingAndCast();
    }
    ///////////////////////////////////
    private int ticks = 0;
    private int tickInterval = 5;
    private boolean hasDroppedInv = false;


    @Override
    public void tick() {
        this.timer.tick();
        ticks++;

        // ---------Move to NotAutoRejoinMod and have an event callback-----------------------
        // Run the if logic when player JOINS a server, rather than every tick
        if (client.player != null && client.player.getMainHandStack().getItem() == Items.COMPASS && !timer.isRunning()) {
            client.player.sendMessage(Text.literal("Trying to join"));
            this.timer.startWithSeconds(30, this::joinCommand);

        } else if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD && !readyToFish && !timer.isRunning()) {
            client.player.sendMessage(Text.literal("Trying to join 1"));
            this.timer.startWithSeconds(5, this::homeCommand);

        } else if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD && readyToFish && !isFishing && !timer.isRunning()) {
            client.player.sendMessage(Text.literal("Trying to join 2"));
            this.timer.startWithSeconds(5, this::startFishing);
        }
        // ---------------------------------
        // TODO update to use ticktimer, because it shouldn't be in-use when its time to drop items.
        if (ticks % tickInterval == 0 && slotsToDrop != null && !slotsToDrop.isEmpty() && !Captcha.isOpen()) {
            PlayerInventory inv = client.player.getInventory();
            Iterator<Integer> iterator = slotsToDrop.iterator();
            ClientPlayerInteractionManager im = client.interactionManager;

            if (iterator.hasNext()) {
                Integer slot = iterator.next();
                ItemStack stackToDrop = inv.getStack(slot);

                if (!stackToDrop.isEmpty()) {
                    AAAClient.LOGGER.info("Dropping item from slot: {}, {}", slot, stackToDrop);
                    // TODO update the inventory to get the player's inventory specifically.
                    // TODO add pet usage
                    im.clickSlot(client.player.currentScreenHandler.syncId, slot, 1, SlotActionType.THROW,
                            client.player);
                    iterator.remove(); // Remove after attempting to drop
                } else {
                    AAAClient.LOGGER.info("Item stack at {} is empty, cannot drop!", slot);
                }
            }

            ticks = 0;
            randomizeTickInterval();

            if (slotsToDrop.isEmpty()) {
                hasDroppedInv = true;
                stopMovingAndCast();
            }
        }
    }

    private void randomizeTickInterval() {
        tickInterval = 5 + new Random().nextInt(3); // Base interval of 5, random additional 0-2 ticks
    }
}
