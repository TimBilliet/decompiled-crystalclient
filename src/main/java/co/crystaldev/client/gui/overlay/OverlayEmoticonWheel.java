package co.crystaldev.client.gui.overlay;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Overlay;
import co.crystaldev.client.gui.buttons.EmoteSelectionButton;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.client.settings.KeyBinding;

import java.awt.*;

public class OverlayEmoticonWheel extends Overlay {
    private static final int RADIUS = 40;

    private int topY = 100000;

    private final FadingColor fadeInColor;

    public OverlayEmoticonWheel(KeyBinding bind) {
        super(bind);
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_28;
        this.fadeInColor = new FadingColor(new Color(255, 255, 255, 5), this.opts.hoveredTextColor, 400L) {
            {
                fade(false);
            }
        };
    }

    public void init() {
        super.init();
        for (int i = 0; i < 6; i++) {
            Emote emote = (ClientOptions.getInstance()).emoteWheel.getEmote(i);
            double angle = i / 6.0D * 2.0D * Math.PI;
            int x = (int) (Math.sin(angle) * 80.0D + this.width / 2.0D);
            int y = (int) (Math.cos(angle) * 80.0D + this.height / 2.0D);
            this.topY = Math.min(this.topY, y - 40);
            addButton(new EmoteSelectionButton(emote, x, y, 40));
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        this.fadeInColor.fade(true);
        this.fontRenderer.drawCenteredString("EMOTE SELECTOR", this.width / 2, this.topY - 25, this.fadeInColor
                .getCurrentColor().getRGB());
        RenderUtils.resetColor();
    }
}
