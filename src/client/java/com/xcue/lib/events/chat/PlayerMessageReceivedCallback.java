package com.xcue.lib.events.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Event handler for when a player receives a dm from another player on cosmic sky
 */
public interface PlayerMessageReceivedCallback {
    Event<PlayerMessageReceivedCallback> EVENT = EventFactory.createArrayBacked(PlayerMessageReceivedCallback.class,
            (listeners) -> (player, msg) -> {
                for (PlayerMessageReceivedCallback listener : listeners) {
                    listener.interact(player, msg);
                }
            });

    void interact(ClientPlayerEntity player, PlayerMessage msg);
}
