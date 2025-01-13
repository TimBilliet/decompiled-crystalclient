package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ConfigurableSize
@ModuleInfo(name = "Potion Status", description = "Display currently applied potion effects onscreen", category = Category.HUD)
public class PotionStatus extends HudModule {
    @Toggle(label = "Show Night Vision")
    public boolean showNightVision = false;

    @Toggle(label = "Compact Mode")
    public boolean compactMode = false;

    @Toggle(label = "Dynamic Potion Color")
    public boolean dynamicColor = true;

    @Colour(label = "Text Color", isTextRender = true)
    public ColorObject textColor = new ColorObject(255, 255, 255, 255);

    private static final List<PotionEffect> DEFAULT_POTION_EFFECTS = Arrays.asList(new PotionEffect(Potion.damageBoost
            .getId(), 0, 1), new PotionEffect(Potion.moveSpeed
            .getId(), 0, 2), new PotionEffect(Potion.fireResistance
            .getId(), 0, 0), new PotionEffect(Potion.waterBreathing
            .getId(), 0, 0));

    private final FontRenderer fr;

    private final int compactWidth;

    private final int regularWidth;

    public PotionStatus() {
        this.enabled = true;
        this.fr = this.mc.fontRendererObj;
        this.position = new ModulePosition(AnchorRegion.CENTER_LEFT, 5.0F, 0.0F);
        this.width = this.height = 0;
        this.compactWidth = this.fr.getStringWidth("Water Breathing 999 - 999:59");
        this.regularWidth = this.fr.getStringWidth("Water Breathing 999") + 16;
    }

    public void draw() {
        renderEffects(this.mc.thePlayer.getActivePotionEffects());
    }

    public void drawDefault() {
        renderEffects(DEFAULT_POTION_EFFECTS);
    }

    private void renderEffects(Collection<PotionEffect> potions) {
        this.width = this.compactMode ? this.compactWidth : this.regularWidth;
        this.height = this.compactMode ? (potions.size() * this.fr.FONT_HEIGHT) : (potions.size() * (this.fr.FONT_HEIGHT * 2 + 2));
        int x = getRenderX();
        int y = getRenderY();
        boolean left = (x < this.mc.displayWidth / 4);
        for (PotionEffect effect : potions) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            int effectX = left ? x : (x + this.width);
            if (potion == Potion.nightVision && !this.showNightVision)
                continue;
            if (this.compactMode) {
                String str = I18n.format(effect.getEffectName()) + " " + (effect.getAmplifier() + 1) + " - " + Potion.getDurationString(effect);
                if (this.dynamicColor) {
                    RenderUtils.drawString(str, effectX - (left ? 0 : this.fr.getStringWidth(str)), y, potion.getLiquidColor());
                } else {
                    RenderUtils.drawString(str, effectX - (left ? 0 : this.fr.getStringWidth(str)), y, this.textColor);
                }
                y += this.fr.FONT_HEIGHT;
                continue;
            }
            y++;
            String name = I18n.format(effect.getEffectName()) + " " + (effect.getAmplifier() + 1);
            String duration = Potion.getDurationString(effect);
            if (potion.hasStatusIcon()) {
                this.mc.getTextureManager().bindTexture(Resources.INVENTORY_TEXTURE);
                int iconIndex = potion.getStatusIconIndex();
                RenderUtils.resetColor();
                RenderUtils.drawTexturedModalRect(left ? effectX : (effectX - 18), y, iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);
            }
            effectX += 22 * (left ? 1 : -1);
            if (this.dynamicColor) {
                RenderUtils.drawString(name, effectX - (left ? 0 : this.fr.getStringWidth(name)), y + 1, potion.getLiquidColor());
            } else {
                RenderUtils.drawString(name, effectX - (left ? 0 : this.fr.getStringWidth(name)), y + 1, this.textColor);
            }
            RenderUtils.drawString(duration, effectX - (left ? 0 : this.fr.getStringWidth(name)), y + this.fr.FONT_HEIGHT + 1, this.textColor);
            y += this.fr.FONT_HEIGHT * 2 + 2;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\PotionStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */