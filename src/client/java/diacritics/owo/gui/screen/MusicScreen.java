package diacritics.owo.gui.screen;

import diacritics.owo.util.Media;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MusicScreen extends Screen {
  public static final String SCREEN_KEY = "cranberry.screen.title";
  public static final Text NEWLINE = Text.literal("\n");

  private NarratedMultilineTextWidget info;
  private ButtonWidget play;
  private ButtonWidget pause;
  private ButtonWidget toggle;

  private Media.Track track;

  private boolean initialized = false;

  public MusicScreen() {
    super(Text.translatable(SCREEN_KEY));
  }

  public void update() {
    Media.Track newTrack = Media.track();

    // some values are briefly nil/default after resuming
    if (this.track == null || !newTrack.id.equals(this.track.id)
        || newTrack.duration.elapsed != 0) {
      this.track = newTrack;
    }

    if (this.initialized) {
      // TODO: duration doesn't update until the track status updates
      this.info.setMessage(Text.literal(this.track.title()).append(NEWLINE)
          .append(Text.literal(this.track.subtitle()).formatted(Formatting.GRAY)).append(NEWLINE)
          .append(Text.literal(this.track.duration()).formatted(Formatting.DARK_GRAY)));
    }
  }

  @Override
  protected void init() {
    this.info = this.addDrawableChild(
        new NarratedMultilineTextWidget(this.width, Text.empty(), this.textRenderer, 12));

    // TODO: use translation keys
    this.play =
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Play"), new PlayAction()).build());
    this.pause = this
        .addDrawableChild(ButtonWidget.builder(Text.literal("Pause"), new PauseAction()).build());
    this.toggle = this
        .addDrawableChild(ButtonWidget.builder(Text.literal("Toggle"), new ToggleAction()).build());

    this.initialized = true;

    this.update();
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    if (initialized) {
      this.info.initMaxWidth(this.width);
      this.info.setPosition((this.width - this.info.getWidth()) / 2, (this.height / 2) - (9 / 2));

      this.play.setPosition((this.width - this.play.getWidth()) / 2,
          this.info.getBottom() + 12 + 5);

      this.pause.setPosition((this.width - this.pause.getWidth()) / 2, this.play.getBottom() + 5);

      this.toggle.setPosition((this.width - this.toggle.getWidth()) / 2,
          this.pause.getBottom() + 5);
    }
  }

  @Override
  public boolean shouldCloseOnEsc() {
    return true;
  }

  @Override
  protected boolean hasUsageText() {
    return false;
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    this.applyBlur(delta);
    this.renderInGameBackground(context);
  }

  @Override
  public void tick() {
    this.update();
  }

  public static class PlayAction implements PressAction {
    @Override
    public void onPress(ButtonWidget button) {
      Media.play();
    }
  }

  public static class PauseAction implements PressAction {
    @Override
    public void onPress(ButtonWidget button) {
      Media.pause();
    }
  }

  public static class ToggleAction implements PressAction {
    @Override
    public void onPress(ButtonWidget button) {
      Media.toggle();
    }
  }
}
