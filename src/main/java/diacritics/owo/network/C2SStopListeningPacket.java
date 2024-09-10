package diacritics.owo.network;

import diacritics.owo.Cranberry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record C2SStopListeningPacket() implements CustomPayload {
  public static final CustomPayload.Id<C2SStopListeningPacket> ID = new CustomPayload.Id<>(
      Cranberry.identifier("c2s_stop_listening"));
  public static final PacketCodec<RegistryByteBuf, C2SStopListeningPacket> CODEC = PacketCodec
      .unit(new C2SStopListeningPacket());

  @Override
  public Id<C2SStopListeningPacket> getId() {
    return ID;
  }
}
