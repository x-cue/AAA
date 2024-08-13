package com.xcue.mods.notahflipper;

import com.xcue.lib.AAAMod;
import com.xcue.mods.notahflipper.util.AhSoldListing;
import com.xcue.mods.notahflipper.util.AuctionHouse;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NotAhFlipperMod implements AAAMod {
//    ClientPlayerEntity player = MinecraftClient.getInstance().player;
//    MinecraftClient client = MinecraftClient.getInstance();
    private boolean hasRead = false;

    @Override
    public void init() {
        AuctionHouse.enable();

        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if(!AuctionHouse.isEnabled()) return;
            if (hasRead) return;

            ClientPlayerEntity player = client.player;
            if(player != null){
                if (AuctionHouse.isOpen(client)) {
                    player.sendMessage(Text.literal("Auction House is open... clicking slot 52"));

                    // Open history tab
                    MinecraftClient.getInstance().interactionManager.clickSlot(player.playerScreenHandler.syncId, 52, 1, SlotActionType.PICKUP, player);
                } else if (AuctionHouse.isHistoryOpen(client)) {
                    player.sendMessage(Text.literal("History is open"));
                    // Scan items
                    HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;
                    ScreenHandler handler = screen.getScreenHandler();
                    List<ItemStack> stacks = handler.getStacks();

                    List<AhSoldListing> listings = new ArrayList<>();
                    for (ItemStack item : stacks) {
                        try {
                            AhSoldListing listing = new AhSoldListing(item);
                            listings.add(listing);

                            player.sendMessage(Text.literal(" "));
                            player.sendMessage(listing.getName());
                            player.sendMessage(Text.literal("Seller Name: " + listing.getSellerName()));
                            player.sendMessage(Text.literal("Amount: " + listing.getAmount()));
                            player.sendMessage(Text.literal("Sold Price: " + listing.getSoldPrice()));
                            player.sendMessage(Text.literal("Time Sold: " + listing.getDateSold().toLocalDateTime().toString()));

                        } catch (ParseException ignore) {
                            player.sendMessage(Text.literal("Error parsing item"));
                        }
                    }

                    player.sendMessage(Text.literal(""));
                    hasRead = true;
                    // How do we know when to stop when at the last page?
                }
            }

        });

    }
}

