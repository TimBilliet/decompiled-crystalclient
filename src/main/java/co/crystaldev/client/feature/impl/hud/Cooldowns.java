package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Selector;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.Cooldown;
import co.crystaldev.client.util.objects.ModulePosition;
import com.google.common.collect.ImmutableList;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "Cooldowns", description = "Display item cool-downs onscreen", category = Category.HUD)
public class Cooldowns extends HudModuleBackground {
    @Selector(label = "Orientation", values = {"Vertical", "Horizontal"})
    public String mode = "Horizontal";

    @Colour(label = "Cooldown Color")
    public ColorObject cooldownColor = ColorObject.fromColor((GuiOptions.getInstance()).mainColor);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.#");

    private final List<Cooldown> activeCooldowns = new ArrayList<>();

    private final List<Cooldown> defaultCooldowns = ImmutableList.of(new DefaultCooldown(new ItemStack(
            Item.getItemById(368), 1), 10000L), new DefaultCooldown(new ItemStack(
            Item.getItemById(322), 1), 35000L));

    private static Cooldowns INSTANCE;

    public Cooldowns() {
        this.width = 30;
        this.height = 30;
        this.drawBackground = false;
        this.position = new ModulePosition(AnchorRegion.BOTTOM_CENTER, 0.0F, 50.0F);
        INSTANCE = this;
    }

    public String getDisplayText() {
        return null;
    }

    public void draw() {
        List<Cooldown> cooldowns = this.drawingDefaultText ? this.defaultCooldowns : this.activeCooldowns;
        if (cooldowns.isEmpty())
            return;
        boolean vertical = this.mode.equalsIgnoreCase("vertical");
        int size = 30;
        this.width = vertical ? 30 : (30 * cooldowns.size());
        this.height = vertical ? (30 * cooldowns.size()) : 30;
        double radius = 13.0D;
        int x = getRenderX();
        int y = getRenderY();
        drawBackground(x, y, x + this.width, y + this.height);
        x = (int) (x + 15.0D);
        y = (int) (y + 15.0D);
        Iterator<Cooldown> iterator = cooldowns.listIterator();
        while (iterator.hasNext()) {
            Cooldown cooldown = iterator.next();
            RenderUtils.drawSemiCircle(x, y, 13.0D, cooldown.getTimeRemaining() / (float)cooldown.getDuration(), this.cooldownColor.getRGB());
            String displayText = DECIMAL_FORMAT.format(cooldown.getTimeRemaining() / 1000.0D);
            if (!displayText.contains("."))
                displayText = displayText + ".0";
            renderItemStackOnScreen(cooldown.getItemStack(), x - 8, y - 11);
            RenderUtils.resetColor();
            Fonts.PT_SANS_BOLD_16.drawCenteredString(displayText, x, y + 7, Color.WHITE.getRGB());
            if (vertical) {
                y = (int) (y + 30.0D);
            } else {
                x = (int) (x + 30.0D);
            }
            if (cooldown.isComplete() && !(cooldown instanceof DefaultCooldown))
                iterator.remove();
        }
    }

    public void addCooldown(ItemStack stack, long duration) {
        if (!this.enabled)
            return;
        this.activeCooldowns.removeAll(this.activeCooldowns.stream().filter(c -> c.getItemStack().getIsItemStackEqual(stack)).collect(Collectors.toList()));
        this.activeCooldowns.add(new Cooldown(stack, duration, System.currentTimeMillis()));
    }

    private void renderItemStackOnScreen(ItemStack stack, int x, int y) {
        boolean blend = GL11.glGetBoolean(3042);
        RenderHelper.enableGUIStandardItemLighting();
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        RenderHelper.disableStandardItemLighting();
        if (!blend)
            GlStateManager.disableBlend();
    }

    private static final class DefaultCooldown extends Cooldown {
        private long startTime = System.currentTimeMillis();

        public DefaultCooldown(ItemStack itemStack, long duration) {
            super(itemStack, duration, 0L);
        }

        public long getTimeRemaining() {
            if (isComplete())
                this.startTime = System.currentTimeMillis();
            return Math.max(0L, getDuration() - System.currentTimeMillis() - this.startTime);
        }

        public boolean isComplete() {
            return (System.currentTimeMillis() - this.startTime > getDuration());
        }
    }

    public static Cooldowns getInstance() {
        return INSTANCE;
    }
}
