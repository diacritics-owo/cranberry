package diacritics.owo.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ArtworkData(int width, int height, String data) {
  public static final PacketCodec<RegistryByteBuf, ArtworkData> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER,
      ArtworkData::width, PacketCodecs.INTEGER, ArtworkData::height, PacketCodecs.STRING, ArtworkData::data,
      ArtworkData::new);
}
