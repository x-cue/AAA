package com.xcue.mixin.client;

import com.xcue.lib.extensions.ClientPlayerEntityExtension;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements ClientPlayerEntityExtension {
    @Unique
    @Override
    public <T extends EntityType<?>> List<Entity> aAA$getNearbyEntities(int radius, T entityType) {
        ClientPlayerEntity p = (ClientPlayerEntity) (Object) this;
        return p.getWorld().getOtherEntities(p, p.getBoundingBox().expand(radius)).
                stream()
                .filter(x -> x.getType().equals(entityType))
                .toList();
    }
}