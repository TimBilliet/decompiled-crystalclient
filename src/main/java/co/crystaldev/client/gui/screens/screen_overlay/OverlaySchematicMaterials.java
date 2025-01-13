package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.MissingSchematicBlock;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.util.ItemStackSortType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class OverlaySchematicMaterials extends ScreenOverlay {
    private ItemStackSortType sortType = ItemStackSortType.fromString(ConfigurationHandler.sortType);

    private List<BlockList.WrappedItemStack> blockList;

    private final SchematicWorld schematic;

    private ScrollPane content;

    public OverlaySchematicMaterials(SchematicWorld schematic) {
        super(0, 0, 250, 150, "Materials");
        this.schematic = schematic;
    }

    public void init() {
        super.init();
        this.pane.x = this.mc.displayWidth / 4 - this.pane.width / 2;
        this.pane.y = this.mc.displayHeight / 4 - this.pane.height / 2;
        this.content = new ScrollPane(this.pane);
        this.content.y += 18;
        this.content.height -= 46;
        this.content.setScrollIf(b -> b.hasAttribute("material_button"));
        this.blockList = (new BlockList()).getList(this.mc.thePlayer, this.schematic, this.mc.theWorld);
        this.sortType.sort(this.blockList);
        int x = this.content.x + 5;
        int y = this.content.y + 5;
        int w = this.content.width - 10;
        int h = 18;
        Pane scissor = this.content.scale(getScaledScreen());
        for (int i = 0; i < this.blockList.size(); i++) {
            addButton(new MaterialButton(i, x, y, w, 22), b -> {
                b.addAttribute("material_button");
                b.setScissorPane(scissor);
            });
            y += 27;
        }
        x = this.pane.x + 5;
        y = this.pane.y + this.pane.height - 5 - h;
        w = (this.pane.width - 6) / 3 - 4;
        addButton(new MenuButton(-1, x, y, w, h, "Trace All"), b -> b.setOnClick(() -> {
            Schematica.getInstance().missingBlocks.addAll(Schematica.getInstance().traceAllMaterials());
        }));
        x += w + 4;
        addButton(new MenuButton(-1, x, y, w, h, "Un-trace All"), b -> b.setOnClick(() -> {
            Schematica.getInstance().clearTracerLists();
        }));
        x += w + 4;
        addButton(new MenuButton(-1, x, y, w, h, "Sort"), b -> b.setOnClick(() -> {
            this.sortType = this.sortType.next();
            this.sortType.sort(this.blockList);
        }));
        this.content.updateMaxScroll(this, 0);
        this.content.addScrollbarToScreen(this);
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        this.content.scroll(this, mouseX, mouseY);
    }

    private class MaterialButton extends Button {
        private final FadingColor backgroundColor;

        private final FadingColor textColor;

        private final MenuButton trace;

        public MaterialButton(int id, int x, int y, int width, int height) {
            super(id, x, y, width, height);
            this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
            this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
            int bw = Fonts.NUNITO_SEMI_BOLD_20.getStringWidth("Trace") + 6;
            this.trace = new MenuButton(-1, this.x + this.width - 3 - bw, this.y + this.height / 2 - 8, bw, 16, "Trace");
        }

        public void onUpdate() {
            this.trace.x = this.x + this.width - 3 - this.trace.width;
            this.trace.y = this.y + this.height / 2 - 8;
        }

        public void onInteract(int mouseX, int mouseY, int mouseButton) {
            super.onInteract(mouseX, mouseY, mouseButton);
            if (this.trace.isHovered(mouseX, mouseY)) {
                BlockList.WrappedItemStack wrappedItemStack = OverlaySchematicMaterials.this.blockList.get(this.id);
                Item item = wrappedItemStack.itemStack.getItem();
                List<MissingSchematicBlock> newMissing = Schematica.getInstance().traceAllMaterials(Block.getBlockFromItem(item)), missing = (Schematica.getInstance()).missingBlocks;
                missing.addAll(newMissing.stream().filter(b -> !missing.contains(b)).collect(Collectors.toList()));
            }
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            Screen.scissorStart(this.scissorPane);
            this.backgroundColor.fade(hovered);
            this.textColor.fade(hovered);
            BlockList.WrappedItemStack wrappedItemStack = OverlaySchematicMaterials.this.blockList.get(this.id);
            ItemStack itemStack = wrappedItemStack.itemStack;
            String itemName = wrappedItemStack.getItemStackDisplayName();
            String amount = wrappedItemStack.getFormattedAmount();
            RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.backgroundColor
                    .getCurrentColor().getRGB());
            this.fontRenderer.drawString(itemName, this.x + 6 + this.height, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                    .getCurrentColor().getRGB());
            this.fontRenderer.drawString(amount, this.trace.x - 3 - this.fontRenderer.getStringWidth(amount), this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                    .getCurrentColor().getRGB());
            this.trace.drawButton(mouseX, mouseY, (this.trace.isHovered(mouseX, mouseY) && hovered));
            RenderUtils.resetColor();
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            this.mc.getRenderItem().renderItemIntoGUI(itemStack, this.x + 3, this.y + 3);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            Screen.scissorEnd(this.scissorPane);
        }
    }
}