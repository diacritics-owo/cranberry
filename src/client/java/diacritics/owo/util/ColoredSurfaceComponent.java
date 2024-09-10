package diacritics.owo.util;

import com.mojang.blaze3d.systems.RenderSystem;

import io.wispforest.owo.ui.container.WrappingParentComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;

public class ColoredSurfaceComponent<C extends Component> extends WrappingParentComponent<C> {
  private Color color = Color.WHITE;

  public ColoredSurfaceComponent(C child) {
    super(Sizing.content(), Sizing.content(), child);
  }

  @Override
  public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
    float[] colors = RenderSystem.getShaderColor().clone();
    RenderSystem.setShaderColor(colors[0] * this.color.red(), colors[1] * this.color.green(),
        colors[2] * this.color.blue(),
        colors[3] * this.color.alpha());
    this.surface().draw(context, this);
    RenderSystem.setShaderColor(colors[0], colors[1], colors[2], colors[3]);

    this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.childView);
  }

  public Color color() {
    return this.color;
  }

  public ColoredSurfaceComponent<C> color(Color color) {
    this.color = color;
    return this;
  }
}
