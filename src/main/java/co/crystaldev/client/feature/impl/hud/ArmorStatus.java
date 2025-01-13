package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.ItemSlotArray;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.DefaultItemSlot;
import co.crystaldev.client.util.objects.ItemSlot;
import co.crystaldev.client.util.objects.ModulePosition;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

@ModuleInfo(name = "Armor Status", description = "Display currently equipped armor onscreen", category = Category.HUD)
public class ArmorStatus extends HudModule {
    @Toggle(label = "Dynamic Text Color")
    public boolean dynamicTextColor = true;

    @Toggle(label = "Display Held Item")
    public boolean showEquippedItem = true;

    @Toggle(label = "Display Item Name")
    public boolean displayItemName = false;

    @Toggle(label = "Flash on Low Durability")
    public boolean lowEffect = true;

    @Slider(label = "Low Durability Threshold", placeholder = "{value}%", minimum = 1.0D, maximum = 100.0D, standard = 7.0D, integers = true)
    public int lowEffectThreshold = 7;

    @Selector(label = "Orientation", values = {"Vertical", "Horizontal"})
    public String mode = "Vertical";

    @Selector(label = "Durability Display", values = {"Remaining/Max", "Remaining", "Percent"})
    public String textMode = "Remaining";

    @Colour(label = "Text Color")
    public ColorObject textColor = new ColorObject(255, 255, 255, 255);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private static final int ITEM_SIZE = 16;

    private final FontRenderer fr;

    private final int verticalHeight;

    private final int horizontalHeight;

    private final ItemSlotArray slots = new ItemSlotArray(new ItemSlot[]{new ItemSlot(), new ItemSlot(ItemSlot.Type.ARMOR, 0), new ItemSlot(ItemSlot.Type.ARMOR, 1), new ItemSlot(ItemSlot.Type.ARMOR, 2), new ItemSlot(ItemSlot.Type.ARMOR, 3)});

    private final ItemSlotArray defaultSlots = new ItemSlotArray(new ItemSlot[]{(ItemSlot) new DefaultItemSlot(ItemSlot.Type.REGULAR, new ItemStack(
            Item.getItemById(276), 1)), (ItemSlot) new DefaultItemSlot(ItemSlot.Type.ARMOR, new ItemStack(
            Item.getItemById(313), 1)), (ItemSlot) new DefaultItemSlot(ItemSlot.Type.ARMOR, new ItemStack(
            Item.getItemById(312), 1)), (ItemSlot) new DefaultItemSlot(ItemSlot.Type.ARMOR, new ItemStack(
            Item.getItemById(311), 1)), (ItemSlot) new DefaultItemSlot(ItemSlot.Type.ARMOR, new ItemStack(
            Item.getItemById(310), 1))});

    public ArmorStatus() {
        this.enabled = true;
        this.position = new ModulePosition(AnchorRegion.BOTTOM_RIGHT, 5.0F, 5.0F);
        this.fr = this.mc.fontRendererObj;
        this.verticalHeight = 80;
        this.horizontalHeight = 16;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Low Durability Threshold", f -> this.lowEffect);
        setOptionVisibility("Display Item Name", f -> this.mode.equals("Vertical"));
    }

    public void draw() {
        renderItems(this.slots);
    }

    public void drawDefault() {
        if (!this.slots.anyPresent()) {
            renderItems(this.defaultSlots);
        } else {
            draw();
        }
    }

    private void renderItems(ItemSlotArray slots) {
        boolean vertical = this.mode.equals("Vertical");
        this.height = vertical ? this.verticalHeight : this.horizontalHeight;
        int x = getRenderX();
        int y = getRenderY();
        boolean top = (y < this.mc.displayHeight / 4);
        boolean left = (x < this.mc.displayWidth / 4);
        int width = 0;
        if (!vertical)
            this.width = 0;
        y = (top || !vertical) ? y : (y + this.height - this.fr.FONT_HEIGHT);
        int curX = x;
        int curY = y;
        int itemX = x;
        ItemSlot[] arr = (!vertical || top) ? slots.getReversed() : slots.get();
        for (ItemSlot slot : arr) {
            if (slot.getType() == ItemSlot.Type.ARMOR || this.showEquippedItem)
                if (slot.isPresent()) {
                    ItemStack stack = slot.getItemStack();
                    String display = getUsesString(stack, left);
                    if (vertical) {
                        if (!left)
                            itemX = Math.max(itemX, x + this.fr.getStringWidth(display));
                        width = Math.max(width, 18 + this.fr.getStringWidth(display));
                        curY += top ? 18 : -18;
                    } else {
                        renderItemOnScreen(stack, curX, curY + this.fr.FONT_HEIGHT / 2 - 8);
                        int curXBefore = curX;
                        curX = renderString(stack, left, display, curX + 16, curY) + 2;
                        width += curX - curXBefore;
                    }
                }
        }
        if (vertical)
            for (ItemSlot slot : arr) {
                if (slot.getType() == ItemSlot.Type.ARMOR || this.showEquippedItem)
                    if (slot.isPresent()) {
                        ItemStack stack = slot.getItemStack();
                        String display = getUsesString(stack, left);
                        renderString(stack, left, display, left ? (itemX + 16 + 2) : (itemX - 2 - this.fr.getStringWidth(display)), y);
                        renderItemOnScreen(stack, itemX, y + this.fr.FONT_HEIGHT / 2 - 8);
                        y += top ? 18 : -18;
                    }
            }
        this.width = width;
    }

    private int renderString(ItemStack stack, boolean left, String string, int x, int y) {
        if (stack.isItemStackDamageable()) {
            ColorObject dynamicColor = this.textColor;
            if (this.dynamicTextColor) {
                int i = (int) Math.round(255.0D - stack.getItemDamage() * 255.0D / stack.getMaxDamage());
                if (this.lowEffect && System.currentTimeMillis() / Math.max(140, i * 16) % 2L == 0L && (stack.getMaxDamage() - stack.getItemDamage()) / (float)stack.getMaxDamage() * 100.0D <= this.lowEffectThreshold) {
                    dynamicColor = new ColorObject(255, 255, 255, 255);
                } else {
                    dynamicColor = new ColorObject(255, i, i, 255);
                }
            }
            if (!this.mode.equals("Vertical") || !this.displayItemName)
                return RenderUtils.drawString(string, x, y, dynamicColor);
            String[] strings = string.split(" - ");
            if (left) {
                if (strings.length > 1)
                    return RenderUtils.drawString(" - " + strings[1], RenderUtils.drawString(strings[0], x, y, dynamicColor), y, this.textColor);
            } else if (strings.length > 1) {
                return RenderUtils.drawString(strings[1], RenderUtils.drawString(strings[0] + "&r - ", x, y, this.textColor), y, dynamicColor);
            }
            return RenderUtils.drawString(strings[0], x, y, this.textColor);
        }
        return RenderUtils.drawString(string, x, y, this.textColor);
    }

    private void renderItemOnScreen(ItemStack stack, int x, int y) {
        boolean blend = GL11.glGetBoolean(3042);
        RenderHelper.enableGUIStandardItemLighting();
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        RenderHelper.disableStandardItemLighting();
        if (!blend)
            GlStateManager.disableBlend();
        if (!stack.isItemStackDamageable()) {
            int itemAmount = 0;
            for (ItemStack itemStack : this.mc.thePlayer.inventory.mainInventory) {
                if (itemStack != null && itemStack.getItem() == stack.getItem())
                    itemAmount += itemStack.stackSize;
            }
            if (itemAmount > 1) {
                GL11.glDisable(2929);
                this.fr.drawString(itemAmount + "", x + 16 - this.mc.fontRendererObj.getStringWidth(itemAmount + ""), y + 16 - 9, -1);
                GL11.glEnable(2929);
            }
        }
    }

    private String getUsesString(ItemStack stack, boolean left) {
        String uses;
        StringBuilder builder = new StringBuilder();
        if (stack.isItemStackDamageable()) {
            int itemDamage = stack.getItemDamage();

            int maxDamage = stack.getMaxDamage();
            double percent = MathHelper.clamp_double((maxDamage - itemDamage) / (float)maxDamage * 100.0D, 0.0D, 100.0D);
            switch (this.textMode) {
                case "Remaining":
                    uses = Integer.toString(maxDamage - itemDamage);
                    break;
                case "Remaining/Max":
                    uses = (maxDamage - itemDamage) + "/" + maxDamage;
                    break;
                default:
                    uses = DECIMAL_FORMAT.format(percent) + "%";
                    break;
            }
        } else {
            uses = "";
        }
        if (left) {
            builder.append(uses);
            if (this.displayItemName && this.mode.equals("Vertical"))
                builder.append(!uses.isEmpty() ? " - &7" : "&7").append(stack.getDisplayName().trim());
        } else {
            if (this.displayItemName && this.mode.equals("Vertical"))
                builder.append("&7").append(stack.getDisplayName().trim()).append(!uses.isEmpty() ? " - " : "");
            builder.append(uses);
        }
        return ChatColor.translate(builder.toString());
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\ArmorStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */