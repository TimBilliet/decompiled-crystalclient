package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.feature.annotations.ConfigurableSize;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.handler.ModuleHandler;
import com.github.timmekeclient.util.enums.AnchorRegion;
import com.github.timmekeclient.util.objects.ModulePosition;
import com.github.timmekeclient.util.type.Tuple;

@ConfigurableSize
@ModuleInfo(name = "TPS", description = "View how performant the server is onscreen", category = Category.HUD)
public class TPS extends HudModuleBackground {
    @Toggle(label = "Adaptive Color")
    public boolean adaptiveColor = false;

    public TPS() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.width = 60;
        this.height = 18;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 5.0F, 127.0F);
    }

    public Tuple<String, String> getInfoHud() {
        float tps = ModuleHandler.getTps();
        return new Tuple<>("TPS", String.format("%.2f", tps));
    }

    public String getDisplayText() {
        return String.format("%.2f TPS", ModuleHandler.getTps());
    }

    public void draw() {
        int rgb = this.textColor.getRGB();
        if (this.adaptiveColor) {
            float tps = ModuleHandler.getTps();
            this.textColor.setRGB(((tps >= 17.0F) ? 5635925 : ((tps >= 14.0F) ? 16777045 : ((tps >= 8.0F) ? 16733525 : 11141120))) | 0xFF000000);
        }
        super.draw();
        this.textColor.setRGB(rgb);
    }
}

