package diacritics.owo.gui.screen;

import diacritics.owo.gui.widget.ImageWidget;
import diacritics.owo.util.Media;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MusicScreen extends Screen {
  public static final String SCREEN_KEY = "cranberry.screen.title";

  private NarratedMultilineTextWidget info;
  private ButtonWidget toggle;
  private ImageWidget image;

  private Media.Track track;
  private NativeImage artwork;

  private boolean initialized = false;

  public MusicScreen() {
    super(Text.translatable(SCREEN_KEY));
  }

  public void update() {
    String oldId = this.track == null ? null : this.track.id;
    Media.Track newTrack = Media.track();

    // some values are briefly nil/default after resuming
    if (this.track == null || (newTrack.id == null || !newTrack.id.equals(this.track.id))
        || newTrack.duration.elapsed != 0) {
      this.track = newTrack;

      if (this.artwork == null || (newTrack.id == null || !newTrack.id.equals(oldId))) {
        this.artwork = Media.artwork(50, 50).image();
      }

      if (this.initialized) {
        // TODO: elapsed duration doesn't update until the track status updates
        this.info.setMessage(Text.literal(this.track.title()).append("\n")
            .append(Text.literal(this.track.subtitle()).formatted(Formatting.GRAY)).append("\n")
            .append(Text.literal(this.track.duration()).formatted(Formatting.DARK_GRAY)));
        this.toggle.setMessage(
            Text.translatable("cranberry.button." + (this.track.playing ? "pause" : "play")));
        this.image
            .setImage(this.artwork == null ? new NativeImage(NativeImage.Format.RGBA, 1, 1, false) : this.artwork);

        this.initTabNavigation(); // reposition everything
      }
    }
  }

  @Override
  protected void init() {
    this.info = this.addDrawableChild(
        new NarratedMultilineTextWidget(this.width, Text.empty(), this.textRenderer, 12));

    this.toggle = this.addDrawableChild(ButtonWidget.builder(Text.empty(), new PressAction() {
      @Override
      public void onPress(ButtonWidget button) {
        if (track != null) {
          toggle.setMessage(
              Text.translatable("cranberry.button." + (track.playing ? "play" : "pause")));
        }

        Media.toggle();
      }
    }).build());

    this.image = this
        .addDrawableChild(new ImageWidget(0, 0, new NativeImage(NativeImage.Format.RGBA, 1, 1, false)));

    this.initialized = true;

    this.update();
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    if (this.initialized) {
      this.info.initMaxWidth(this.width);
      this.info.setPosition((this.width - this.info.getWidth()) / 2, (this.height / 2) - (9 / 2));

      this.toggle.setPosition((this.width - this.toggle.getWidth()) / 2,
          this.info.getBottom() + 12 + 5);

      this.image.setPosition((this.width - this.image.getWidth()) / 2,
          (this.info.getBottom() - this.info.getHeight() - this.image.getHeight()) - 12 - 5);
    }
  }

  @Override
  public boolean shouldCloseOnEsc() {
    return true;
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    this.applyBlur(delta);
    this.renderInGameBackground(context);
  }

  // TODO: update more efficiently (notificationcenter)
  @Override
  public void tick() {
    this.update();
  }
}
