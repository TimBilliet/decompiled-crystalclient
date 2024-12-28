package co.crystaldev.client.util;

import co.crystaldev.client.gui.Pane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

public class ScissorManager {
  private static final ScissorManager INSTANCE = new ScissorManager();

  private final Stack<Pane> stack = new Stack<>();

  private Pane current = null;

  public void push(Pane pane) {
    if (pane == null)
      return;
    this.stack.push(pane);
    applyScissor(pane);
  }

  public void pop() {
    if (!this.stack.isEmpty()) {
      this.stack.pop();
      removeScissor();
    }
    if (!this.stack.isEmpty()) {
      Pane pane = this.stack.peek();
      applyScissor(pane);
    }
  }

  public void pop(Pane pane) {
    if (pane == null || this.current != pane)
      return;
    pop();
  }

  private void applyScissor(Pane pane) {
    if (pane == null)
      return;
    int scale = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
    this.current = pane;
    int x = pane.x;
    int y = pane.y;
    int x1 = pane.x + pane.width;
    int y1 = pane.y + pane.height;
    GL11.glScissor(x * scale,

        (Minecraft.getMinecraft()).displayHeight - y1 * scale, (x1 - x) * scale, (y1 - y) * scale);
    GL11.glEnable(3089);
  }

  private void removeScissor() {
    if (this.current != null) {
      GL11.glDisable(3089);
      this.current = null;
    }
  }

  public static ScissorManager getInstance() {
    return INSTANCE;
  }
}

