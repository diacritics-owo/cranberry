package diacritics.owo.gui.screen;

import org.jetbrains.annotations.NotNull;
import de.androidpit.colorthief.ColorThief;
import diacritics.owo.CranberryClient;
import diacritics.owo.gui.widget.ImageWidget;
import diacritics.owo.util.Artwork;
import diacritics.owo.util.ColoredSurfaceComponent;
import diacritics.owo.util.CranberryHelpers;
import diacritics.owo.util.Media;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import java.util.UUID;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;

public class MusicScreen extends BaseOwoScreen<FlowLayout> {
  private ImageWidget image;
  private LabelComponent info;
  private ButtonComponent toggle;
  private FlowLayout listening;

  private ColoredSurfaceComponent<ParentComponent> surface;
  private ColoredSurfaceComponent<FlowLayout> listeningSurface;

  private Media.Track track;
  private Artwork artwork = new Artwork(CranberryHelpers.IMAGE_SIZE.width(), CranberryHelpers.IMAGE_SIZE.height());
  private int color;

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
          Media.toggle();
        });
    this.image = new ImageWidget(0, 0,
        Artwork.empty(CranberryHelpers.IMAGE_SIZE.width(), CranberryHelpers.IMAGE_SIZE.height()));
    this.listening = Containers.verticalFlow(Sizing.content(), Sizing.content());
    this.listening.padding(Insets.of(5));

    this.listeningSurface = new ColoredSurfaceComponent<>(this.listening);
    this.listeningSurface.surface(Surface.PANEL_INSET);

    this.surface = new ColoredSurfaceComponent<>(
        Containers.renderEffect(Containers.verticalFlow(Sizing.content(), Sizing.content())
            .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Components.wrapVanillaWidget(this.image))
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(this.info).child(this.toggle).margins(Insets.left(10)))
                .verticalAlignment(VerticalAlignment.CENTER)
                .padding(Insets.bottom(10)))
            .child(this.listeningSurface)
            .padding(Insets.of(10))
            .verticalAlignment(VerticalAlignment.CENTER)));

    this.surface.surface(Surface.PANEL);

    rootComponent
        .child(this.surface);
  }

  @Override
  public void tick() {
    Media.Track newTrack = Media.track();
    boolean newId = newTrack.id() == null || !newTrack.id().equals(this.track == null ? null : this.track.id());

    // note: some values are briefly nil/default after resuming playback

    if (!this.artwork.cached() || newId) {
      NativeImage image = this.artwork.reloaded();
      this.image.setImage(image);

      if (CranberryClient.CONFIG.colorBackground()) {
        BufferedImage buffered = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
          for (int y = 0; y < image.getHeight(); y++) {
            buffered.setRGB(x, y, image.getColor(x, y));
          }
        }

        this.color = CranberryHelpers.toArgb(ColorThief.getColorMap(buffered, 2).vboxes.get(0).avg(false));
      } else {
        this.color = 0xffffffff;
      }

      this.surface.color(Color.ofArgb(this.color));
      this.listeningSurface.color(this.surface.color());
    }

    if (this.track == null || newTrack.valid() || newId) {
      this.track = newTrack;

      // reset the width
      this.listening.horizontalSizing(Sizing.fixed(0));

      this.info.text(this.track.getTitle().append("\n")
          .append(this.track.getSubtitle().append("\n"))
          .append(this.track.getDuration()));
      this.setToggle(this.track.playing());
    }

    // the icon is cached so this shouldn't be too bad?
    this.listening.clearChildren();
    this.listening.children(CranberryClient.LISTENING.entrySet().stream().map(entry -> {
      UUID player = entry.getKey();
      Media.Track track = entry.getValue().first;
      Artwork icon = entry.getValue().second;

      PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(player);

      if (playerListEntry == null)
        return null;

      return Containers.horizontalFlow(Sizing.content(), Sizing.content())
          .child(Components
              .label(Text.literal(
                  playerListEntry.getProfile()
                      .getName())))
          .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
              .child(Components.wrapVanillaWidget(new ImageWidget(0, 0, icon.image()))).margins(Insets.horizontal(5)))
          .child(Components.label(track.getShortTitle()))
          .verticalAlignment(VerticalAlignment.CENTER)
          .margins(Insets.vertical(2));
    }).filter(x -> x != null).collect(Collectors.toList()));

    // the text and image can update out of sync, so we need to update the color
    // every tick (~~we could update the color only when needed but the overhead is
    // negligible~~)
    this.info.text(this.info.text().copy().withColor(CranberryHelpers.textColor(this.color)));

    // make sure the width of the parent is at least enough to fit everything (see
    // below)
    this.listening.horizontalSizing(Sizing.content());

    // ditto
    ParentComponent parent = this.listeningSurface.parent();
    Insets padding = parent.padding().get();
    this.listening.sizing(Sizing.fixed(parent.width() - padding.left() - padding.right()), Sizing.content());
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
