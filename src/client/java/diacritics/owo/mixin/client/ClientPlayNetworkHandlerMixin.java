package diacritics.owo.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import diacritics.owo.CranberryClient;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  @Inject(at = @At("TAIL"), method = "onGameJoin")
  public void joinWorld(GameJoinS2CPacket packet,
      CallbackInfo info) {
    CranberryClient.sendRequestPollPacket();
  }
}
