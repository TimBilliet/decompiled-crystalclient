package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.feature.impl.factions.Patchcrumbs;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;

@ConfigurableSize
@ModuleInfo(name = "Cannon Speed", description = "View the rate at which your Patchcrumb is changing onscreen", category = Category.HUD)
public class CannonSpeed extends HudModuleBackground {
    private float speed = 0.0F;

    public CannonSpeed() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.width = 60;
        this.height = 18;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 70.0F, 109.0F);
    }

    public void enable() {
        NotificationHandler.addNotification("&c&lNOTE: &fThis module is not 100% accurate and depends on server TPS and your ping");
        super.enable();
    }

    public Tuple<String, String> getInfoHud() {
        if ((Patchcrumbs.getInstance()).enabled) {
            updateTime();
            return new Tuple<>("Cannon Speed", getDisplayText());
        }
        return null;
    }

    public String getDisplayText() {
        updateTime();
        return String.format("%.2f sec", this.speed);
    }

    public void updateTime() {
        if (Patchcrumbs.getInstance().isUpdated()) {
            long ms = System.currentTimeMillis() - Patchcrumbs.getInstance().getLastUpdate();
            this.speed = (float) ms / 1000.0F;
            Patchcrumbs.getInstance().setUpdated(false);
        }
        if ((Patchcrumbs.getInstance()).enabled && System.currentTimeMillis() - Patchcrumbs.getInstance().getLastUpdate() >= 30000L) {
            Patchcrumbs.getInstance().setLastUpdate(0L);
            this.speed = 0.0F;
        }
    }
}
