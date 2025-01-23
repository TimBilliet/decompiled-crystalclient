package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;

import java.lang.reflect.Field;

@ModuleInfo(name = "Auto Sell", description = "Quickly sell collection chests on SaicoPvP", category = Category.FACTIONS)

public class AutoSell extends Module implements IRegistrable {

    private boolean clickedCollectionChest = false;
    private boolean clickedSellGUI = false;
    private boolean handledSignGUI = false;
    private int quantity = 0;
    private int colOpenForXTicks = 0;

    public AutoSell() {
        this.enabled = false;
    }

    private void onClienTick(ClientTickEvent.Post ev) {
        if (mc.currentScreen instanceof GuiContainer) {
            GuiContainer gui = ((GuiContainer) mc.currentScreen);
            String inv = getInventoryName(gui);
            if (inv.contains("Collection Chest")) {
                colOpenForXTicks++;
                if(!clickedCollectionChest && colOpenForXTicks > 1){
                    colOpenForXTicks = 0;
                    clickedCollectionChest = true;
                    ItemStack itemStack = ((GuiContainer) mc.currentScreen).inventorySlots.getSlot(0).getStack();
                    if (itemStack == null)
                        return;
                    String quantityInfo = itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(4).toString();
                    try {
                        quantity = Integer.parseInt(quantityInfo.substring(22, quantityInfo.length() - 2));
                    } catch (Exception exception) {
                        Reference.LOGGER.error("Failed to parse head quantity");
                    }
                    this.mc.playerController.windowClick(this.mc.thePlayer.openContainer.windowId, 0, 1, 0, this.mc.thePlayer);
                }
            } else if (inv.contains("Sell") && !clickedSellGUI) {
                clickedSellGUI = true;
                this.mc.playerController.windowClick(this.mc.thePlayer.openContainer.windowId, 4, 0, 0, this.mc.thePlayer);
            }
        } else if (mc.currentScreen instanceof GuiEditSign && !handledSignGUI) {
            handledSignGUI = true;
            GuiEditSign guiEditSign = (GuiEditSign) mc.currentScreen;
            try {
                Field field = GuiEditSign.class.getDeclaredField("tileSign");
                field.setAccessible(true);
                TileEntitySign sign = (TileEntitySign) field.get(guiEditSign);
                sign.signText[0] = new ChatComponentText(String.valueOf(quantity));
                sign.markDirty();
                mc.thePlayer.closeScreen();
                quantity = 0;
            } catch (Exception e) {
                Reference.LOGGER.error("Error handling sign: ", e);
            }
        } else if (clickedCollectionChest || clickedSellGUI || handledSignGUI) {
            clickedCollectionChest = false;
            clickedSellGUI = false;
            handledSignGUI = false;
            colOpenForXTicks = 0;
        }
    }

    private String getInventoryName(GuiContainer guiContainer) {
        if (guiContainer instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) guiContainer;
            IInventory inventory = (guiChest.inventorySlots.getSlot(0)).inventory;
            if (inventory != null) {
                return inventory.getDisplayName().getUnformattedText();
            }
        }
        return "";
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, this::onClienTick);
    }
}
