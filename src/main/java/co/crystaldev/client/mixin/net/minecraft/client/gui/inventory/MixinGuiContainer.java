package co.crystaldev.client.mixin.net.minecraft.client.gui.inventory;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Set;

@Mixin({GuiContainer.class})
public abstract class MixinGuiContainer extends GuiScreen {
    @Unique
    private static final GuiTextField searchBar = new GuiTextField(0, (Minecraft.getMinecraft()).fontRendererObj, 4, 4, 120, 15);

    @Shadow
    private int lastClickButton;

    @Shadow
    private long lastClickTime;

    @Shadow
    private int dragSplittingButton;

    @Shadow
    private int dragSplittingRemnant;

    @Shadow
    private Slot clickedSlot;

    @Shadow
    private ItemStack draggedStack;

    @Shadow
    private boolean isRightMouseClick;

    @Shadow
    @Final
    protected Set<Slot> dragSplittingSlots;

    @Shadow
    protected boolean dragSplitting;

    @Shadow
    public Container inventorySlots;

    @Shadow
    private int dragSplittingLimit;

    @Shadow
    private boolean doubleClick;

    @Shadow
    private Slot lastClickSlot;

    @Shadow
    private boolean ignoreMouseUp;

    @Shadow
    protected int guiLeft;

    @Shadow
    protected int guiTop;

    @Shadow
    protected int xSize;

    @Shadow
    protected int ySize;

    @Shadow
    private ItemStack shiftClickedSlot;

    @Shadow
    protected abstract boolean checkHotbarKeys(int paramInt);

    @Shadow
    protected abstract void updateDragSplitting();

    @Shadow
    protected abstract Slot getSlotAtPosition(int paramInt1, int paramInt2);

    @Shadow
    protected abstract void handleMouseClick(Slot paramSlot, int paramInt1, int paramInt2, int paramInt3);

    @Overwrite
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchBar.mouseClicked(mouseX, mouseY, mouseButton);
        boolean flag = (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100);
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        long i = Minecraft.getSystemTime();
        this.doubleClick = (this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton);
        this.ignoreMouseUp = false;
        if (mouseButton == 0 || mouseButton == 1 || flag) {
            int j = this.guiLeft;
            int k = this.guiTop;
            boolean flag1 = (mouseX < j || mouseY < k || mouseX >= j + this.xSize || mouseY >= k + this.ySize);
            if (slot != null)
                flag1 = false;
            int l = -1;
            if (slot != null)
                l = slot.slotNumber;
            if (flag1)
                l = -999;
            if (this.mc.gameSettings.touchscreen && flag1 && this.mc.thePlayer.inventory.getItemStack() == null) {
                this.mc.displayGuiScreen(null);
                return;
            }
            if (l != -1)
                if (this.mc.gameSettings.touchscreen) {
                    if (slot != null && slot.getHasStack()) {
                        this.clickedSlot = slot;
                        this.draggedStack = null;
                        this.isRightMouseClick = (mouseButton == 1);
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.dragSplitting) {
                    if (this.mc.thePlayer.inventory.getItemStack() == null) {
                        if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                            handleMouseClick(slot, l, mouseButton, 3);
                        } else {
                            boolean flag2 = (l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)));
                            int i1 = 0;
                            if (flag2) {
                                this.shiftClickedSlot = slot.getHasStack() ? slot.getStack() : null;
                                i1 = 1;
                            } else if (l == -999) {
                                i1 = 4;
                            }
                            handleMouseClick(slot, l, mouseButton, i1);
                        }
                        this.ignoreMouseUp = true;
                    } else {
                        this.dragSplitting = true;
                        this.dragSplittingButton = mouseButton;
                        this.dragSplittingSlots.clear();
                        if (mouseButton == 0) {
                            this.dragSplittingLimit = 0;
                        } else if (mouseButton == 1) {
                            this.dragSplittingLimit = 1;
                        } else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                            this.dragSplittingLimit = 2;
                        }
                    }
                }
        }
        this.lastClickSlot = slot;
        checkHotbarKeys(mouseButton - 100);
        this.lastClickTime = i;
        this.lastClickButton = mouseButton;
    }

    @Inject(method = {"updateDragSplitting"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;")}, cancellable = true)
    private void fixRemnants(CallbackInfo ci) {
        if (this.dragSplittingButton == 2) {
            this.dragSplittingRemnant = this.mc.thePlayer.inventory.getItemStack().getMaxStackSize();
            ci.cancel();
        }
    }

    @Inject(method = {"initGui"}, at = {@At("TAIL")})
    public void hook$initGui(CallbackInfo ci) {
        Keyboard.enableRepeatEvents(true);
    }

    @Inject(method = {"onGuiClosed"}, at = {@At("HEAD")})
    public void hook$onGuiClosed(CallbackInfo ci) {
        Keyboard.enableRepeatEvents(false);
    }

    @Inject(method = {"updateScreen"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;updateScreen()V", shift = At.Shift.AFTER)})
    public void hook$updateScreen(CallbackInfo ci) {
        searchBar.updateCursorCounter();
    }

    @Inject(method = {"keyTyped"}, cancellable = true, at = {@At("HEAD")})
    public void hook$keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (searchBar.textboxKeyTyped(typedChar, keyCode))
            ci.cancel();
    }

    @Inject(method = {"drawScreen"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawDefaultBackground()V", shift = At.Shift.AFTER)})
    public void drawScreen(int k1, int slot, float i1, CallbackInfo ci) {
        if ((ClientOptions.getInstance()).inventorySearchBar)
            searchBar.drawTextBox();
    }

    @Inject(method = {"drawSlot"}, cancellable = true, at = {@At("HEAD")})
    private void impl$drawSlot(Slot slotIn, CallbackInfo ci) {
        ci.cancel();
        int i = slotIn.xDisplayPosition;
        int j = slotIn.yDisplayPosition;
        ItemStack itemstack = slotIn.getStack();
        boolean search = searchItem(itemstack);
        boolean flag = false;
        boolean flag1 = (slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick);
        ItemStack itemstack1 = this.mc.thePlayer.inventory.getItemStack();
        String s = null;
        if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemstack != null) {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemstack1 != null) {
            if (this.dragSplittingSlots.size() == 1)
                return;
            if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, (slotIn.getStack() == null) ? 0 : (slotIn.getStack()).stackSize);
                if (itemstack.stackSize > itemstack.getMaxStackSize()) {
                    s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }
                if (itemstack.stackSize > slotIn.getItemStackLimit(itemstack)) {
                    s = EnumChatFormatting.YELLOW + "" + slotIn.getItemStackLimit(itemstack);
                    itemstack.stackSize = slotIn.getItemStackLimit(itemstack);
                }
            } else {
                this.dragSplittingSlots.remove(slotIn);
                updateDragSplitting();
            }
        }
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        if (itemstack == null) {
            String s1 = slotIn.getSlotTexture();
            if (s1 != null) {
                TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite(s1);
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }
        if (!flag1) {
            if (flag)
                drawRect(i, j, i + 16, j + 16, -2130706433);
            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, i, j);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemstack, i, j, s);
            GlStateManager.disableDepth();
        }
        if (!searchBar.getText().isEmpty() && !search) {
            this.zLevel = 151.0F;
            drawGradientRect(i, j, i + 16, j + 16, -1610612736, -1610612736);
        }
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private boolean searchItem(ItemStack is) {
        String searchText = searchBar.getText().toLowerCase();
        if (searchText.isEmpty())
            return true;
        if (is != null) {
            if (is.getDisplayName().toLowerCase().contains(searchText) || is.getUnlocalizedName().toLowerCase().contains(searchText))
                return true;
            for (String line : ItemUtils.getLoreLinesStripped(is)) {
                if (line.toLowerCase().contains(searchText))
                    return true;
            }
        }
        return false;
    }
}

