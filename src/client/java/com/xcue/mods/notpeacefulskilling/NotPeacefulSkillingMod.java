package com.xcue.mods.notpeacefulskilling;

import com.xcue.lib.AAAMod;
import com.xcue.lib.extensions.ClientPlayerEntityExtension;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.List;

/**
 * Random util to make it easier to grind
 * Hides nearby when holding tools
 */
public class NotPeacefulSkillingMod implements AAAMod {
    private static final int RADIUS = 5;
    private static List<Entity> hiddenPlayers = List.of();

    public static List<Entity> getHiddenPlayers() {
        return hiddenPlayers;
    }

    @Override
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            ClientPlayerEntity p = client.player;
            if (p == null) return;

            if (p.getMainHandStack().getItem().getName().getString().toLowerCase().contains("hoe")) {
                hiddenPlayers = ((ClientPlayerEntityExtension) p).aAA$getNearbyEntities(RADIUS, EntityType.PLAYER);
            } else {
                hiddenPlayers = List.of();
            }
        }));
    }
}
