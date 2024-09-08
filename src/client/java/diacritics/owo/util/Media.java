package diacritics.owo.util;

import org.apache.commons.codec.binary.Base64;
import diacritics.owo.Cranberry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;

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
    public String data;

    public NativeImage image() {
      try {
        return NativeImage.read(Base64.decodeBase64(this.data));
      } catch (Exception e) {
        return new NativeImage(NativeImage.Format.RGBA, 1, 1, false);
      }
    }
  }
}
