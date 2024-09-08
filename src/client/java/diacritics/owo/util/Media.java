package diacritics.owo.util;

import diacritics.owo.Cranberry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import com.google.common.base.Splitter;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

public class Media {
  public native static Track track();

  public native static Artwork artwork(int width, int height);

  public native static void play();

  public native static void pause();

  public native static void toggle();

  static {
    System.load(FabricLoader.getInstance().getModContainer(Cranberry.MOD_ID).get()
        .findPath("assets/cranberry/libCranberry.dylib").get().toAbsolutePath().toString());
  }

  public static class Track {
    public String title;
    public String artist;
    public String album;
    public String id;
    public boolean playing;
    public float playbackRate;
    public Duration duration;

    public String title() {
      return this.title + ""; // (ensure non-null)
    }

    public String subtitle() {
      return this.artist + " — " + this.album;
    }

    // TODO: elapsed time
    public String duration() {
      return CranberryHelpers.toTimeString((int) this.duration.total);
    }
  }

  public static class Duration {
    public float elapsed;
    public float total;
  }

  public static class Artwork {
    public int width;
    public int height;
    public String data;

    @Nullable
    public NativeImage image() {
      try {
        if (this.data == null) {
          return null;
        }

        NativeImage image = new NativeImage(this.width, this.height, true);
        List<Integer> data = Splitter.fixedLength(8).splitToStream(this.data)
            .map(n -> new BigInteger(n, 16).intValue())
            .collect(Collectors.toList());

        for (int x = 0; x < this.width; x++) {
          for (int y = 0; y < this.height; y++) {
            image.setColor(x, y, data.get((width * x) + y));
          }
        }

        return image;
      } catch (Exception e) {
        Cranberry.LOGGER.error("failed to create nativeimage for artwork!", e);
        return null;
      }
    }
  }
}
