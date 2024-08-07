package com.xcue.mixin.client;

import com.xcue.mods.notafk.NotAfkMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Mixins are top-bottom in order of when they are called
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method = "onGameMessage")
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo callbackInfo) {
        Pattern pattern = Pattern.compile("^\\[Skyblock] \\[(\\S+)\\s.*$");
        Matcher matcher = pattern.matcher(packet.content().getString());
        ClientPlayerEntity p = MinecraftClient.getInstance().player;

        if (matcher.matches()) {
            String player = matcher.group(1);
            if (!NotAfkMod.whitelist.contains(player.toLowerCase()) && NotAfkMod.getInstance().isEnabled()) {
                MinecraftClient.getInstance().close();
            }
        }
    }
}