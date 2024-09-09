package diacritics.owo.util;

import net.minecraft.client.texture.NativeImage;
import diacritics.owo.Cranberry;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class Artwork {
  public int width;
  public int height;
  public String data;

  public Artwork(int width, int height, String data) {
    this.width = width;
    this.height = height;
    this.data = data;
  }

  public native static Artwork artwork(int width, int height);

  public static NativeImage empty(int width, int height) {
    NativeImage image = new NativeImage(width, height, false);
    image.fillRect(0, 0, width, height, 0);
    return image;
  }

  // TODO: once we have networking and validity isn't guaranteed, we should
  // probably make this crash-resistant~
  @Nullable
  public NativeImage image() {
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
