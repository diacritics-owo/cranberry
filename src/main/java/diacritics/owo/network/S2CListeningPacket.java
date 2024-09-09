package diacritics.owo.network;

import diacritics.owo.util.Media;
import java.util.UUID;

public record S2CListeningPacket(
    UUID player, Media.Track track, ArtworkData artwork) {
  public static S2CListeningPacket from(UUID player, C2SListeningPacket packet) {
    return new S2CListeningPacket(player, packet.track(), packet.artwork());
  }
}
