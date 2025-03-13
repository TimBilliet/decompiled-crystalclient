package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.feature.annotations.ConfigurableSize;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Selector;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.util.enums.AnchorRegion;
import com.github.timmekeclient.util.objects.ModulePosition;
import com.github.timmekeclient.util.type.Tuple;
import net.minecraft.util.MathHelper;

@ConfigurableSize
@ModuleInfo(name = "Speed", description = "Displays your current velocity onscreen", category = Category.HUD)
public class Speed extends HudModuleBackground {
    @Selector(label = "Unit", values = {"m/s", "b/s"})
    public String unit = "m/s";

    public Speed() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.width = 60;
        this.height = 18;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 70.0F, 55.0F);
    }

    public Tuple<String, String> getInfoHud() {
        return new Tuple("Speed", getVelocity());
    }

    public String getDisplayText() {
        return getVelocity();
    }

    private String getVelocity() {
        double distX = this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX;
        double distZ = this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ;
        return String.format("%.1f " + this.unit, new Object[]{Float.valueOf(MathHelper.sqrt_double(distX * distX + distZ * distZ) / 0.05F)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\Speed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */