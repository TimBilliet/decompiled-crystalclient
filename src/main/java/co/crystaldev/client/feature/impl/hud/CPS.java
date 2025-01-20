package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;

import java.util.ArrayDeque;

@ConfigurableSize
@ModuleInfo(name = "CPS", description = "Displays your clicks-per-second onscreen", category = Category.HUD)
public class CPS extends HudModuleBackground implements IRegistrable {
    @Toggle(label = "Show Right Click CPS")
    public boolean rightClickCps = true;

    private static final char LINE = '|';

    private final ArrayDeque<Long> clickListLeft = new ArrayDeque<>(), clickListRight = new ArrayDeque<>();

    public CPS() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 5.0F, 91.0F);
        this.width = 60;
        this.height = 18;
    }

    public String getDisplayText() {
        return getLeftCps() + (this.rightClickCps ? String.format(" %c %d", new Object[]{Character.valueOf('|'), Integer.valueOf(getRightCps())}) : "") + " CPS";
    }

    public Tuple<String, String> getInfoHud() {
        return new Tuple("CPS", getDisplayText());
    }

    public void disable() {
        this.clickListLeft.clear();
        this.clickListRight.clear();
        super.disable();
    }

    private int getLeftCps() {
        this.clickListLeft.removeIf(x -> (x.longValue() < System.currentTimeMillis() - 1000L));
        return this.clickListLeft.size();
    }

    private int getRightCps() {
        this.clickListRight.removeIf(x -> (x.longValue() < System.currentTimeMillis() - 1000L));
        return this.clickListRight.size();
    }

    public void registerEvents() {
        EventBus.register(this, InputEvent.Mouse.class, ev -> {
            if (ev.buttonState && ev.button == 0) {
                this.clickListLeft.add(Long.valueOf(System.currentTimeMillis()));
            } else if (this.rightClickCps && ev.buttonState && ev.button == 1) {
                this.clickListRight.add(Long.valueOf(System.currentTimeMillis()));
            }
        });
    }
}

