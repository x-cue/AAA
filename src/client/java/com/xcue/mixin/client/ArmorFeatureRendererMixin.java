package com.xcue.mixin.client;

import com.xcue.mods.notpeacefulskilling.NotPeacefulSkillingMod;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>,
        A extends BipedEntityModel<T>> {

    @Shadow
    protected abstract A getModel(EquipmentSlot slot);

    @Shadow
    protected abstract void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model);

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        // If entity is nearby player, do not render their armor
        if (livingEntity instanceof AbstractClientPlayerEntity player && NotPeacefulSkillingMod.getHiddenPlayers().contains(player)) {
            A head = this.getModel(EquipmentSlot.HEAD);
            A chest = this.getModel(EquipmentSlot.CHEST);
            A legs = this.getModel(EquipmentSlot.LEGS);
            A feet = this.getModel(EquipmentSlot.FEET);

            head.setVisible(false);
            chest.setVisible(false);
            legs.setVisible(false);
            feet.setVisible(false);

            this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, head);
            this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, legs);
            this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, feet);
            this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, head);

            ci.cancel();
        }
    }
}