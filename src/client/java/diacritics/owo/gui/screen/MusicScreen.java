package diacritics.owo.gui.screen;

import diacritics.owo.util.Media;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MusicScreen extends Screen {
  public static final String SCREEN_KEY = "cranberry.screen.title";
  public static final Text NEWLINE = Text.literal("\n");

  private NarratedMultilineTextWidget textWidget;
  private Media.Track track;

  public MusicScreen() {
    super(Text.translatable(SCREEN_KEY));
  }

  public void update() {
    this.track = Media.track();

    if (this.textWidget != null) {
      // TODO: duration doesn't update until the track status updates
      this.textWidget.setMessage(Text.literal(this.track.title()).append(NEWLINE)
          .append(Text.literal(this.track.subtitle()).formatted(Formatting.GRAY)).append(NEWLINE)
          .append(Text.literal(this.track.duration()).formatted(Formatting.DARK_GRAY)));
    }
  }

  @Override
  protected void init() {
    this.textWidget = this.addDrawableChild(
        new NarratedMultilineTextWidget(this.width, Text.empty(), this.textRenderer, 12));

    this.update();
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    if (this.textWidget != null) {
      this.textWidget.initMaxWidth(this.width);
      int left = this.width / 2 - this.textWidget.getWidth() / 2;
      int top = this.height / 2;
      this.textWidget.setPosition(left, top - 9 / 2);
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
}
