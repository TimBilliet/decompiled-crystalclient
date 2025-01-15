package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Reference;
import co.crystaldev.client.Resources;
import co.crystaldev.client.account.AltManager;
import co.crystaldev.client.account.MojangAuthManager;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.ScreenPanorama;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.*;
import co.crystaldev.client.gui.screens.override.ScreenMainMenu;
import co.crystaldev.client.util.Reflector;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Scanner;

public class ScreenLogin extends ScreenPanorama {
    private TextInputField usernameInput;

    private TextInputField passwordInput;

    private MenuButton signInButton;

    private MenuButton microsoftButton;

    private ResourceButton returnButton;

    private boolean shouldExit = false;

    private String errorMessage = "";

    private long erroredAt = System.currentTimeMillis() - 2000L;

    private Scanner msmcIn = null;

    private Thread thread = null;

    public ScreenLogin(GuiScreen parent) {
        super(parent, Resources.PANORAMA_IMAGES, 256);
    }

    public void init() {
        super.init();
        int pw = 200;
        int ph = 212;
        float scale = getScaledScreen();
        this.pane = new Pane((this.width / scale) / 2.0D - pw / 2.0D, (this.height / scale) / 2.0D - ph / 2.0D, pw, ph);
        int w = (int) (this.pane.width * 0.9D);
        int h = 18;
        int x = this.pane.x + this.pane.width / 2 - w / 2;
        int y = this.pane.y + (int) (this.pane.width / 2.0F * 0.5F) + 40;
        this.returnButton = new ResourceButton(-1, x, this.pane.y + this.pane.width / 2 - w / 2, 18, 18, Resources.CHEVRON_LEFT);
        addButton((Button) (this.usernameInput = new TextInputField(-1, x, y, w, h, "Username")));
        y += h + 4;
        addButton((Button) (this.passwordInput = new TextInputField(-1, x, y, w, h, "Password", true)));
        y += h + 4;
        addButton((Button) (this.signInButton = new MenuButton(-1, x, y, w, h, "Sign In")));
        y += h + 8;
        addButton((Button) new Label(this.pane.x + this.pane.width / 2, y + h / 2, ChatColor.translate("&o- OR -"), this.opts.neutralTextColor.getRGB()));
        y += h + 8;
        addButton((Button) (this.microsoftButton = (MenuButton) new MenuResourceButton(-1, x, y, w, h, "Sign-in with Microsoft", Resources.MICROSOFT, h - 6)));
        this.signInButton.onClick = (() -> {
            if (!this.usernameInput.getText().contains("@")) {
                this.errorMessage = "Please enter a valid e-mail address";
                this.erroredAt = System.currentTimeMillis();
                return;
            }
            if (this.passwordInput.getText().length() < 3) {
                this.errorMessage = "Your password should be longer than 3.";
                this.erroredAt = System.currentTimeMillis();
                return;
            }
            try {
                MojangAuthManager.loginNoCatch(this.usernameInput.getText(), this.passwordInput.getText());
                this.mc.displayGuiScreen(new ScreenMainMenu());
            } catch (AuthenticationException ex) {
                this.errorMessage = ex.getMessage();
                this.erroredAt = System.currentTimeMillis();
            }
        });
        this.microsoftButton.onClick = (() -> {
//            if (Reflector.isOptiFineLoaded()) {
//                System.out.println("launcherProtocol.openMicrosoftWindow()");
//                this.microsoftButton.displayText = "Sign-in window opened";
//                this.microsoftButton.setEnabled(false);
//                this.msmcIn = new Scanner(System.in);
//                this.thread = new Thread(() -> {
//                    while (true) {
//                        if (this.msmcIn.hasNextLine()) {
//                            String line = this.msmcIn.nextLine();
//                            try {
//                                JsonObject jsonObject = Reference.GSON.fromJson(line, JsonObject.class);
//                                if(jsonObject.has("final")){
//                                    String var3 = jsonObject.get("status").getAsString();
//                                    int var4 = -1;
//                                    switch(var3.hashCode()) {
//                                        case -1867169789:
//                                            if (var3.equals("success")) {
//                                                var4 = 2;
//                                            }
//                                            break;
//                                        case -838846263:
//                                            if (var3.equals("update")) {
//                                                var4 = 0;
//                                            }
//                                            break;
//                                        case 96784904:
//                                            if (var3.equals("error")) {
//                                                var4 = 1;
//                                            }
//                                    }
//                                    switch (var4) {
//                                        case 0:
////                                            this.microsoftButton. = var2.get("message").getAsString();
////                                            this.microsoftButton.
//                                            break;
//                                        case 1:
//                                            this.errorMessage = jsonObject.get("message").getAsString();
//                                            this.erroredAt = System.currentTimeMillis();
//                                            break;
//                                    }
//                                }
//                            } catch (JsonParseException ex) {
//                                Reference.LOGGER.error("Unable to parse JSON from '{}'", line, ex);
//                            }
//                        }
//                    }
//
//                });
//                this.thread.setDaemon(true);
//                this.thread.start();
//            } else {
//
//            }
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
        if (System.currentTimeMillis() < this.erroredAt + 3000L) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Fonts.NUNITO_REGULAR_12.drawString(this.errorMessage, this.pane.x + this.pane.width - 5 - Fonts.NUNITO_REGULAR_12.getStringWidth(this.errorMessage), this.pane.y + 5, this.opts.secondaryRed
                    .getRGB());
        }
        if (this.msmcIn == null && this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        if (this.parent != null && AltManager.isLoggedIn())
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

    public void onMicrosoftExit() {
        this.shouldExit = true;
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenLogin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */