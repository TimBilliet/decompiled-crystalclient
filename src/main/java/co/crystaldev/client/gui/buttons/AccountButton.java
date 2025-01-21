package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.account.AccountData;
import co.crystaldev.client.account.AltManager;
import co.crystaldev.client.account.AuthManager;
import co.crystaldev.client.cache.SkinCache;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenLogin;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountButton extends Button {
    private final Screen parent;

    private final FadingColor fadingColor;

    private final FadingColor textColor;

    private boolean loginScreen = false;

    private boolean expanded = false;

    private int expandedSize = 20;

    private final List<Button> buttons;

    public AccountButton(int id, int x, int y, Screen parent) {
        super(id, x, y, 0, 18);
        this.fontRenderer = Fonts.NUNITO_REGULAR_20;
        this.parent = parent;
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.buttons = new ArrayList<>();
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        if (this.loginScreen) {
            this.mc.displayGuiScreen(new ScreenLogin(null));
            return;
        }
        List<AccountData> accounts = AltManager.getAccounts();
        if (accounts != null && this.width == 0) {
            int y = this.y + this.height + 2;
            if (accounts.isEmpty() || !AltManager.isLoggedIn())
                this.width = 45 + this.fontRenderer.getStringWidth("No Account Selected");
            for (AccountData accountData : accounts) {
                if (accountData == null)
                    continue;
                this.width = Math.max(Math.max(60 + this.fontRenderer.getStringWidth(accountData.getName()), this.width), 45 + fontRenderer.getStringWidth("Add account..."));
                this.buttons.add(new AccountInfoButton(accountData, this.x + 2, y));
                y += 20;
                this.expandedSize += 20;
            }
            this.buttons.add(new MenuResourceButton(-1, this.x + 2, y, this.width - 4, 18, "Add account...", Resources.ADD_BOX, (int) (this.height * 0.8D)) {
                {
                    setFontRenderer(fontRenderer);
                    setFadingColor(new FadingColor(opts.getColor(opts.neutralButtonBackground, 0), opts.hoveredButtonBackground));
                }
            });
            this.expandedSize += 2;
        } else if (this.width == 0) {
            return;
        }
        hovered = (hovered || this.expanded);
        this.fadingColor.fade(hovered);
        this.textColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height + (this.expanded ? this.expandedSize : 0)), 9.0D, this.fadingColor
                .getCurrentColor().getRGB());
        AccountData data = AltManager.getCurrentAccount();
        this.fontRenderer.drawString((data == null) ? "No Account Selected" : data.getName(), this.x + this.height + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                .getCurrentColor().getRGB());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (data != null) {
            if (data.isOffline()) {
                RenderUtils.drawCustomSizedResource(Resources.OFFLINE, this.x + 4, this.y + 2, this.height - 4, this.height - 4);
            } else {
                RenderUtils.drawCustomSizedResource(SkinCache.getInstance().getCachedSkin(data.getId()).getResourceLocation(), this.x + 4, this.y + 2, this.height - 4, this.height - 4);
            }
        }
        if (this.expanded) {
            for (Button button : this.buttons)
                button.drawButton(mouseX, mouseY, button.isHovered(mouseX, mouseY));
            RenderUtils.setGlColor(this.textColor.getCurrentColor());
            RenderUtils.drawCustomSizedResource(Resources.CHEVRON_UP, this.x + this.width - 12, this.y + this.height / 2 - 6, 10, 10);
        } else {
            RenderUtils.setGlColor(this.textColor.getCurrentColor());
            RenderUtils.drawCustomSizedResource(Resources.CHEVRON_DOWN, this.x + this.width - 12, this.y + this.height / 2 - 5, 10, 10);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        this.expanded = !this.expanded;
    }

    public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
        if (this.expanded)
            for (Button button : this.buttons) {
                if (!button.isHovered(mouseX, mouseY))
                    continue;
                if (button instanceof AccountInfoButton) {
                    AccountInfoButton b = (AccountInfoButton) button;
                    new Thread(() -> {
                        if (b.removeAccountButton.isHovered(mouseX, mouseY)) {
                            AltManager.getInstance().removeAccount(b.data);
                            AltManager.getInstance().saveAltManager();
                            width = 0;
                            expanded = false;
                            buttons.clear();
                            expandedSize = 20;
                        } else {
                            boolean res = false;
                            try {
                                if (b.data.isOffline()) {
                                    AltManager.getInstance().addAccount(new AccountData(null, b.data.getName(), b.data.getUnformattedId()));
                                    res = true;
                                } else {
                                    res = AuthManager.login(b.data);
                                }
                            } catch (IOException ignored) {
                            }
                            if (!res) {
                                this.loginScreen = true;
                                AltManager.getInstance().removeAccount(b.data);
                                AltManager.getInstance().saveAltManager();
                            }
                        }
                    }).start();
                    continue;
                }
                if (button instanceof MenuResourceButton) {
                    this.expanded = false;
                    this.mc.displayGuiScreen(new ScreenLogin(this.parent));
                }
            }
    }

    private class AccountInfoButton extends Button {
        private final AccountData data;

        private final FadingColor fadingColor;

        private final FadingColor textColor;

        private final ResourceButton removeAccountButton;

        public AccountInfoButton(AccountData data, int x, int y) {
            super(-1, x, y, 0, 18);
            this.data = data;
            this.fadingColor = new FadingColor(this.opts.getColor(this.opts.neutralButtonBackground, 0), this.opts.hoveredButtonBackground);
            this.textColor = new FadingColor(this.opts.getColor(this.opts.neutralTextColor, 150), this.opts.getColor(this.opts.hoveredTextColor, 210));
            this.fontRenderer = Fonts.NUNITO_REGULAR_20;
            this.removeAccountButton = new ResourceButton(-1, this.x + AccountButton.this.width - 4 - (this.height - 2), y + 2, this.height - 4, this.height - 4, Resources.CLOSE) {
                {
                    setFadingColor(new FadingColor(opts.getColor(opts.mainRed, 100), opts.getColor(opts.mainRed, 180)));
                    setIconColor(new FadingColor(opts.neutralTextColor, opts.hoveredTextColor));
                    setRadius(6);
                }
            };
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            boolean selected = (AltManager.getCurrentAccount() != null && this.data != null && AltManager.getCurrentAccount().getId().equals(this.data.getId()));
            this.fadingColor.fade(hovered);
            this.textColor.fade((hovered || selected));
            RenderUtils.drawRoundedRect(this.x, this.y, (this.x + AccountButton.this.width - 4), (this.y + this.height), 9.0D, this.fadingColor
                    .getCurrentColor().getRGB());
            this.fontRenderer.drawString(this.data.getName(), this.x + this.height + 2, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                    .getCurrentColor().getRGB());
            if (data.isOffline()) {
                RenderUtils.drawCustomSizedResource(Resources.OFFLINE, this.x + 2, this.y + 2, this.height - 4, this.height - 4);
            } else {
                RenderUtils.drawCustomSizedResource(SkinCache.getInstance().getCachedSkin(this.data.getId()).getResourceLocation(), this.x + 2, this.y + 2, this.height - 4, this.height - 4);
            }
            if (hovered)
                this.removeAccountButton.drawButton(mouseX, mouseY, removeAccountButton.isHovered(mouseX, mouseY));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        public boolean isHovered(int mouseX, int mouseY) {
            this.width = AccountButton.this.width - 4;
            removeAccountButton.x = this.x + this.width - (this.height - 2);
            return super.isHovered(mouseX, mouseY);
        }
    }
}
