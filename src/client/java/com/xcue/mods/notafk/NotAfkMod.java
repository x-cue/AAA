package com.xcue.mods.notafk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class NotAfkMod implements ClientModInitializer {
    boolean autoClickEnabled = false;
    int ticks = 0;
    @Override
    public void onInitializeClient() {


        AttackEntityCallback.EVENT.register(((player1, world, hand, entity, hitResult) -> {
            if (!entity.isPlayer() && !autoClickEnabled){
                ticks = 0;
                autoClickEnabled = true;
            }
            return ActionResult.PASS;
        }));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player == null){
                return;
            }

            if(autoClickEnabled && (ticks % 2 == 0)){
                HitResult hRes = client.crosshairTarget;
                if(client.mouse.wasLeftButtonClicked() && ticks != 0){
                    autoClickEnabled = false;
                    return;
                }
                if (hRes != null && player != null) {
                    if (hRes.getType() == HitResult.Type.ENTITY) {
                        Entity ent = ((EntityHitResult) hRes).getEntity();
                        client.interactionManager.attackEntity(player, ent);
                    }
                }
            }
            ticks++;
        });



    }
}
