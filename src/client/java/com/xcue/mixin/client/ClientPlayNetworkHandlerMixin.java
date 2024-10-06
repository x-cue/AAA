package com.xcue.mixin.client;

import com.xcue.lib.events.chat.PlayerMessage;
import com.xcue.lib.events.chat.PlayerMessageReceivedCallback;
import com.xcue.lib.events.island.IslandAreaFishedOutCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method = "onGameMessage")
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo callbackInfo) {
        String msg = packet.content().getString();

        Pattern msgPattern = Pattern.compile("^\\[(\\S+)] \\[(\\S+)\\s.*](.*)$");
        Matcher matcher = msgPattern.matcher(msg);
        ClientPlayerEntity p = MinecraftClient.getInstance().player;

        if (matcher.matches()) {
            String server = matcher.group(1);
            String sender = matcher.group(2);
            String chatMsg = matcher.group(3);

            PlayerMessageReceivedCallback.EVENT.invoker().interact(p, new PlayerMessage(server, sender, chatMsg));
        }

        Pattern fishedOutPattern = Pattern.compile("^Move to an un-fished area to continue fishing!$");
        Matcher fishedOutMatcher = fishedOutPattern.matcher(msg);

        if (fishedOutMatcher.matches()) {
            IslandAreaFishedOutCallback.EVENT.invoker().interact();
        }
    }
}
