package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.awt.image.BufferedImage;
import java.io.InputStream;

@ModuleInfo(name = "Server Display", description = "Displays the icon and IP of the currently connected server onscreen", category = Category.HUD)
public class ServerDisplay extends HudModuleBackground implements IRegistrable {
    @Toggle(label = "Display Server Icon")
    public boolean displayIcon = true;

    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");

    private static final ServerData UNKNOWN_SERVER_DATA = new ServerData("Singleplayer", "Singleplayer", false);

    private String connectedServerIp;

    private ResourceLocation serverIcon;

    private DynamicTexture serverIconTexture;

    private ServerData currentServer;

    private String base64Image;

    public ServerDisplay() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.width = 110;
        this.height = 18;
        this.position = new ModulePosition(AnchorRegion.TOP_RIGHT, 140.0F, 10.0F);
    }

    public void enable() {
        super.enable();
        setIcon();
    }

    public String getDisplayText() {
        return "";
    }

    public Tuple<String, String> getInfoHud() {
        this.connectedServerIp = ((this.connectedServerIp = Client.formatConnectedServerIp(false)) == null) ? "< Unknown >" : this.connectedServerIp;
        return new Tuple("Server IP", this.connectedServerIp);
    }

    public void draw() {
        if (this.mc.theWorld == null || this.connectedServerIp == null)
            return;
        if (!this.drawBackground) {
            RenderUtils.drawCenteredString("[" + this.connectedServerIp + "]", getRenderX() + this.width / 2, getRenderY() + this.height / 2, this.textColor);
            return;
        }
        int x = getRenderX();
        int y = getRenderY();
        int iconSize = this.displayIcon ? this.height : 0;
        drawBackground(x, y, x + this.width, y + this.height);
        RenderUtils.drawCenteredString(this.connectedServerIp, x + (this.width - iconSize) / 2 + iconSize, y + this.height / 2, this.textColor);
        if (this.displayIcon) {
            if (this.currentServer == null || (this.currentServer.getBase64EncodedIconData() != null && !this.currentServer.getBase64EncodedIconData().equals(this.base64Image))) {
                this.currentServer = getCurrentServer();
                this.base64Image = this.currentServer.getBase64EncodedIconData();
                prepareServerIcon();
            }
            GlStateManager.enableBlend();
            GlStateManager.resetColor();
            this.mc.getTextureManager().bindTexture((this.serverIconTexture != null) ? this.serverIcon : UNKNOWN_SERVER);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
            GlStateManager.disableBlend();
        }
    }

    private void setIcon() {
        this.currentServer = getCurrentServer();
        this.serverIcon = (this.currentServer == UNKNOWN_SERVER_DATA) ? UNKNOWN_SERVER : new ResourceLocation("servers/" + this.currentServer.serverIP + "/icon");
        prepareServerIcon();
        this.connectedServerIp = ((this.connectedServerIp = Client.formatConnectedServerIp(false)) == null) ? "Singleplayer" : this.connectedServerIp;
        this.width = this.height + this.mc.fontRendererObj.getStringWidth(this.connectedServerIp) + 16;
    }

    private void prepareServerIcon() {
        if (this.currentServer == null || this.currentServer.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.serverIcon);
            this.serverIconTexture = null;
        } else {
            ByteBuf buf = Unpooled.copiedBuffer(this.currentServer.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf buf1 = Base64.decode(buf);
            BufferedImage image = null;
            try {
                image = TextureUtil.readBufferedImage((InputStream) new ByteBufInputStream(buf1));
                Validate.validState((image.getWidth() == 64), "Must be 64 pixels wide", new Object[0]);
                Validate.validState((image.getHeight() == 64), "Must be 64 pixels high", new Object[0]);
            } catch (Throwable throwable) {
                this.currentServer.setBase64EncodedIconData(null);
            } finally {
                buf.release();
                buf1.release();
            }
            assert image != null;
            if (this.serverIconTexture == null) {
                this.serverIconTexture = new DynamicTexture(image.getWidth(), image.getHeight());
                this.mc.getTextureManager().loadTexture(this.serverIcon, (ITextureObject) this.serverIconTexture);
            }
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), this.serverIconTexture.getTextureData(), 0, image.getWidth());
            this.serverIconTexture.updateDynamicTexture();
        }
    }

    private ServerData getCurrentServer() {
        return (this.mc.getCurrentServerData() == null || this.mc.isSingleplayer()) ? UNKNOWN_SERVER_DATA : this.mc.getCurrentServerData();
    }

    public void registerEvents() {
        EventBus.register(this, WorldEvent.Load.class, ev -> setIcon());
    }
}