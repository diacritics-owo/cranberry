package diacritics.owo.util;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Media {
  public native static Track track();

  public native static void play();

  public native static void pause();

  public native static void toggle();

  public static record Track(String title, String artist, String album, String id, boolean playing,
      Duration duration) {

    public static final PacketCodec<RegistryByteBuf, Track> CODEC = PacketCodec.tuple(PacketCodecs.STRING, Track::title,
        PacketCodecs.STRING, Track::artist, PacketCodecs.STRING, Track::album, PacketCodecs.STRING, Track::id,
        PacketCodecs.BOOL, Track::playing, Duration.CODEC, Track::duration, Track::new);

    public MutableText getTitle() {
      return (this.title == null ? Text.translatable("cranberry.text.no_title") : Text.literal(this.title));
    }

    public MutableText getSubtitle() {
      return (this.artist == null ? Text.translatable("cranberry.text.no_artist") : Text.literal(this.artist))
          .append(this.artist != null && this.album != null ? " — " : "").append(this.album == null ? "" : this.album)
          .formatted(Formatting.ITALIC);
    }

    public MutableText getDuration() {
      return Text.literal(CranberryHelpers.toTimeString((int) this.duration.total))
          .formatted(Formatting.ITALIC);
    }

    public MutableText getShortTitle() {
      return this.getTitle().append(" (")
          .append(this.artist == null ? Text.translatable("cranberry.text.no_artist") : Text.literal(this.artist))
          .append(")")
          .formatted(Formatting.GRAY);
    }

    public boolean valid() {
      return (this.id == null || this.duration.total != 0) && (this.title != null && !this.title.isEmpty());
    }

    // TODO: nullpointerexceptions everywhere (handle things in less ad hoc way)
    public Track nonNull() {
      return new Track(this.title == null ? "" : this.title, this.artist == null ? "" : this.artist,
          this.album == null ? "" : this.album, this.id == null ? "" : this.id, this.playing,
          this.duration);
    }
  }

  public static record Duration(float elapsed, float total) {
    public static final PacketCodec<RegistryByteBuf, Duration> CODEC = PacketCodec.tuple(PacketCodecs.FLOAT,
        Duration::elapsed, PacketCodecs.FLOAT, Duration::total, Duration::new);
  }
}
