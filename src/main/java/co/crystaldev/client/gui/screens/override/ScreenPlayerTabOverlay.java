package co.crystaldev.client.gui.screens.override;

import co.crystaldev.client.Resources;
import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.duck.NetworkPlayerInfoExt;
import co.crystaldev.client.feature.impl.all.TabEditor;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.IconColor;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

import java.util.*;

public class ScreenPlayerTabOverlay extends GuiPlayerTabOverlay {
    private static final Ordering<NetworkPlayerInfo> comparator = Ordering.from(new PlayerComparator());

    private final Minecraft mc;

    private final GuiIngame guiIngame;

    private IChatComponent footer;

    private IChatComponent header;

    private long lastTimeOpened;

    private boolean isBeingRendered;
    private List<NetworkPlayerInfo> list;

    public ScreenPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
        super(mcIn, guiIngameIn);
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
        list = new ArrayList<>();
    }

    public void updatePlayerList(boolean willBeRendered) {
        if (willBeRendered && !this.isBeingRendered)
            this.lastTimeOpened = Minecraft.getSystemTime();
        this.isBeingRendered = willBeRendered;
    }

    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        int l;
        NetHandlerPlayClient nethandlerplayclient = this.mc.thePlayer.sendQueue;
        try {
            list = comparator.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        } catch (IllegalArgumentException ex) {
            list = new ArrayList<>(nethandlerplayclient.getPlayerInfoMap());
        }
        int i = 0;
        int j = 0;
        for (NetworkPlayerInfo networkplayerinfo : list) {
            boolean isOnCrystal = ((NetworkPlayerInfoExt) networkplayerinfo).isOnCrystalClient();
            int k = this.mc.fontRendererObj.getStringWidth(getPlayerName(networkplayerinfo));
            i = Math.max(i, k + (isOnCrystal ? 10 : 0));
            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                k = this.mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                j = Math.max(j, k + (isOnCrystal ? 10 : 0));
            }
        }
        List<NetworkPlayerInfo> list = this.list.subList(0, Math.min(this.list.size(), 80));
        int l3 = list.size();
        int i4 = l3;
        int j4;
        for (j4 = 1; i4 > 20; i4 = (l3 + j4 - 1) / j4)
            j4++;
        boolean flag = (this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted());
        if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                l = 90;
            } else {
                l = j;
            }
        } else {
            l = 0;
        }
        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i + l + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;
        if (this.header != null) {
            list1 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);
            for (String s : list1)
                l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s));
        }
        if (this.footer != null) {
            list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
            for (String s2 : list2)
                l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s2));
        }
        if (list1 != null) {
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * this.mc.fontRendererObj.FONT_HEIGHT, -2147483648);
            for (String s3 : list1) {
                int i2 = this.mc.fontRendererObj.getStringWidth(s3);
                this.mc.fontRendererObj.drawStringWithShadow(s3, ((float) width / 2 - (float) i2 / 2), k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
            k1++;
        }
        drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, -2147483648);
        for (int k4 = 0; k4 < l3; k4++) {
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            NetworkPlayerInfo networkplayerinfo1 = null;
            if (k4 < list.size()) {
                networkplayerinfo1 = list.get(k4);
                UUID id = networkplayerinfo1.getGameProfile().getId();
                TabEditor editor = TabEditor.getInstance();
                boolean isSelf = Objects.equals(id, this.mc.thePlayer.getUniqueID());
                boolean isInGroup = (GroupManager.getSelectedGroup() != null && GroupManager.getSelectedGroup().getMember(id) != null);
                int color = (isSelf && editor.enabled && editor.highlightSelf) ? editor.selfHighlightColor.getRGB() : ((isInGroup && editor.enabled && editor.highlightGroupMembers) ? editor.groupHighlightColor.getRGB() : 553648127);
                if ((isSelf && editor.selfHighlightColor.isChroma()) || (isInGroup && editor.groupHighlightColor.isChroma()))
                    ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
                drawRect(j2, k2, j2 + i1, k2 + 8, color);
                ShaderManager.getInstance().disableShader();
            } else {
                drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (k4 < list.size()) {
                if (networkplayerinfo1 == null)
                    networkplayerinfo1 = list.get(k4);
                String s1 = getPlayerName(networkplayerinfo1);
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();
                boolean isOnCrystal = ((NetworkPlayerInfoExt) networkplayerinfo1).isOnCrystalClient();
                if (flag) {
                    EntityPlayer entityplayer = this.mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    this.mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int l2 = 8;
                    int i3 = 8;
                    Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, l2, 8, i3, 8, 8, 64.0F, 64.0F);
                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8;
                        int k3 = 8;
                        Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }
                    j2 += 9;
                }
                if (isOnCrystal) {
                    CosmeticPlayer cp = CosmeticCache.getInstance().fromId(gameprofile.getId());
                    Color color = (cp == null) ? null : (Color) cp.getColor();
                    if (cp != null && color != null) {
                        IconColor iconColor = color.getIconColor();
                        if (iconColor == IconColor.CHROMA)
                            ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
                        RenderUtils.setGlColor(iconColor.getColor(), 255);
                    } else {
                        GlStateManager.resetColor();
                    }
                    this.mc.getTextureManager().bindTexture(Resources.LOGO_WHITE);
                    Gui.drawScaledCustomSizeModalRect(j2, k2 - 1, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
                    ShaderManager.getInstance().disableShader();
                    GlStateManager.resetColor();
                    j2 += 11;
                }
                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    this.mc.fontRendererObj.drawStringWithShadow(s1, j2, k2, -1862270977);
                } else {
                    this.mc.fontRendererObj.drawStringWithShadow(s1, j2, k2, -1);
                }
                if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;
                    if (l5 - k5 > 5)
                        drawScoreboardValues(scoreObjectiveIn, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
                }
                j2 -= isOnCrystal ? 11 : 0;
                drawPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
        }
        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, -2147483648);
            for (String s4 : list2) {
                int j5 = this.mc.fontRendererObj.getStringWidth(s4);
                this.mc.fontRendererObj.drawStringWithShadow(s4, ((float) width / 2 - (float) j5 / 2), k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    protected void drawPing(int offset, int xPosition, int yPosition, NetworkPlayerInfo info) {
        if ((TabEditor.getInstance()).enabled && (TabEditor.getInstance()).showPingAsNumber) {
            FontRenderer fr = Fonts.PT_SANS_BOLD_12;
            int ping = info.getResponseTime();
            int x = xPosition + offset - fr.getStringWidth(String.valueOf(ping)) - 2;
            int y = yPosition + this.mc.fontRendererObj.FONT_HEIGHT / 2 - fr.getStringHeight() / 2;
            int color = (ping > 500) ? 6160384 : ((ping > 300) ? 16399934 : ((ping > 200) ? 16419902 : ((ping > 135) ? 16429630 : ((ping > 70) ? 16446526 : ((ping >= 0) ? 4127294 : 16777215)))));
            fr.drawString((ping == 0) ? "?" : String.valueOf(ping), x, y, color);
            GlStateManager.resetColor();
        } else {
            super.drawPing(offset, xPosition, yPosition, info);
        }
    }

    private void drawScoreboardValues(ScoreObjective objective, int p_175247_2_, String name, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo info) {
        int i = objective.getScoreboard().getValueFromObjective(name, objective).getScorePoints();
        if (objective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            this.mc.getTextureManager().bindTexture(icons);
            if (this.lastTimeOpened == info.func_178855_p())
                if (i < info.func_178835_l()) {
                    info.func_178846_a(Minecraft.getSystemTime());
                    info.func_178844_b((this.guiIngame.getUpdateCounter() + 20));
                } else if (i > info.func_178835_l()) {
                    info.func_178846_a(Minecraft.getSystemTime());
                    info.func_178844_b((this.guiIngame.getUpdateCounter() + 10));
                }
            if (Minecraft.getSystemTime() - info.func_178847_n() > 1000L || this.lastTimeOpened != info.func_178855_p()) {
                info.func_178836_b(i);
                info.func_178857_c(i);
                info.func_178846_a(Minecraft.getSystemTime());
            }
            info.func_178843_c(this.lastTimeOpened);
            info.func_178836_b(i);
            int j = MathHelper.ceiling_float_int(Math.max(i, info.func_178860_m()) / 2.0F);
            int k = Math.max(MathHelper.ceiling_float_int(((float) i / 2)), Math.max(MathHelper.ceiling_float_int(((float) info.func_178860_m() / 2)), 10));
            boolean flag = (info.func_178858_o() > this.guiIngame.getUpdateCounter() && (info.func_178858_o() - this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L);
            if (j > 0) {
                float f = Math.min(((float) p_175247_5_ - p_175247_4_ - 4) / k, 9.0F);
                if (f > 3.0F) {
                    for (int l = j; l < k; l++)
                        drawTexturedModalRect(p_175247_4_ + l * f, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                    for (int j1 = 0; j1 < j; j1++) {
                        drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        if (flag) {
                            if (j1 * 2 + 1 < info.func_178860_m())
                                drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, 70, 0, 9, 9);
                            if (j1 * 2 + 1 == info.func_178860_m())
                                drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, 79, 0, 9, 9);
                        }
                        if (j1 * 2 + 1 < i)
                            drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, (j1 >= 10) ? 160 : 52, 0, 9, 9);
                        if (j1 * 2 + 1 == i)
                            drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, (j1 >= 10) ? 169 : 61, 0, 9, 9);
                    }
                } else {
                    float f1 = MathHelper.clamp_float(i / 20.0F, 0.0F, 1.0F);
                    int i1 = (int) ((1.0F - f1) * 255.0F) << 16 | (int) (f1 * 255.0F) << 8;
                    String s = "" + (i / 2.0F);
                    if (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(s + "hp") >= p_175247_4_)
                        s = s + "hp";
                    this.mc.fontRendererObj.drawStringWithShadow(s, (((float) p_175247_5_ + p_175247_4_) / 2 - (float) this.mc.fontRendererObj.getStringWidth(s) / 2), p_175247_2_, i1);
                }
            }
        } else {
            String s1 = EnumChatFormatting.YELLOW + "" + i;
            this.mc.fontRendererObj.drawStringWithShadow(s1, (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(s1)), p_175247_2_, 16777215);
        }
    }

    public void setFooter(IChatComponent footerIn) {
        this.footer = footerIn;
    }

    public void setHeader(IChatComponent headerIn) {
        this.header = headerIn;
    }

    public void resetFooterHeader() {
        this.header = null;
        this.footer = null;
    }

    private static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        public int compare(NetworkPlayerInfo p1, NetworkPlayerInfo p2) {
            ScorePlayerTeam tm1 = p1.getPlayerTeam();
            ScorePlayerTeam tm2 = p2.getPlayerTeam();
            if ((TabEditor.getInstance()).sortByCrystalPlayers)
                return ComparisonChain.start()
                        .compareTrueFirst((p1.getGameType() != WorldSettings.GameType.SPECTATOR), (p2.getGameType() != WorldSettings.GameType.SPECTATOR))
                        .compare(((NetworkPlayerInfoExt) p2).isOnCrystalClient(), ((NetworkPlayerInfoExt) p1).isOnCrystalClient())
                        .compare((tm1 != null) ? tm1.getRegisteredName() : "", (tm2 != null) ? tm2.getRegisteredName() : "")
                        .compare(p1.getGameProfile().getName(), p2.getGameProfile().getName())
                        .result();
            return ComparisonChain.start()
                    .compareTrueFirst((p1.getGameType() != WorldSettings.GameType.SPECTATOR), (p2.getGameType() != WorldSettings.GameType.SPECTATOR))
                    .compare((tm1 != null) ? tm1.getRegisteredName() : "", (tm2 != null) ? tm2.getRegisteredName() : "")
                    .compare(p1.getGameProfile().getName(), p2.getGameProfile().getName())
                    .result();
        }
    }
}

