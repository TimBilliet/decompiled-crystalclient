package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.Reference;
import co.crystaldev.client.font.Fonts;
//import co.crystaldev.client.group.GroupManager;
//import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.NavigationButton;
//import co.crystaldev.client.gui.buttons.groups.GroupAddButton;
//import co.crystaldev.client.gui.buttons.groups.GroupButton;
import co.crystaldev.client.gui.buttons.groups.GroupAddButton;
import co.crystaldev.client.gui.buttons.groups.GroupButton;
import co.crystaldev.client.gui.screens.ScreenBase;
//import co.crystaldev.client.gui.screens.screen_overlay.OverlayCreateGroup;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayCreateGroup;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.GroupCategory;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ScreenGroups extends ScreenBase {
    private NavigationButton<GroupCategory> nav;

    public NavigationButton<GroupCategory> getNav() {
        return this.nav;
    }

    private GroupSection section = null;

    private ScrollPane groups;

    private ScrollPane center;

    private ScrollPane full;

    private ScrollPane members;

    public GroupSection getSection() {
        return this.section;
    }

    private GroupAddButton groupAddButton = null;

    public void init() {
        super.init();
        this.nav = new NavigationButton(GroupCategory.LANDING, this.header.x + this.header.width / 2, this.header.y + this.header.height / 2);
        this.groups = new ScrollPane(this.content.x + 10, this.content.y, 40, this.content.height - 10);
        this.members = new ScrollPane(this.content.x + this.content.width - 140, this.content.y, 130, this.groups.height);
        this.center = new ScrollPane(this.content.x + this.groups.width + 10, this.content.y, this.members.x - this.groups.x + this.groups.width, this.groups.height);
        this.full = new ScrollPane(this.groups.x + this.groups.width, this.groups.y, this.content.width - this.groups.width - 10, this.groups.height);
        this.groups.setScrollIf(b -> b instanceof GroupButton);
        initGroupButtons();
        initSection();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        RenderUtils.drawRoundedRect(this.groups.x, this.groups.y, (this.groups.x + this.groups.width), (this.groups.y + this.groups.height), 22.0D, this.opts.sidebarBackground
                .getRGB());
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float scaledScreen = getScaledScreen();
        GL11.glPushMatrix();
        GL11.glScalef(scaledScreen, scaledScreen, scaledScreen);
        if (!this.overlay)
            drawDefaultBackground();
        boolean hasOverlay = hasOverlay();
        int mouseXScaled = (int) (mouseX / scaledScreen), buttonX = hasOverlay ? -200 : mouseXScaled;
        int mouseYScaled = (int) (mouseY / scaledScreen), buttonY = hasOverlay ? -200 : mouseYScaled;
        draw(hasOverlay ? -200 : mouseXScaled, hasOverlay ? -200 : mouseYScaled, partialTicks);
        if (this.section != null)
            this.section.draw(hasOverlay ? -200 : mouseXScaled, hasOverlay ? -200 : mouseYScaled, partialTicks);
        try {
            boolean wasHovered = false;
            if (this.section != null)
                for (Button button : this.section.buttons) {
                    if (!button.visible || button.removed ||
                            button.shouldBeCulled())
                        continue;
                    boolean hovered = button.isHovered(buttonX, buttonY);
                    button.drawButton(buttonX, buttonY, (!wasHovered && hovered));
                    if (!wasHovered && hovered)
                        wasHovered = true;
                }
            for (Button button : this.buttons) {
                if (!button.visible || button.removed ||
                        button.shouldBeCulled())
                    continue;
                boolean hovered = button.isHovered(buttonX, buttonY);
                button.drawButton(buttonX, buttonY, (!wasHovered && hovered));
                if (!wasHovered && hovered)
                    wasHovered = true;
            }
        } catch (ConcurrentModificationException ex) {
            Reference.LOGGER.error("Exception raised while rendering GUI components", ex);
        }
        GL11.glPopMatrix();
        drawOverlay(mouseX, mouseY, mouseXScaled, mouseYScaled, scaledScreen, partialTicks);
        int dwheel = Mouse.getDWheel();
        this.groups.scroll((Screen) this, mouseX, mouseY, dwheel);
        if (this.section != null)
            this.section.scroll(dwheel, mouseX, mouseY);
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonInteract(button, mouseX, mouseY, mouseButton);
        if (button instanceof GroupAddButton)
            addOverlay((Screen) new OverlayCreateGroup(this.pane.x + this.pane.width / 2 - 100, this.pane.y + this.pane.height / 2, 200));
    }

    public void keyTyped(char charTyped, int keyCode) {
        super.keyTyped(charTyped, keyCode);
        if (this.nav.wasUpdated())
            initSection();
        if (this.section != null && keyCode != 1)
            this.section.keyTyped(charTyped, keyCode);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hasOverlay = hasOverlay();
        if (this.section != null)
            this.section.mouseClicked(hasOverlay ? -200 : mouseX, hasOverlay ? -200 : mouseY, mouseButton);
        if (this.nav.wasUpdated())
            initSection();
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        if (this.section != null)
            this.section.onGuiClosed();
    }

    public void initSection() {
        if (this.section != null)
            this.section.onGuiClosed();
        if (GroupManager.getSelectedGroup() == null) {
            this.section = null;
            if (this.nav != null) {
                removeButton((Button) this.nav);
                this.nav.removed = false;
                this.nav.visible = true;
            }
            int x = this.full.x + this.full.width / 2;
            int y = this.full.y + 40;
            addButton((Button) new Label(x, y, "You don't have a group currently selected", -1, Fonts.NUNITO_SEMI_BOLD_24) {

            });
            y += Fonts.NUNITO_SEMI_BOLD_28.getStringHeight() / 2 + 10;
            String desc = "You may join a group, create a new group, or select an existing group on the left sidebar.";
            for (String str : WordUtils.wrap(desc, 60).split("\n")) {
                addButton((Button) new Label(x, y, str, this.opts.neutralTextColor.getRGB(), Fonts.NUNITO_REGULAR_16) {

                });
                y += Fonts.NUNITO_REGULAR_20.getStringHeight();
            }
            return;
        }
        removeButton(b -> b.hasAttribute("groupSection#noneSelected"));
        if (this.buttons.stream().noneMatch(b -> b.equals(this.nav)))
            addButton((Button) this.nav);
        switch ((GroupCategory) this.nav.getCurrent()) {
            case LANDING:
                this.section = new SectionLanding((Pane) this.center, (Pane) this.members);
                return;
            case RANKS:
                this.section = new SectionRanks((Pane) this.center, (Pane) this.members);
                return;
            case SETTINGS:
                this.section = new SectionSettings((Pane) this.full);
                return;
            case SCHEMATICS:
                this.section = new SectionSchematics((Pane) this.full);
                return;
            case USERS:
                this.section = new SectionMembers((Pane) this.full);
                return;
        }
        this.section = null;
    }

    public void initGroupButtons() {
        removeButton(b -> b.hasAttribute("groupSelectionButton"));
        List<Group> sorted = new ArrayList<>(GroupManager.getGroups());
        sorted.sort(Comparator.comparing(g -> g.getName().toLowerCase()));
        int x = this.groups.x + 8;
        int y = this.groups.y + 7;
        int w = this.groups.width - 16;
        final Pane scissor = this.groups.scale(getScaledScreen());
        scissor.width += 150;
        scissor.height -= 10 + w;
        for (Group group : sorted) {
            addButton((Button) new GroupButton(group, x, y, w, w) {

            });
            y += w + 4;
        }
        if (this.groupAddButton != null)
            removeButton((Button) this.groupAddButton);
        addButton((Button) (this.groupAddButton = new GroupAddButton(x, this.groups.y + this.groups.height - 8 - w, w, w) {

        }));
        this.groups.updateMaxScroll((Screen) this, 16);
    }

    public void removeButtons() {
        super.removeButtons();
        if (this.section != null)
            this.section.removeButtons();
    }

    public static void updateSection(Class<? extends GroupSection> clazz) {
        if ((Minecraft.getMinecraft()).currentScreen instanceof ScreenGroups) {
            ScreenGroups instance = (ScreenGroups) (Minecraft.getMinecraft()).currentScreen;
            if (instance.getSection().getClass() == clazz)
                instance.initSection();
        }
    }

    public static void updateMembers() {
        if ((Minecraft.getMinecraft()).currentScreen instanceof ScreenGroups) {
            ScreenGroups instance = (ScreenGroups) (Minecraft.getMinecraft()).currentScreen;
            if (instance.getSection() != null)
                instance.getSection().initGroupMembers();
        }
    }

    public static void updateGroup() {
        if ((Minecraft.getMinecraft()).currentScreen instanceof ScreenGroups) {
            ScreenGroups instance = (ScreenGroups) (Minecraft.getMinecraft()).currentScreen;
            instance.initGroupButtons();
            instance.initSection();
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\groups\ScreenGroups.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */