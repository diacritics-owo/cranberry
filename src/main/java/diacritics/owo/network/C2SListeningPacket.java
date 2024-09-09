package diacritics.owo.network;

import diacritics.owo.util.Media;

public record C2SListeningPacket(Media.Track track, ArtworkData artwork) {
}
