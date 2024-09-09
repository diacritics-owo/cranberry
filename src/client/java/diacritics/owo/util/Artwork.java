package diacritics.owo.util;

import net.minecraft.client.texture.NativeImage;
import diacritics.owo.Cranberry;
import diacritics.owo.network.ArtworkData;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class Artwork {
  private int width;
  private int height;
  private String data;

  private NativeImage cache;

  public Artwork(int width, int height) {
    this.width = width;
    this.height = height;
    this.data = null;
  }

  public Artwork(int width, int height, String data) {
    this.width = width;
    this.height = height;
    this.data = data;
  }

  private native void _reload();

  public static NativeImage empty(int width, int height) {
    NativeImage image = new NativeImage(width, height, false);
    image.apply(x -> 0);
    return image;
  }

  public static Artwork from(ArtworkData data) {
    return new Artwork(data.width(), data.height(), data.data());
  }

  public ArtworkData artworkData() {
    return new ArtworkData(this.width, this.height,
        this.data == null ? "0".repeat(8 * this.width * this.height) : this.data);
  }

  public void reload() {
    this._reload();
    this.cache = null;
  }

  public boolean cached() {
    return this.cache != null;
  }

  public NativeImage reloaded() {
    this.reload();
    return this.image();
  }

  public NativeImage image() {
    if (this.cached()) {
      return this.cache;
    }

    NativeImage image = this.raw();
    this.cache = image;
    return image == null ? empty(this.width, this.width) : image;
  }

  @Nullable
  private NativeImage raw() {
    try {
      if (this.data == null) {
        return null;
      }

      NativeImage image = new NativeImage(this.width, this.height, true);
      List<Integer> data = CranberryHelpers.readColors(this.data);

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
