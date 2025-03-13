package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.feature.annotations.ConfigurableSize;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import net.minecraft.util.BlockPos;

@ConfigurableSize
@ModuleInfo(name = "Looking At Coords", description = "Displays the coordinates you're looking at", category = Category.HUD)
public class LookingAtCoords extends HudModuleBackground {

    @Toggle(label = "Show X")
    public boolean showX = false;

    @Toggle(label = "Show Y")
    public boolean showY = true;

    @Toggle(label = "Show Z")
    public boolean showZ = false;

    public String getDisplayText() {
        StringBuilder out = new StringBuilder();
        if(this.mc != null && this.mc.objectMouseOver != null){
            BlockPos pos = this.mc.objectMouseOver.getBlockPos();
            if(pos!= null){
                if (showX)
                    out.append("X: ").append(pos.getX()).append(" ");
                if (showY)
                    out.append("Y: ").append(pos.getY()).append(" ");
                if (showZ)
                    out.append("Z: ").append(pos.getZ());
            }
        }
        return out.toString();
    }
}