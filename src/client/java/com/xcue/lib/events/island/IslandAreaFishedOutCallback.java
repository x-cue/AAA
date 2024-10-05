package com.xcue.lib.events.island;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event handler for when an area is fished out on an island
 */
public interface IslandAreaFishedOutCallback {
    Event<IslandAreaFishedOutCallback> EVENT = EventFactory.createArrayBacked(IslandAreaFishedOutCallback.class,
            (listeners) -> () -> {
                for (IslandAreaFishedOutCallback listener : listeners) {
                    listener.interact();
                }
            });

    void interact();
}
