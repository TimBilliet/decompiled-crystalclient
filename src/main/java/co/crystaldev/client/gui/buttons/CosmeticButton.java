package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenCosmetics;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.IconColor;
import co.crystaldev.client.util.objects.FadingColor;
import mchorse.emoticons.common.EmoteAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticButton extends Button {
    private final FadingColor fadingColor;

    private final FadingColor textColor;

    private final FadingColor lockColor;

    private final FadingColor statusColor;

    private final FadingColor statusColor1;

    private boolean locked = false;

    private final Cosmetic cosmetic;

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Cosmetic getCosmetic() {
        return this.cosmetic;
    }

    private boolean selected = false;

    private int entityScale;

    private int entityX;

    private int entityY;

    private final CosmeticPlayer player;

    public boolean isSelected() {
        return this.selected;
    }

    public CosmeticButton(CosmeticPlayer player, Cosmetic cosmetic, int x, int y, int width, int height) {
        super(-1, x, y, width, height);
        this.player = player;
        this.cosmetic = cosmetic;
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.lockColor = new FadingColor(this.opts.secondaryRed, this.opts.mainRed);
        this.statusColor = new FadingColor(this.opts.mainDisabled, this.opts.mainEnabled);
        this.statusColor1 = new FadingColor(this.opts.secondaryDisabled, this.opts.secondaryEnabled);
        updateEntityLocation();
    }

    public void onUpdate() {
        updateEntityLocation();
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        boolean wearing = isWearingCosmetic();
        if (this.selected != wearing)
            this.selected = wearing;
        this.fadingColor.fade(hovered);
        this.textColor.fade(hovered);
        this.lockColor.fade(hovered);
        this.statusColor.fade(wearing);
        this.statusColor1.fade(wearing);
        RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 6.0D, 1.7F, this.statusColor

                .getCurrentColor().getRGB(), this.statusColor1.getCurrentColor().getRGB(), this.fadingColor
                .getCurrentColor().getRGB());
        Fonts.NUNITO_SEMI_BOLD_12.drawCenteredString(this.cosmetic.getDisplayName().toUpperCase(), this.entityX, this.y + this.height - (this.y + this.height - this.entityY) / 2, this.textColor

                .getCurrentColor().getRGB());
        if (this.cosmetic instanceof Color) {
            Color color = (Color) this.cosmetic;
            if (color.getIconColor() == IconColor.CHROMA)
                ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
            RenderUtils.glColor(color.getIconColor().getColor().getRGB());
            int logoSize = this.width - 16;
            boolean blendState = GL11.glGetBoolean(3042);
            RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, this.x + 8, this.y + 8, logoSize, logoSize);
            if (blendState)
                GL11.glEnable(3042);
            ShaderManager.getInstance().disableShader();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (this.locked && !CosmeticManager.getInstance().isWildcard(this.cosmetic.getType())) {
            GL11.glTranslated(0.0D, 0.0D, 1.01D);
            RenderUtils.glColor(this.lockColor.getCurrentColor().getRGB());
            RenderUtils.drawCustomSizedResource(Resources.LOCK, this.x + this.width - 3 - 15, this.y + 3, 15, 15);
            RenderUtils.resetColor();
            GL11.glTranslated(0.0D, 0.0D, -1.01D);
        }
        Screen.scissorEnd(this.scissorPane);
    }

    private void updateEntityLocation() {
        switch (this.cosmetic.getType()) {
            case WINGS:
                this.entityScale = 31;
                this.entityX = this.x + this.width / 2;
                this.entityY = (int) (this.y + this.height * 0.76D);
                return;
            case EMOTE:
                this.entityX = this.x + this.width / 2;
                this.entityY = this.y;
                return;
        }
        this.entityScale = 52;
        this.entityX = this.x + this.width / 2;
        this.entityY = (int) (this.y + this.height * 0.79D);
    }

    public void renderPlayer() {
        if (this.cosmetic instanceof Color || this.cosmetic instanceof co.crystaldev.client.cosmetic.type.Emoticon)
            return;
        Screen.scissorStart(this.scissorPane);
        ScreenCosmetics.setRenderingPlayer(true);
        Cosmetic cloak = this.player.getCloak(), wings = this.player.getWings();
        ResourceLocation cape = this.player.getLocationOfCape();
        boolean legacy = this.player.isShouldHideLegacyCosmetics();
        this.player.setLocationOfCape(null);
        this.player.setShouldHideLegacyCosmetics(true);
        this.player.setCloak(null);
        this.player.setWings(null);
        EmoteAPI.setEmoteClient("", this.mc.thePlayer);
        switch (this.cosmetic.getType()) {
            case CLOAK:
                this.player.setCloak(this.cosmetic);
                break;
            case WINGS:
                this.player.setWings(this.cosmetic);
                break;
        }
        RenderUtils.drawEntityOnScreen(this.entityX, this.entityY, this.entityScale, 0.0F, 0.0F,
                this.cosmetic.getType().isFront() ? 20 : 200, this.mc.thePlayer);
        this.player.setShouldHideLegacyCosmetics(legacy);
        this.player.setLocationOfCape(cape);
        this.player.setCloak(cloak);
        this.player.setWings(wings);
        ScreenCosmetics.setRenderingPlayer(false);
        Screen.scissorEnd(this.scissorPane);
    }

    private boolean isWearingCosmetic() {
        if (this.mc.thePlayer == null || this.mc.theWorld == null)
            return false;
        CosmeticPlayer player = CosmeticCache.getInstance().fromPlayer(this.mc.thePlayer);
        switch (this.cosmetic.getType()) {
            case CLOAK:
                return this.cosmetic.equals(player.getCloak());
            case WINGS:
                return this.cosmetic.equals(player.getWings());
            case COLOR:
                return this.cosmetic.equals(player.getColor());
            case EMOTE:
                return (ClientOptions.getInstance()).emoteWheel.hasSelected(this.cosmetic.getName());
        }
        return false;
    }
}