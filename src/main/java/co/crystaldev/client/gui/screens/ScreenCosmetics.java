package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Resources;
import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.CosmeticType;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.duck.AbstractClientPlayerExt;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.*;
import co.crystaldev.client.gui.buttons.settings.ToggleButton;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.IconColor;
import co.crystaldev.client.util.objects.resources.cosmetic.ICosmeticTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScreenCosmetics extends ScreenBase {
    public static boolean isRenderingPlayer() {
        return isRenderingPlayer;
    }

    public static void setRenderingPlayer(boolean isRenderingPlayer) {
        ScreenCosmetics.isRenderingPlayer = isRenderingPlayer;
    }

    private static boolean isRenderingPlayer = false;

    private NavigationButton<CosmeticType> nav;

    private SearchButton search;

    private Pane entity;

    private Pane entityScaled;

    private ScrollPane cosmetics;

    private MenuButton removeButton;

    private boolean newlyOpened = true;

    public void init() {
        super.init();
        int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2;
        int h = this.header.height - half * 2;
        addButton((this.nav = new NavigationButton(CosmeticType.CLOAK, this.header.x + this.header.width / 2, this.header.y + this.header.height / 2)));
        addButton((this.search = new SearchButton(this.header.x + this.header.width - half - h, this.header.y + half, h, h * 6, h)));
        this.cosmetics = new ScrollPane(this.content.x, this.content.y, this.content.width - this.sidebar.width, this.content.height);
        this.entity = new Pane(this.content.x + this.cosmetics.width, this.content.y, this.content.width - this.cosmetics.width, this.content.height);
        this.entityScaled = this.content.scale(getScaledScreen());
        this.cosmetics.setScrollIf(b -> b instanceof CosmeticButton);
        initCosmetics();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        Screen.scissorStart(this.entityScaled);
        isRenderingPlayer = true;
        RenderUtils.drawEntityOnScreen(this.entity.x + this.entity.width / 2, (int) (this.entity.y + this.entity.height * 0.78D) + 10, 75, 0.0F, 0.0F,
                (this.nav.getCurrent()).isFront() ? 20 : 200, (EntityLivingBase) this.mc.thePlayer);
        isRenderingPlayer = false;
        Screen.scissorEnd(this.entityScaled);
        CosmeticPlayer cp = CosmeticCache.getInstance().fromPlayer(this.mc.thePlayer);
        boolean shouldUpdate = cp.shouldUpdateCosmetic();
        cp.setShouldUpdateCosmetic(false);
        for (Button button : this.buttons) {
            if (button instanceof CosmeticButton) {
                CosmeticButton cosmeticButton = (CosmeticButton) button;
                ITextureObject tex = cosmeticButton.getCosmetic().getTexture();
                boolean flag = (tex instanceof ICosmeticTexture && ((ICosmeticTexture) tex).isTextureLoaded());
                if (this.newlyOpened || !button.shouldBeCulled() || !flag)
                    cosmeticButton.renderPlayer();
            }
        }
        cp.setShouldUpdateCosmetic(shouldUpdate);
        super.draw(mouseX, mouseY, partialTicks);
        RenderUtils.drawRoundedRect(this.entity.x, this.entity.y, (this.entity.x + this.entity.width), (this.entity.y + this.entity.height), 30.0D, this.opts.sidebarBackground
                .getRGB(), false, false, false, true);
        this.cosmetics.scroll(this, mouseX, mouseY);
        this.newlyOpened = false;
    }

    public void keyTyped(char key, int keycode) {
        super.keyTyped(key, keycode);
        if (this.nav.wasUpdated() || this.search.wasUpdated())
            initCosmetics();
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonInteract(button, mouseX, mouseY, mouseButton);
        CosmeticPlayer player = ((AbstractClientPlayerExt) this.mc.thePlayer).crystal$getCosmeticPlayer();
        if (button.equals(this.removeButton))
            switch (this.nav.getCurrent()) {
                case CLOAK:
                    player.setCloak(null);
                    break;
                case COLOR:
                    player.setCloak(CosmeticManager.COLOR_WHITE);
                    break;
                case WINGS:
                    player.setWings(null);
                    break;
            }
        if (button instanceof CosmeticButton && !((CosmeticButton) button).isLocked()) {
            Cosmetic cosmetic = ((CosmeticButton) button).getCosmetic();
            if (cosmetic.getType() == CosmeticType.EMOTE) {
                (ClientOptions.getInstance()).emoteWheel.addEmote(cosmetic.getName());
            } else {
                switch (this.nav.getCurrent()) {
                    case CLOAK:
                        player.setCloak(cosmetic);
                        break;
                    case WINGS:
                        player.setWings(cosmetic);
                        break;
                    case COLOR:
                        player.setColor(cosmetic);
                        break;
                }
            }
        }
        if (this.nav.wasUpdated())
            initCosmetics();
    }

    public void initCosmetics() {
        if (this.removeButton != null)
            removeButton(this.removeButton);
        removeButton(b -> b.hasAttribute("cosmetic"));
        int w = (int) (this.entity.width * 0.82F);
        int h = 18;
        int x = this.entity.x + this.entity.width / 2 - w / 2;
        int y = this.entity.y + this.entity.height - x - this.entity.x - h + 6;
        addButton(new MenuResourceButton(-1, x, this.entity.y + x - this.entity.x + 6, w, h, "Store", Resources.SHOPPING_CART, 10), b -> {
            b.addAttribute("cosmetic");
        });
        addButton(new ToggleButton(-1, x, this.entity.y + x - this.entity.x + 6 + h + 4, w, h, "Display Unowned", (ClientOptions.getInstance()).showUnownedCosmetics), b -> {
            b.addAttribute("cosmetic");
        });
        addButton(this.removeButton = new MenuButton(-1, x, this.entity.height + entity.width, w, h, String.format("Disable %s", (this.nav.getCurrent()).getSingularForm())), b -> {
            b.addAttribute("cosmetic");
        });
        int margin = 20;
        w = (this.cosmetics.width - margin * 4) / 3;
        y = this.cosmetics.y + 5;
        x = this.cosmetics.x + margin;
        int x1 = x + w + margin;
        int x2 = x1 + w + margin;
        switch (this.nav.getCurrent()) {
            case CLOAK:
                h = (int) (w * 1.8D) + 10;
                break;
            case COLOR:
            case WINGS:
                h = w + 20;
                break;
            case EMOTE:
                h = 20;
                break;
        }
        int index = 0;
        final Pane scissor = this.cosmetics.scale(getScaledScreen());
        List<Cosmetic> ownedCosmetics = CosmeticManager.getInstance().getOwnedCosmetics();
        List<Cosmetic> cosmetics = new ArrayList<>(ownedCosmetics);
        if (this.nav.getCurrent() != CosmeticType.COLOR)
            cosmetics.sort(Comparator.comparing(c -> c.getDisplayName().toLowerCase()));
        List<Cosmetic> unowned = new ArrayList<>(CosmeticManager.getInstance().getAllCosmetics());
        unowned.removeIf(cosmetics::contains);
        if (this.nav.getCurrent() != CosmeticType.COLOR)
            unowned.sort(Comparator.comparing(c -> c.getDisplayName().toLowerCase()));
        cosmetics.addAll(unowned);
        CosmeticPlayer player = CosmeticCache.getInstance().fromPlayer(this.mc.thePlayer);
        for (Cosmetic cosmetic : cosmetics) {
            if (cosmetic.getType() == CosmeticType.COLOR && ((Color) cosmetic).getIconColor() == IconColor.WHITE)
                continue;
            if (!this.search.matchesQuery(cosmetic.getDisplayName()))
                continue;
            if (cosmetic.getType().equals(this.nav.getCurrent())) {
                addButton(new CosmeticButton(player, cosmetic, (index == 0) ? x : ((index == 1) ? x1 : x2), y, w, h), b -> {
                    b.addAttribute("cosmetic");
                    b.setScissorPane(scissor);
                });
                index++;
                if (index == 3) {
                    y += h + margin;
                    index = 0;
                }
            }
        }
        this.cosmetics.updateMaxScroll(this, 10);
        this.cosmetics.addScrollbarToScreen(this);
    }
}