package diacritics.owo.network;

import diacritics.owo.Cranberry;
import diacritics.owo.util.Media;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record C2SListeningPacket(Media.Track track, ArtworkData artwork) implements CustomPayload {
  public static final CustomPayload.Id<C2SListeningPacket> ID = new CustomPayload.Id<>(
      Cranberry.identifier("c2s_listening"));
  public static final PacketCodec<RegistryByteBuf, C2SListeningPacket> CODEC = PacketCodec.tuple(Media.Track.CODEC,
      C2SListeningPacket::track, ArtworkData.CODEC, C2SListeningPacket::artwork, C2SListeningPacket::new);

  @Override
  public Id<C2SListeningPacket> getId() {
    return ID;
  }
}
