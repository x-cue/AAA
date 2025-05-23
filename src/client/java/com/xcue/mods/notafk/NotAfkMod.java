package com.xcue.mods.notafk;

import com.xcue.Keybinds;
import com.xcue.lib.AAAMod;
import com.xcue.lib.events.chat.PlayerMessageReceivedCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotAfkMod extends AAAMod {
    private boolean fixedLastTick;

    private boolean isHoldingSword(ClientPlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        Pattern allowedItems = Pattern.compile("^.*Sword$");

        Matcher matcher = allowedItems.matcher(item.getName().getString());

        return matcher.matches();
    }

    public boolean isItemLowDurability(ClientPlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        double maxDurability = item.getMaxDamage();
        double durability = maxDurability - item.getDamage();
        double durabilityLeft = durability / maxDurability;

        return durabilityLeft < (getModSetting("fix-at-percent", 15D) / 100);
    }

    @Override
    public void init() {
        AttackEntityCallback.EVENT.register(((playerEnt, world, hand, entity, hitResult) -> {
            // TODO fix all on cooldown will spam yo bitch
            ClientPlayerEntity player = client.player;
            assert player != null; // Asserts are dangerous, but it is ok here because the event is only called when
            // the player exists

            if (!entity.isPlayer() && isHoldingSword(player) && !AutoClicker.isRunning()) {
                AutoClicker.start(player);
            }

            // If AutoClicker isn't running still, we don't need to run any AutoClicker logic
            if (!AutoClicker.isRunning() || !isEnabled()) return ActionResult.PASS;

            // Check for sword and that it's durability is low
            if (isHoldingSword(player) && isItemLowDurability(player, hand)) {
                // TODO: Update to a ticktimer...
                if (fixedLastTick) {
                    fixedLastTick = false;
                    AutoClicker.stop("No valid sword found");
                    return ActionResult.FAIL;
                }

                // If it was not fixed, look for a new sword
                // If none found, FAIL the attack and stop AutoClicking
                for (int i = 0; i < 9; i++) {
                    player.getInventory().scrollInHotbar(-1);

                    if (isHoldingSword(player) && !isItemLowDurability(player, hand)) {
                        break;
                    }

                    // No new sword was found. Stop the AutoClicker
                    if (i == 8) {
                        // Try to fix the sword
                        player.networkHandler.sendChatCommand("fix all");
                        fixedLastTick = true;
                        return ActionResult.PASS;
                    }
                }
            } else {
                fixedLastTick = false;
            }

            // If player has moved even 1 block, stop AutoClicking and FAIL the attack
            if (!player.getBlockPos().isWithinDistance(AutoClicker.getPlayerStartingPos(), 1D)) {
                AutoClicker.stop("Player has moved");
                return ActionResult.FAIL;
            }

            // Only after all checks have passed should we return PASS
            return ActionResult.PASS;
        }));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            AutoClicker.stop("");
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Keybinds.NOT_AFK.wasPressed()) toggle();
            if (Keybinds.NOT_AFK_STOP_ON_MSG.wasPressed()) setModSetting("stop-on-msg", !getModSetting("stop-on-msg",
                    false));
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || !AutoClicker.isRunning() || !isEnabled()) return;

            AutoClicker.tick();
        });

        PlayerMessageReceivedCallback.EVENT.register(((player, msg) ->
        {
            if (AutoClicker.isRunning() && getModSetting("stop-on-msg", false)) {
                AutoClicker.stop("Player received a message");
            }
        }));
    }
}