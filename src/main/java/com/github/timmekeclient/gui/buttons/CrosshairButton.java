package com.github.timmekeclient.gui.buttons;

import com.github.timmekeclient.Resources;
import com.github.timmekeclient.feature.impl.combat.CrosshairSettings;
import com.github.timmekeclient.gui.Button;
import com.github.timmekeclient.gui.Screen;
import com.github.timmekeclient.util.ClientTextureManager;
import com.github.timmekeclient.util.RenderUtils;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.LinkedList;

public class CrosshairButton extends Button {
    private final LinkedList<ResourceLocation> backgrounds = new LinkedList<>(Arrays.asList(Resources.CROSSHAIR_BACKGROUNDS));

    private int backgroundIndex = 0;

    public CrosshairButton(int x, int y, int width, int height) {
        super(-1, x, y, width, height);
        for (ResourceLocation bg : this.backgrounds)
            ClientTextureManager.getInstance().loadTextureMipMap(bg);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        ResourceLocation bg = this.backgrounds.get(this.backgroundIndex);
        RenderUtils.drawCustomSizedResource(bg, this.x, this.y, this.width, this.height);
        CrosshairSettings.getInstance().renderCrosshair(this.x + this.width / 2, this.y + this.height / 2);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.8D);
        RenderUtils.drawCustomSizedResource(Resources.CHEVRON_LEFT, this.x + 2, this.y + this.height / 2 - 8, 16, 16);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.8D);
        RenderUtils.drawCustomSizedResource(Resources.CHEVRON_RIGHT, this.x + this.width - 18, this.y + this.height / 2 - 8, 16, 16);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        Screen.scissorEnd(this.scissorPane);
    }

    public void mouseDown(Screen currentScreen, int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0)
            return;
        if (previousHovered(mouseX, mouseY))
            this.backgroundIndex = (this.backgroundIndex - 1 == -1) ? (this.backgrounds.size() - 1) : (this.backgroundIndex - 1);
        if (nextHovered(mouseX, mouseY))
            this.backgroundIndex = (this.backgroundIndex + 1 == this.backgrounds.size()) ? 0 : (this.backgroundIndex + 1);
    }

    private boolean previousHovered(int mouseX, int mouseY) {
        int x = this.x + 2;
        int y = this.y + this.height / 2 - 8;
        int width = 16;
        int height = 16;
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }

    private boolean nextHovered(int mouseX, int mouseY) {
        int x = this.x + this.width - 18;
        int y = this.y + this.height / 2 - 8;
        int width = 16;
        int height = 16;
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }
}
