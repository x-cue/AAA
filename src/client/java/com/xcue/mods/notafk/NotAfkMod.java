package com.xcue.mods.notafk;

import com.xcue.lib.AAAMod;
import com.xcue.lib.events.chat.PlayerMessageReceivedCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class NotAfkMod implements AAAMod {
    boolean autoClickEnabled = false;
    int ticks = 0;
    int xLoc;
    int yLoc;
    int zLoc;
    public boolean getCurrItem(ClientPlayerEntity player){
        Item currItem = player.getMainHandStack().getItem();
        Pattern allowedItems = Pattern.compile("^.*Sword$");

        Matcher matcher = allowedItems.matcher(currItem.getName().getString());

        return matcher.matches();
    }
    public boolean getCurrItemHealth(ClientPlayerEntity player, Hand hand){
        ItemStack currItem = player.getStackInHand(hand);
        int i = currItem.getMaxDamage() - currItem.getDamage();
        return(i < 20);
    }
    @Override
    public void init() {
        PlayerMessageReceivedCallback.EVENT.register(((player, msg) -> {
            autoClickEnabled = false;
            player.sendMessage(Text.literal("Disabled AutoClicker"));
        }));

        AttackEntityCallback.EVENT.register(((player, world, hand, entity, hitResult) -> {
            // Is sword
            ClientPlayerEntity player1 = MinecraftClient.getInstance().player;
            if (!entity.isPlayer() && !autoClickEnabled && getCurrItem(player1)){
                ticks = 0;
                autoClickEnabled = true;
                xLoc = player1.getBlockX();
                yLoc = player1.getBlockY();
                zLoc = player1.getBlockZ();
            }
            if(getCurrItemHealth(player1,hand) && getCurrItem(player1)){
                player1.networkHandler.sendChatCommand("fix");
                if(!getCurrItemHealth(player1,hand)){
                    autoClickEnabled = true;
                    return ActionResult.PASS;
                } else {
                    player1.getInventory().scrollInHotbar(-1);
                    if(getCurrItem(player1)){
                        autoClickEnabled = true;
                        return ActionResult.PASS;
                    } else {
                        autoClickEnabled = false;
                        return ActionResult.FAIL;
                    }
                }
            }
            if(player1.getBlockX() != xLoc || player1.getBlockY() != yLoc || player1.getBlockZ() != zLoc){
                autoClickEnabled = false;
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        }));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            autoClickEnabled = false;
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player == null){
                return;
            }
            if(autoClickEnabled && (ticks % 2 == 0)){
                HitResult hRes = client.crosshairTarget;

                if(client.mouse.wasLeftButtonClicked() && ticks != 0 || !getCurrItem(player)){
                    autoClickEnabled = false;
                    return;
                }
                if (hRes != null) {
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