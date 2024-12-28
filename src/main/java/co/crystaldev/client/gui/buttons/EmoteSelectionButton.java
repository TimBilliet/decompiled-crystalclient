package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.cosmetic.CosmeticType;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.handler.OverlayHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class EmoteSelectionButton extends Button {
  private  int radius = 4;

  private  Emote emote;

  private  FadingColor fadeInColor =  new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground);

  private  FadingColor backgroundColor =  new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground);

  private  FadingColor outlineFadeInColor =  new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground);

  private  FadingColor outlineColor =  new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground);

  private  FadingColor fadeInTextColor =  new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground);

  private  FadingColor textColor =  new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground);

  private final long initTime = System.currentTimeMillis();

  public EmoteSelectionButton(int id, int x, int y, int width, int height) {
    super(id, x, y, width, height);
  }

  public EmoteSelectionButton(Emote emote, int x, int y, int radius) {
    super(-1, x, y, 0, 0, (emote != null) ? emote.name.toUpperCase().replaceAll("_", " ") : null);
    this.emote = emote;
    this.radius = radius;
    this.fadeInColor = new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralButtonBackground) {

      };
    this.outlineFadeInColor = new FadingColor(new Color(0, 0, 0, 5), this.opts.mainDisabled) {

      };
    this.fadeInTextColor = new FadingColor(new Color(0, 0, 0, 5), this.opts.neutralTextColor) {

      };
    this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    this.outlineColor = new FadingColor(this.opts.mainDisabled, this.opts.mainEnabled);
    this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.fontRenderer = Fonts.UPHEAVAL_20;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    hovered = (hovered && this.emote != null);
//    hovered = false;
    this.backgroundColor.fade(hovered);
    this.outlineColor.fade(hovered);
    this.textColor.fade(hovered);
    this.fadeInColor.fade(true);
    this.outlineFadeInColor.fade(true);
    this.fadeInTextColor.fade(true);
    RenderUtils.drawPolygon(this.x, this.y, 6, this.radius, (
        (System.currentTimeMillis() - this.initTime > this.fadeInColor.getFadeTimeMs()) ? this.backgroundColor : this.fadeInColor).getCurrentColor().getRGB());
    RenderUtils.drawPolygonOutline(this.x, this.y, 6, this.radius, 2.0F, (
        (System.currentTimeMillis() - this.initTime > this.outlineFadeInColor.getFadeTimeMs()) ? this.outlineColor : this.outlineFadeInColor).getCurrentColor().getRGB());
    if (this.emote != null) {
      GL11.glPushMatrix();
      GL11.glScalef(1.0F, 1.0F, 0.6F);
      GL11.glTranslatef(this.x, this.y, 1.0F);
      int color = ((System.currentTimeMillis() - this.initTime > this.fadeInTextColor.getFadeTimeMs()) ? this.textColor : this.fadeInTextColor).getCurrentColor().getRGB();
      if (this.fontRenderer.getStringWidth(this.displayText) > this.radius * 0.8D) {
        String[] strs = WordUtils.wrap(this.displayText, 10).split("\n");
        int height = this.fontRenderer.getStringHeight(strs);
        GL11.glTranslatef(0.0F, -(height / 2.0F) + this.fontRenderer.getStringHeight() / 2.0F, 0.0F);
        int y = 0;
        for (String str : strs) {
          this.fontRenderer.drawCenteredString(str, 0, y, color);
          y += this.fontRenderer.getStringHeight(str);
        }
      } else {
        this.fontRenderer.drawCenteredString(this.displayText, 0, 0, color);
      }
      GL11.glPopMatrix();
    }
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    if (this.emote != null && CosmeticManager.getInstance().isOwned(this.emote.name, CosmeticType.EMOTE)) {
      EmoteAPI.setEmoteClient(this.emote.name, this.mc.thePlayer);
      OverlayHandler.getInstance().getCurrentOverlay().close();
    }
  }

  public boolean isHovered(int mouseX, int mouseY) {
    int diffX = Math.abs(this.x - mouseX);
    int diffY = Math.abs(this.y - mouseY);
    return (Math.sqrt((diffX * diffX + diffY * diffY)) < this.radius);
  }
}
