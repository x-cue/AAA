package com.xcue.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event handler for when an area is fished out on an island
 */
public interface CaptchaSolvedCallback {
    Event<CaptchaSolvedCallback> EVENT = EventFactory.createArrayBacked(CaptchaSolvedCallback.class,
            (listeners) -> () -> {
                for (CaptchaSolvedCallback listener : listeners) {
                    listener.interact();
                }
            });

    void interact();
}
