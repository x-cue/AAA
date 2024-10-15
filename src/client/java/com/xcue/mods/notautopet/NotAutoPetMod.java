package com.xcue.mods.notautopet;

public class NotAutoPetMod {
    // v1 implementation from bpack for fishing
    //        if(client.player.getMainHandStack().getItem() == Items.FISHING_ROD && isFishing && ticks % 14400 == 0){
    //            isFishing = false;
    //            readyToFish = false;
    //            client.player.getInventory().scrollInHotbar(-1);
    //            activatePet = true;
    //            ticks = 0;
    //        }
    //        if(client.player.getMainHandStack().getItem() == Items.PLAYER_HEAD && activatePet && ticks % 100 == 0){
    //            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
    //            activatePet = false;
    //            readyToSwap = true;
    //            ticks = 0;
    //        }
    //        if(client.player.getMainHandStack().getItem() != Items.PLAYER_HEAD && activatePet && ticks % 100 == 0){
    //            activatePet = false;
    //            readyToSwap = true;
    //            ticks = 0;
    //        }
    //        if(readyToSwap && !activatePet && ticks % 100 == 0){
    //            client.player.getInventory().scrollInHotbar(1);
    //            readyToFish = true;
    //            ticks = 0;
    //        }
    //        if(readyToSwap && readyToFish && ticks % 100 == 0){
    //            stopMovingAndCast();
    //            readyToSwap = false;
    //            isFishing = true;
    //            ticks = 0;
    //        }
}
