package com.xcue.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.List;

public interface ClientPlayerEntityExtension {
    public <T extends EntityType<?>> List<Entity> aAA$getNearbyEntities(Entity except, int radius, T entityType);
}
