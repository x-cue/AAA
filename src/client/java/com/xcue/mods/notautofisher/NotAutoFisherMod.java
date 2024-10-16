package com.xcue.mods.notautofisher;

import com.xcue.Keybinds;
import com.xcue.lib.AAAMod;
import com.xcue.lib.Captcha;
import com.xcue.lib.events.CaptchaOpenedCallback;
import com.xcue.lib.events.CaptchaSolvedCallback;
import com.xcue.lib.events.island.IslandAreaFishedOutCallback;
import com.xcue.lib.events.island.IslandRodMilestoneCallback;
import com.xcue.mixin.client.FishingBobberEntityAccessorMixin;
import com.xcue.mods.notautofisher.modes.FishInPlaceMode;
import com.xcue.mods.notautofisher.modes.FishLeftRightMode;
import com.xcue.mods.notautofisher.modes.NotAutoFisherMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.*;

public class NotAutoFisherMod extends AAAMod {
    boolean canCast = true;
    boolean canReel = false;
    int castDelay = 15;
    int reelDelay = 15;
    private final Map<String, NotAutoFisherMode> modes = new HashMap<>();
    private NotAutoFisherMode mode;

    public void nextMode() {
        this.mode.stopTimer();

        List<NotAutoFisherMode> modes = this.modes.values().stream().toList();
        int i = modes.indexOf(this.mode);

        if (modes.size() - 1 == i) {
            i = -1;
        }

        this.mode = modes.get(++i);
        setModSetting("mode", mode.getConfigKey());

        assert client.player != null;
        client.player.sendMessage(Text.literal("Swapped fishing to mode: " + this.mode.getConfigKey()));
    }

    @Override
    public void init() {
        registerModes();

        String firstMode = modes.keySet().stream().findFirst().get();
        this.mode = modes.get(getModSetting("mode", firstMode));

        ClientTickEvents.END_CLIENT_TICK.register((c) -> {
            if (Keybinds.NOT_AUTO_FISHER_SWAP_MODES.wasPressed()) {
                nextMode();
            }

            this.tick(this.client);
        });

        IslandAreaFishedOutCallback.EVENT.register(() -> {
            // Play Sound
            assert client.player != null;
            client.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1, 1);

            if (isEnabled()) {
                this.mode.onAreaFishedOut();
            }
        });

        CaptchaOpenedCallback.EVENT.register(() -> {
            if (isEnabled()) {
                mode.onCaptchaOpened();
            }
        });

        CaptchaSolvedCallback.EVENT.register(() -> {
            canCast = true;
            canReel = true;

            if (isEnabled()) {
                mode.onCaptchaSolved();
            }
        });

        IslandRodMilestoneCallback.EVENT.register((level, attribute) -> {
            // TODO update to togglable
            if (!getModSetting("rod-leveler-enabled", true)) return;

            logger.warning("attribute: {}" + attribute);

            // TODO update to comma-delimited
            String keyword = getModSetting("rod-attributes", "bonus");
            // "catch" would match catch success and bonus catch

            String attr = attribute.toLowerCase();
            if (!attr.contains("durability") && !attr.contains(keyword.toLowerCase())) {
                swapToNextRod();
            }
        });
    }

    private void tick(MinecraftClient client) {
        if (Keybinds.NOT_AUTO_FISHER.wasPressed()) toggle();
        if (!isEnabled() || client.player == null) return;

        mode.tick();
// Debug
//        if(castDelay < 0){
//            client.player.sendMessage(Text.literal("Cast delay dropped below 0"));
//        }

        if (canCast) {
            // Cast Logic
            if (client.player.fishHook == null && castDelay == 0) {
                cast();
            } else if (client.player.fishHook == null) {
                castDelay--;
            }
        }

        if (canReel) {
            // Reel Logic
            // Fish is on hook
            if (client.player.fishHook != null && ((FishingBobberEntityAccessorMixin) client.player.fishHook)
                    .getCaughtFish()) {
                // Reel timer is up
                if (reelDelay == 0) {
                    reel();
                } else if (reelDelay > 0) {
                    reelDelay--;
                }
            }
        } else if (client.player.fishHook == null) {
            canReel = true;
        } else {
            canCast = true;
        }
    }

    public void useRod() {
        if (client.player == null || client.interactionManager == null || Captcha.isOpen()) return;

        if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        } else if (client.player.getOffHandStack().getItem() == Items.FISHING_ROD) {
            client.player.swingHand(Hand.OFF_HAND);
            client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
        }
    }

    public void cast() {
        mode.stopTimer();
        canCast = false;
        canReel = true;
        useRod();
        castDelay = new Random().nextInt(6, 16);
    }

    public void reel() {
        canReel = false;
        canCast = true;
        useRod();
        reelDelay = new Random().nextInt(5, 10);
    }

    public void registerMode(NotAutoFisherMode mode) {
        this.modes.put(mode.getConfigKey(), mode);
    }

    private void registerModes() {
        registerMode(new FishInPlaceMode());
        //registerMode(new FishInCircleMode());
        registerMode(new FishLeftRightMode());
    }

    public NotAutoFisherMode getMode() {
        return this.mode;
    }

    public void swapToNextRod() {
        if (client.player == null) return;

        ClientPlayerEntity p = client.player;
        PlayerInventory inv = p.getInventory();

        int currentIndex = client.player.getInventory().selectedSlot;

        for (int i = 1; i < 9; i++) {
            int nextIndex = (currentIndex + i) % 9;
            ItemStack itemStack = inv.getStack(nextIndex);

            if (itemStack.getItem() == Items.FISHING_ROD) {
                client.player.getInventory().selectedSlot = nextIndex;
                break;
            }
        }
    }
}
