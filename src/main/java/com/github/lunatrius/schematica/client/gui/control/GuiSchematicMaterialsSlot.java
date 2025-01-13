package com.github.lunatrius.schematica.client.gui.control;

import co.crystaldev.client.gui.override.CustomGuiSlot;
import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.schematica.client.util.BlockList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;

class GuiSchematicMaterialsSlot extends CustomGuiSlot {
    private final Minecraft minecraft = Minecraft.getMinecraft();

    private final GuiSchematicMaterials guiSchematicMaterials;

    protected int selectedIndex = -1;

    public GuiSchematicMaterialsSlot(GuiSchematicMaterials par1) {
        super(Minecraft.getMinecraft(), par1.width, par1.height, 16, par1.height - 34, 24);
        this.guiSchematicMaterials = par1;
        this.selectedIndex = -1;
    }

    protected int getSize() {
        return this.guiSchematicMaterials.blockList.size();
    }

    protected void elementClicked(int index, boolean par2, int par3, int par4) {
        this.selectedIndex = index;
        this.minecraft.displayGuiScreen((GuiScreen) new GuiSchematicMaterialLocations(this.guiSchematicMaterials.blockList, this.selectedIndex, this.guiSchematicMaterials));
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
        BlockList.WrappedItemStack wrappedItemStack = this.guiSchematicMaterials.blockList.get(index);
        ItemStack itemStack = wrappedItemStack.itemStack;
        String itemName = wrappedItemStack.getItemStackDisplayName();
        String amount = wrappedItemStack.getFormattedAmount();
        GuiHelper.drawItemStackWithSlot(this.minecraft.getTextureManager(), itemStack, x, y);
        this.guiSchematicMaterials.drawString(this.minecraft.fontRendererObj, itemName, x + 24, y + 6, 16777215);
        this.guiSchematicMaterials.drawString(this.minecraft.fontRendererObj, amount, x + 215 - this.minecraft.fontRendererObj.getStringWidth(amount), y + 6, 16777215);
        if (mouseX > x && mouseY > y && mouseX <= x + 18 && mouseY <= y + 18) {
            this.guiSchematicMaterials.renderToolTip(itemStack, mouseX, mouseY);
            GlStateManager.disableLighting();
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\control\GuiSchematicMaterialsSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */