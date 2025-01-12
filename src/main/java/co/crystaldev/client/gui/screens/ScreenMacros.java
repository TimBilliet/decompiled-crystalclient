package co.crystaldev.client.gui.screens;

import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.MacroBuilderButton;
import co.crystaldev.client.gui.buttons.MacroButton;
import co.crystaldev.client.handler.MacroHandler;
import co.crystaldev.client.util.objects.Macro;
import org.lwjgl.input.Keyboard;

public class ScreenMacros extends ScreenBase {
  public void init() {
    super.init();
    this.content.setScrollIf(b -> b.hasAttribute("macro_button"));
    initMacros();
    Keyboard.enableRepeatEvents(true);
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    super.draw(mouseX, mouseY, partialTicks);
    this.content.scroll(this, mouseX, mouseY);
  }

  public void initMacros() {
    removeButton(button -> button.hasAttribute("macro_button"));
    int w = this.content.width - this.content.width / 5;
    int h = 28;
    int x = this.content.x + this.content.width / 2 - w / 2;
    int y = this.content.y + 10;
    final Pane scissor = this.content.scale(getScaledScreen());
    addButton(new MacroBuilderButton(x, y, w, h),b-> {
      b.addAttribute("macro_button");
      b.setScissorPane(scissor);
      ;});
    y += h + 10;
    for (Macro macro : MacroHandler.getInstance().getRegisteredMacros()) {
      addButton(new MacroButton(macro, x, y, w, h),b-> {
          b.addAttribute("macro_button");
          b.setScissorPane(scissor);
          });
      y += h + 5;
    }
    this.content.updateMaxScroll(this, 0);
    this.content.addScrollbarToScreen(this);
  }

  public void onGuiClosed() {
    super.onGuiClosed();
    Keyboard.enableRepeatEvents(false);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenMacros.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */