package com.xcue.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements ClientPlayerEntityExtension {
    @Unique
    @Override
    public <T extends EntityType<?>> List<Entity> aAA$getNearbyEntities(Entity except, int radius, T entityType) {
        ClientPlayerEntity p = (ClientPlayerEntity) (Object) this;
        return p.getWorld().getOtherEntities(p, p.getBoundingBox().expand(10)).
                stream()
                .filter(x -> x.getType().equals(entityType))
                .toList();
    }
}