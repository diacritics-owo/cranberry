package diacritics.owo.gui.screen;

import java.util.UUID;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import diacritics.owo.CranberryClient;
import diacritics.owo.gui.widget.ImageWidget;
import diacritics.owo.util.Artwork;
import diacritics.owo.util.CranberryHelpers;
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
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MusicScreen extends BaseOwoScreen<FlowLayout> {
  private ImageWidget image;
  private LabelComponent info;
  private ButtonComponent toggle;
  private FlowLayout listening;

  private Media.Track track;
  private Artwork artwork = new Artwork(CranberryHelpers.IMAGE_SIZE.width(), CranberryHelpers.IMAGE_SIZE.height());

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
    this.image = new ImageWidget(0, 0,
        Artwork.empty(CranberryHelpers.IMAGE_SIZE.width(), CranberryHelpers.IMAGE_SIZE.height()));
    this.listening = Containers.verticalFlow(Sizing.content(), Sizing.content());

    rootComponent.child(
        Containers.verticalFlow(Sizing.fill(70), Sizing.content())
            .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.wrapVanillaWidget(this.image))
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(this.info).child(this.toggle).margins(Insets.of(10)))
                .verticalAlignment(VerticalAlignment.CENTER))
            .child(Containers.verticalScroll(Sizing.content(), Sizing.fixed(25),
                this.listening))
            .padding(Insets.of(10))
            .surface(Surface.DARK_PANEL)
            .verticalAlignment(VerticalAlignment.CENTER));
  }

  @Override
  public void tick() {
    Media.Track newTrack = Media.track();
    boolean newId = newTrack.id() == null || !newTrack.id().equals(this.track == null ? null : this.track.id());

    // note: some values are briefly nil/default after resuming playback

    if (!this.artwork.cached() || newId) {
      this.image.setImage(this.artwork.reloaded());
    }

    if (this.track == null || newTrack.valid() || newId) {
      this.track = newTrack;

      // TODO: elapsed duration doesn't update until the track status updates
      this.info.text(this.track.getTitle().append("\n")
          .append(this.track.getSubtitle()).append("\n")
          .append(this.track.getDuration()));
      this.setToggle(this.track.playing());

      this.listening.clearChildren();
      this.listening.children(CranberryClient.LISTENING.entrySet().stream().map(entry -> {
        UUID player = entry.getKey();
        Media.Track track = entry.getValue().first;
        Artwork icon = entry.getValue().second;

        return Containers.horizontalFlow(Sizing.content(), Sizing.content())
            .child(Components
                .label(Text.literal(
                    MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(player).getProfile()
                        .getName())))
            .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(Components.wrapVanillaWidget(new ImageWidget(0, 0, icon.image()))).margins(Insets.horizontal(5)))
            .child(Components.label(track.getShortTitle()))
            .verticalAlignment(VerticalAlignment.CENTER);
      }).collect(Collectors.toList()));
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

  @Override
  public boolean shouldPause() {
    return false;
  }
}
