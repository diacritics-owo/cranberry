package diacritics.owo.util;

import java.nio.ByteBuffer;
import diacritics.owo.Cranberry;
import net.fabricmc.loader.api.FabricLoader;

public class Media {
  public native static Track track();

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
    public float playbackRate;
    public Duration duration;
    public Artwork artwork;

    public String title() {
      return this.title;
    }

    public String subtitle() {
      return this.artist + "—" + this.album;
    }

    public String duration() {
      return Helpers.toTimeString((int) this.duration.elapsed) + "/"
          + Helpers.toTimeString((int) this.duration.total) + " (" + this.playbackRate + "x)";
    }

    @Override
    public String toString() {
      return this.artist + " - " + this.title + " (" + this.album + ")" + " - " + this.duration
          + " [" + this.artwork + "]";
    }
  }

  public static class Duration {
    public float elapsed;
    public float total;

    @Override
    public String toString() {
      return this.elapsed + "/" + this.total;
    }
  }

  public static class Artwork {
    public int width;
    public int height;
    public ByteBuffer data;
    public String mime;

    @Override
    public String toString() {
      return this.width + "x" + this.height + " "
          + (this.data == null ? 0 : this.data.array().length) + " " + this.mime + " "
          + (this.data == null ? ":(" : this.data.get(0));
    }
  }
}
