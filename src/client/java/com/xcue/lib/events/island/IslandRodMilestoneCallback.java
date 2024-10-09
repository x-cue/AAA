package com.xcue.lib.events.island;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event handler for when an area is fished out on an island
 */
public interface IslandRodMilestoneCallback {
    Event<IslandRodMilestoneCallback> EVENT = EventFactory.createArrayBacked(IslandRodMilestoneCallback.class,
            (listeners) -> (newLevel, attributeMsg) -> {
                for (IslandRodMilestoneCallback listener : listeners) {
                    listener.interact(newLevel, attributeMsg);
                }
            });

    void interact(int newLevel, String attributeMsg);
}