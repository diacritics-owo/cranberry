package diacritics.owo.network;

import diacritics.owo.Cranberry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record S2CPollListeningPacket() implements CustomPayload {
  public static final CustomPayload.Id<S2CPollListeningPacket> ID = new CustomPayload.Id<>(
      Cranberry.identifier("s2c_poll_listening"));
  public static final PacketCodec<RegistryByteBuf, S2CPollListeningPacket> CODEC = PacketCodec
      .unit(new S2CPollListeningPacket());

  @Override
  public Id<S2CPollListeningPacket> getId() {
    return ID;
  }
}
