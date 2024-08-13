package com.xcue.lib.extensions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.List;

public interface ClientPlayerEntityExtension {
    <T extends EntityType<?>> List<Entity> aAA$getNearbyEntities(int radius, T entityType);
}