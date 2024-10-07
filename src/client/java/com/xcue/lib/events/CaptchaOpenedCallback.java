package com.xcue.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event handler for when an area is fished out on an island
 */
public interface CaptchaOpenedCallback {
    Event<CaptchaOpenedCallback> EVENT = EventFactory.createArrayBacked(CaptchaOpenedCallback.class,
            (listeners) -> () -> {
                for (CaptchaOpenedCallback listener : listeners) {
                    listener.interact();
                }
            });

    void interact();
}