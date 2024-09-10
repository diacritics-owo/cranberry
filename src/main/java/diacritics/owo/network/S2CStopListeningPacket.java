package diacritics.owo.network;

import java.util.UUID;
import diacritics.owo.Cranberry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

public record S2CStopListeningPacket(UUID player) implements CustomPayload {
  public static final CustomPayload.Id<S2CStopListeningPacket> ID = new CustomPayload.Id<>(
      Cranberry.identifier("s2c_stop_listening"));
  public static final PacketCodec<RegistryByteBuf, S2CStopListeningPacket> CODEC = PacketCodec.tuple(Uuids.PACKET_CODEC,
      S2CStopListeningPacket::player, S2CStopListeningPacket::new);

  @Override
  public Id<S2CStopListeningPacket> getId() {
    return ID;
  }
}
