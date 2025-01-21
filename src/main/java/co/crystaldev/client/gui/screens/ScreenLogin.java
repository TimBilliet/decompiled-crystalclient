package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Reference;
import co.crystaldev.client.Resources;
import co.crystaldev.client.account.*;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.ScreenPanorama;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.*;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

public class ScreenLogin extends ScreenPanorama {
    private TextInputField offlineName;

    private MenuButton microsoftButton;

    private ResourceButton returnButton;

    private boolean shouldExit = false;

    private Thread thread = null;

    private HttpServer httpServer;

    private static final String pageContent = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "  <title>Logged in!</title>\n" +
            "  <style>\n" +
            "    body {\n" +
            "      display: flex;\n" +
            "      justify-content: center;\n" +
            "      align-items: center;\n" +
            "      height: 100vh;\n" +
            "      margin: 0;\n" +
            "      background-color: #333;\n" +
            "      font-family: 'Arial', sans-serif;\n" +
            "    }\n" +
            "    h1 {\n" +
            "      font-size: 3.5em;\n" +
            "      color: #f0f8ff;\n" +
            "    }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>You may now close this window</h1>\n" +
            "</body>\n" +
            "</html>";


    public ScreenLogin(GuiScreen parent) {
        super(parent, Resources.PANORAMA_IMAGES, 256);
    }

    public void init() {
        super.init();
        int pw = 200;
        int ph = 194;
        float scale = getScaledScreen();
        this.pane = new Pane((this.width / scale) / 2.0D - pw / 2.0D, (this.height / scale) / 2.0D - ph / 2.0D, pw, ph);
        int w = (int) (this.pane.width * 0.9D);
        int h = 18;
        int x = this.pane.x + this.pane.width / 2 - w / 2;
        int y = this.pane.y + (int) (this.pane.width / 2.0F * 0.5F) + 40;
        this.returnButton = new ResourceButton(-1, x, this.pane.y + this.pane.width / 2 - w / 2, 18, 18, Resources.CHEVRON_LEFT);
        addButton(this.offlineName = new TextInputField(-1, x, y, w, h, "Username"));
        y += h + 4;
        MenuButton offlineAccountButton;
        addButton(offlineAccountButton = new MenuButton(-1, x, y, w, h, "Add offline account"));
        y += h + 8;
        addButton(new Label(this.pane.x + this.pane.width / 2, y + h / 2, ChatColor.translate("&o- OR -"), this.opts.neutralTextColor.getRGB()));
        y += h + 8;
        addButton(this.microsoftButton = new MenuResourceButton(-1, x, y, w, h, "Sign-in with Microsoft", Resources.MICROSOFT, h - 6));
        offlineAccountButton.onClick = (() -> {
            if (!offlineName.getText().isEmpty()) {
                AltManager.getInstance().addAccount(new AccountData(null,offlineName.getText(), UUID.nameUUIDFromBytes(("Offline:" + offlineName.getText()).getBytes()).toString()));
            }
            shouldExit = true;
        });
        this.microsoftButton.onClick = (() -> {
            this.microsoftButton.displayText = "Sign-in window opened";
            this.microsoftButton.setEnabled(false);
             this.thread =  new Thread(() -> {
                try {
                    httpServer = HttpServer.create(new InetSocketAddress(59125), 0);
                    httpServer.createContext("/", exchange -> {
                        try {
                            exchange.getResponseHeaders().add("Location", "http://localhost:59125/end");
                            exchange.sendResponseHeaders(302, -1L);
                            new Thread(() -> authenticate(exchange.getRequestURI().getQuery()), "MS auth thread").start();
                        } catch (Exception ex) {
                            Reference.LOGGER.error("Unable to process auth request", ex);
                        }
                    });
                    httpServer.createContext("/end", exchange -> {
                        try {
                            byte[] b = pageContent.getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=UTF-8"));
                            exchange.sendResponseHeaders(200, b.length);
                            OutputStream outputStream = exchange.getResponseBody();
                            outputStream.write(b);
                            outputStream.flush();
                            outputStream.close();
                            if(httpServer != null){
                                httpServer.stop(0);
                            }
                        } catch (Exception ex) {
                            Reference.LOGGER.warn("Unable to process end request", ex);
                            if (httpServer != null) {
                                httpServer.stop(0);
                            }
                        }
                    });
                    httpServer.start();
                    Sys.openURL("https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account");
                } catch (IOException ex) {
                    Reference.LOGGER.error("Unable to start MS auth server", ex);
                    if (httpServer != null) {
                        httpServer.stop(0);
                    }
                }
            }, "MS Auth server Thread");
            this.thread.start();
        });
        Keyboard.enableRepeatEvents(true);
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 20.0D, this.opts.backgroundColor
                .getRGB());
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 20.0D, this.opts.backgroundColor1
                .getRGB());
        int logoSize = (int) (this.pane.width / 2.0F * 0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawCustomSizedResource(Resources.LOGO, (this.pane.x + this.pane.width / 2.0F - logoSize / 2.0F), (this.pane.y + 15), logoSize, logoSize);
        Fonts.NUNITO_REGULAR_20.drawCenteredString("Login to Minecraft", this.pane.x + this.pane.width / 2.0F, (this.pane.y + 25 + logoSize), Color.WHITE
                .getRGB());
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        if (this.parent != null)
            this.returnButton.drawButton(mouseX, mouseY, this.returnButton.isHovered(mouseX, mouseY));
        if (this.shouldExit)
            this.mc.displayGuiScreen(AltManager.isLoggedIn() ? this.parent : null);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.returnButton != null && AltManager.isLoggedIn()) {
            float scale = getScaledScreen();
            mouseX = (int) (mouseX / scale);
            mouseY = (int) (mouseY / scale);
            if (this.returnButton.isHovered(mouseX, mouseY))
                this.mc.displayGuiScreen(this.parent);
        }
    }

    public void keyTyped(char charTyped, int keyCode) {
        if (keyCode == 1 && !AltManager.isLoggedIn())
            this.parent = null;
        super.keyTyped(charTyped, keyCode);
    }


    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
        if (this.httpServer != null) {
            this.httpServer.stop(0);
        }
    }

    private void authenticate(String query) {
        try {
            if (query == null)
                throw new NullPointerException("Query is null");
            if (query.contains("error=access_denied"))
                throw new AuthenticationException("Access denied by user");
            if (!query.startsWith("code="))
                throw new IllegalStateException("query=" + query);
            MicrosoftAuthManager.login(query.replace("code=", ""));
            if (AltManager.isLoggedIn()) {
                microsoftButton.setEnabled(true);
                microsoftButton.displayText = "Sign-in with Microsoft";
                shouldExit = true;
            }
        } catch (Exception ex) {
            Reference.LOGGER.error("Login failed!", ex);
        }
    }
}