package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.PlayerEvent;
import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.text.DecimalFormat;

@ConfigurableSize
@ModuleInfo(name = "Reach Display", description = "Displays your most recent attack reach onscreen", category = Category.HUD)
public class ReachDisplay extends HudModuleBackground implements IRegistrable {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private double reach = 0.0D;

    private long lastAttackTime = 0L;

    public ReachDisplay() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 70.0F, 91.0F);
        this.width = 60;
        this.height = 18;
    }

    public String getDisplayText() {
        if (System.currentTimeMillis() - this.lastAttackTime > 2000L)
            this.reach = 0.0D;
        return DECIMAL_FORMAT.format(this.reach) + " blocks";
    }

    public Tuple<String, String> getInfoHud() {
        return new Tuple("Reach", getDisplayText());
    }

    public void registerEvents() {
        EventBus.register(this, PlayerEvent.Attack.class, ev -> {
            if (this.mc.objectMouseOver == null || this.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
                return;
            Vec3 eyePos = this.mc.getRenderViewEntity().getPositionEyes(1.0F);
            this.reach = this.mc.objectMouseOver.hitVec.distanceTo(eyePos);
            this.lastAttackTime = System.currentTimeMillis();
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\ReachDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */