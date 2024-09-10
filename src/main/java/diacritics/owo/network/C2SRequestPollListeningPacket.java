package diacritics.owo.network;

import diacritics.owo.Cranberry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record C2SRequestPollListeningPacket() implements CustomPayload {
  public static final CustomPayload.Id<C2SRequestPollListeningPacket> ID = new CustomPayload.Id<>(
      Cranberry.identifier("c2s_request_poll_listening"));
  public static final PacketCodec<RegistryByteBuf, C2SRequestPollListeningPacket> CODEC = PacketCodec
      .unit(new C2SRequestPollListeningPacket());

  @Override
  public Id<C2SRequestPollListeningPacket> getId() {
    return ID;
  }
}
