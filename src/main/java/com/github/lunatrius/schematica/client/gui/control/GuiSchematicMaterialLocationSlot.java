package com.github.lunatrius.schematica.client.gui.control;

import co.crystaldev.client.gui.override.CustomGuiSlot;
import co.crystaldev.client.util.RenderUtils;
import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.schematica.client.util.BlockList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class GuiSchematicMaterialLocationSlot extends CustomGuiSlot {
    private final Minecraft minecraft = Minecraft.getMinecraft();

    private static final ResourceLocation CHECKBOX_TICKED = new ResourceLocation("crystalmod", "gui/checkbox_ticked.png");

    private static final ResourceLocation CHECKBOX_EMPTY = new ResourceLocation("crystalmod", "gui/checkbox_empty.png");

    private final GuiSchematicMaterialLocations guiLocations;

    private final HashMap<Integer, BlockPos> entries = new HashMap<>();

    protected int selectedIndex = -1;

    public GuiSchematicMaterialLocationSlot(GuiSchematicMaterialLocations guiLocations) {
        super(Minecraft.getMinecraft(), guiLocations.width, guiLocations.height, 16, guiLocations.height - 34, 24);
        this.guiLocations = guiLocations;
        int id = 0;
        for (BlockPos pos : ((BlockList.WrappedItemStack) this.guiLocations.blockList.get(guiLocations.index)).positions)
            this.entries.putIfAbsent(Integer.valueOf(id++), pos);
    }

    protected int getSize() {
        return ((BlockList.WrappedItemStack) this.guiLocations.blockList.get(this.guiLocations.index)).positions.size();
    }

    protected void elementClicked(int index, boolean par2, int par3, int par4) {
    }

    public void handleMouseInput() {
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            int index = getSlotIndexFromScreenCoords(this.mouseX, this.mouseY);
            if (index != -1)
                this.selectedIndex = index;
        }
        super.handleMouseInput();
    }

    protected boolean isSelected(int index) {
        return (index == this.selectedIndex);
    }

    protected void drawBackground() {
    }

    protected void drawContainerBackground(Tessellator tessellator) {
    }

    protected int getScrollBarX() {
        return this.width / 2 + getListWidth() / 2 + 2;
    }

    protected void drawSlot(int index, int x, int y, int var4, int mouseX, int mouseY) {
        BlockList.WrappedItemStack wrappedItemStack = this.guiLocations.blockList.get(this.guiLocations.index);
        ItemStack itemStack = wrappedItemStack.itemStack;
        BlockPos pos = this.entries.get(Integer.valueOf(index));
        GuiHelper.drawItemStackWithSlot(this.minecraft.getTextureManager(), itemStack, x, y);
        this.guiLocations.drawString(this.minecraft.fontRendererObj, String.format("x%s y%s z%s", new Object[]{Integer.valueOf(pos.getX()), Integer.valueOf(pos.getY()), Integer.valueOf(pos.getZ())}), x + 24, y + 6, 16777215);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        RenderUtils.drawModalRectWithCustomSizedTexture(x + 189, y + 5, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        if (mouseX > x && mouseY > y && mouseX <= x + 18 && mouseY <= y + 18) {
            this.guiLocations.renderToolTip(itemStack, mouseX, mouseY);
            GlStateManager.disableLighting();
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\control\GuiSchematicMaterialLocationSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */