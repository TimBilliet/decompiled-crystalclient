package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
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
        BlockPos pos = this.mc.objectMouseOver.getBlockPos();
        if(pos!= null){
            if (showX)
                out.append("X: ").append(pos.getX()).append(" ");
            if (showY)
                out.append("Y: ").append(pos.getY()).append(" ");
            if (showZ)
                out.append("Z: ").append(pos.getZ());
        }
        return out.toString();
    }
}