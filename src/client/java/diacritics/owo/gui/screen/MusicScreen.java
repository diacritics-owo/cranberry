package diacritics.owo.gui.screen;

import org.jetbrains.annotations.NotNull;

import diacritics.owo.gui.widget.ImageWidget;
import diacritics.owo.util.Artwork;
import diacritics.owo.util.Media;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;

public class MusicScreen extends BaseOwoScreen<FlowLayout> {
  public static final Size IMAGE_SIZE = Size.of(50, 50);
  public static final Size ICON_SIZE = Size.of(10, 10);

  private ImageWidget image;
  private LabelComponent info;
  private ButtonComponent toggle;

  private Media.Track track;
  private NativeImage artwork;

  public MusicScreen() {
    super(Text.translatable("cranberry.screen.title"));
  }

  @Override
  protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
    return OwoUIAdapter.create(this, Containers::verticalFlow);
  }

  @Override
  protected void build(FlowLayout rootComponent) {
    rootComponent
        .surface(Surface.VANILLA_TRANSLUCENT)
        .horizontalAlignment(HorizontalAlignment.CENTER)
        .verticalAlignment(VerticalAlignment.CENTER);

    this.info = Components.label(Text.empty());
    this.toggle = Components.button(Text.empty(),
        button -> {
          if (this.track != null) {
            this.setToggle(!this.track.playing());
          }

          Media.toggle();
        });
    this.image = new ImageWidget(0, 0, Artwork.empty(IMAGE_SIZE.width(), IMAGE_SIZE.height()));

    rootComponent.child(
        Containers.verticalFlow(Sizing.content(), Sizing.content())
            .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.wrapVanillaWidget(this.image))
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(this.info).child(this.toggle).padding(Insets.of(10)))
                .verticalAlignment(VerticalAlignment.CENTER))
            .padding(Insets.of(10))
            .surface(Surface.DARK_PANEL)
            .verticalAlignment(VerticalAlignment.CENTER)
            .horizontalAlignment(HorizontalAlignment.CENTER));
  }

  @Override
  public void tick() {
    Media.Track newTrack = Media.track();
    boolean newId = newTrack.id() == null || !newTrack.id().equals(this.track == null ? null : this.track.id());

    // some values are briefly nil/default after resuming

    if (this.artwork == null || newId) {
      this.artwork = Artwork.artwork(IMAGE_SIZE.width(), IMAGE_SIZE.height()).image();
      this.image.setImage(
          this.artwork == null ? Artwork.empty(IMAGE_SIZE.width(), IMAGE_SIZE.height()) : this.artwork);
    }

    if (this.track == null || newTrack.valid() || newId) {
      this.track = newTrack;

      // TODO: elapsed duration doesn't update until the track status updates
      this.info.text(this.track.getTitle().append("\n")
          .append(this.track.getSubtitle()).append("\n")
          .append(this.track.getDuration()));
      this.setToggle(this.track.playing());
    }
  }

  public void forceUpdate() {
    this.info.text(this.info.text());
  }

  public void setToggle(boolean playing) {
    this.toggle.setMessage(
        Text.translatable("cranberry.button." + (playing ? "pause" : "play")));
    this.forceUpdate();
  }
}
