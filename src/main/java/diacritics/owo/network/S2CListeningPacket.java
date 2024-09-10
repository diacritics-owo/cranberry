package diacritics.owo.network;

import diacritics.owo.Cranberry;
import diacritics.owo.util.Media;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import java.util.UUID;

public record S2CListeningPacket(
    UUID player, Media.Track track, ArtworkData artwork) implements CustomPayload {

  public static final CustomPayload.Id<S2CListeningPacket> ID = new CustomPayload.Id<>(
      Cranberry.identifier("s2c_listening"));
  public static final PacketCodec<RegistryByteBuf, S2CListeningPacket> CODEC = PacketCodec.tuple(Uuids.PACKET_CODEC,
      S2CListeningPacket::player, Media.Track.CODEC, S2CListeningPacket::track, ArtworkData.CODEC,
      S2CListeningPacket::artwork, S2CListeningPacket::new);

  public static S2CListeningPacket from(UUID player, C2SListeningPacket packet) {
    return new S2CListeningPacket(player, packet.track(), packet.artwork());
  }

  @Override
  public Id<S2CListeningPacket> getId() {
    return ID;
  }
}
