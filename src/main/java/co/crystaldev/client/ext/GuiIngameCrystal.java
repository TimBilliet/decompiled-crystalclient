package co.crystaldev.client.ext;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.Event;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.gui.MixinGuiIngame;
import co.crystaldev.client.mixin.accessor.net.minecraftforge.client.MixinGuiIngameForge;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public class GuiIngameCrystal extends GuiIngameForge {
    private ScaledResolution res = null;

    private FontRenderer fontrenderer = null;

    private GuiOverlayDebugCrystal debugOverlay;

    public GuiIngameCrystal(Minecraft mc) {
        super(mc);
        this.debugOverlay = new GuiOverlayDebugCrystal(mc);
    }

    private boolean shouldShowCrosshair() {
        if (this.mc.currentScreen != null || this.mc.gameSettings.thirdPersonView != 0)
            return false;
        boolean visible = ((MixinGuiIngame) this).callShowCrosshair();
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        if (Client.getTimer() != null) {
            Event event = (new RenderOverlayEvent.Crosshair((Client.getTimer()).renderPartialTicks, visible)).call();
            GlStateManager.popMatrix();
            if (event.isCancelled())
                return false;
        }

        return visible;
    }

    public void renderGameOverlay(float partialTicks) {
        this.res = new ScaledResolution(this.mc);
        ((MixinGuiIngameForge) this).setEventParent(new RenderGameOverlayEvent(partialTicks, this.res));
        int width = this.res.getScaledWidth();
        int height = this.res.getScaledHeight();
        renderHealthMount = this.mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = (this.mc.thePlayer.ridingEntity == null);
        renderJumpBar = this.mc.thePlayer.isRidingHorse();
        right_height = 39;
        left_height = 39;
        if (!pre(RenderGameOverlayEvent.ElementType.ALL)) {
            this.fontrenderer = this.mc.fontRendererObj;
            this.mc.entityRenderer.setupOverlayRendering();
            GlStateManager.enableBlend();
            if (Minecraft.isFancyGraphicsEnabled()) {
                renderVignette(this.mc.thePlayer.getBrightness(partialTicks), this.res);
            } else {
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            }
            if (renderHelmet) {
                renderHelmet(this.res, partialTicks);
            }
            if (renderPortal && !this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                renderPortal(this.res, partialTicks);
            }
            if (renderHotbar) {
                renderTooltip(this.res, partialTicks);

            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.zLevel = -90.0F;
            this.rand.setSeed(this.updateCounter * 312871L);
            if (shouldShowCrosshair())
                renderCrosshairs(width, height);
            if (renderBossHealth)
                renderBossHealth();
            if (this.mc.playerController.shouldDrawHUD() && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
                if (renderHealth)
                    renderHealth(width, height);
                if (renderArmor)
                    renderArmor(width, height);
                if (renderFood)
                    renderFood(width, height);
                if (renderHealthMount)
                    renderHealthMount(width, height);
                if (renderAir)
                    renderAir(width, height);
            }
            renderSleepFade(width, height);
            if (renderJumpBar) {
                renderJumpBar(width, height);
            } else if (renderExperiance) {
                renderExperience(width, height);
            }
            if (!this.mc.gameSettings.showDebugInfo) {
                GlStateManager.pushMatrix();
                (new RenderOverlayEvent.All(partialTicks, this.res)).call();
                GlStateManager.popMatrix();
            }
            renderToolHightlight(this.res);
            renderHUDText(width, height);
            renderRecordOverlay(width, height, partialTicks);
            renderTitle(width, height, partialTicks);
            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            ScoreObjective objective = null;
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getCommandSenderEntity().getName());

            if (scoreplayerteam != null) {
                int slot = scoreplayerteam.getChatFormat().getColorIndex();
                if (slot >= 0)
                    objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
            }
            ScoreObjective scoreobjective1 = (objective != null) ? objective : scoreboard.getObjectiveInDisplaySlot(1);
            if (renderObjective && scoreobjective1 != null)
                renderScoreboard(scoreobjective1, this.res);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableAlpha();
            renderChat(width, height);
            renderPlayerList(width, height);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            post(RenderGameOverlayEvent.ElementType.ALL);
        }
    }

    public ScaledResolution getResolution() {
        return this.res;
    }

    protected void renderCrosshairs(int width, int height) {
        if (!pre(RenderGameOverlayEvent.ElementType.CROSSHAIRS)) {
            if (this.showCrosshair()) {
                bind(Gui.icons);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
                GlStateManager.enableAlpha();
                drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableBlend();
            }
            post(RenderGameOverlayEvent.ElementType.CROSSHAIRS);
        }
    }

    private void renderHelmet(ScaledResolution res, float partialTicks) {
        if (!pre(RenderGameOverlayEvent.ElementType.HELMET)) {
            ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
            if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() != null)
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
                    renderPumpkinOverlay(res);
                } else {
                    itemstack.getItem().renderHelmetOverlay(itemstack, this.mc.thePlayer, res, partialTicks);
                }
            post(RenderGameOverlayEvent.ElementType.HELMET);
        }
    }

    protected void renderArmor(int width, int height) {
        if (!pre(RenderGameOverlayEvent.ElementType.ARMOR)) {
            this.mc.mcProfiler.startSection("armor");
            GlStateManager.enableBlend();
            int left = width / 2 - 91;
            int top = height - left_height;
            int level = ForgeHooks.getTotalArmorValue(this.mc.thePlayer);
            for (int i = 1; level > 0 && i < 20; i += 2) {
                if (i < level) {
                    drawTexturedModalRect(left, top, 34, 9, 9, 9);
                } else if (i == level) {
                    drawTexturedModalRect(left, top, 25, 9, 9, 9);
                } else if (i > level) {
                    drawTexturedModalRect(left, top, 16, 9, 9, 9);
                }
                left += 8;
            }
            left_height += 10;
            GlStateManager.disableBlend();
            this.mc.mcProfiler.endSection();
            post(RenderGameOverlayEvent.ElementType.ARMOR);
        }
    }

    protected void renderPortal(ScaledResolution res, float partialTicks) {
        if (!pre(RenderGameOverlayEvent.ElementType.PORTAL)) {
            float f1 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
            if (f1 > 0.0F)
                renderPortal(f1, res);
            post(RenderGameOverlayEvent.ElementType.PORTAL);
        }
    }

    protected void renderAir(int width, int height) {
        if (!pre(RenderGameOverlayEvent.ElementType.AIR)) {
            this.mc.mcProfiler.startSection("air");
            EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
            GlStateManager.enableBlend();
            int left = width / 2 + 91;
            int top = height - right_height;
            if (player.isInsideOfMaterial(Material.water)) {
                int air = player.getAir();
                int full = MathHelper.ceiling_double_int((air - 2) * 10.0D / 300.0D);
                int partial = MathHelper.ceiling_double_int(air * 10.0D / 300.0D) - full;
                for (int i = 0; i < full + partial; i++)
                    drawTexturedModalRect(left - i * 8 - 9, top, (i < full) ? 16 : 25, 18, 9, 9);
                right_height += 10;
            }
            GlStateManager.disableBlend();
            this.mc.mcProfiler.endSection();
            post(RenderGameOverlayEvent.ElementType.AIR);
        }
    }

    public void renderHealth(int width, int height) {
        bind(icons);
        if (!pre(RenderGameOverlayEvent.ElementType.HEALTH)) {
            this.mc.mcProfiler.startSection("health");
            GlStateManager.enableBlend();
            EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
            int health = MathHelper.ceiling_float_int(player.getHealth());
            boolean highlight = (this.healthUpdateCounter > this.updateCounter && (this.healthUpdateCounter - this.updateCounter) / 3L % 2L == 1L);
            if (health < this.playerHealth && player.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = (this.updateCounter + 20);
            } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = (this.updateCounter + 10);
            }
            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = health;
                this.lastPlayerHealth = health;
                this.lastSystemTime = Minecraft.getSystemTime();
            }
            this.playerHealth = health;
            int healthLast = this.lastPlayerHealth;
            IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            float healthMax = (float) attrMaxHealth.getAttributeValue();
            float absorb = player.getAbsorptionAmount();
            int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - healthRows - 2, 3);
            this.rand.setSeed((this.updateCounter * 312871L));
            int left = width / 2 - 91;
            int top = height - left_height;
            left_height += healthRows * rowHeight;
            if (rowHeight != 10)
                left_height += 10 - rowHeight;
            int regen = -1;
            if (player.isPotionActive(Potion.regeneration))
                regen = this.updateCounter % 25;
            int TOP = 9 * (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
            int BACKGROUND = highlight ? 25 : 16;
            int MARGIN = 16;
            if (player.isPotionActive(Potion.poison)) {
                MARGIN += 36;
            } else if (player.isPotionActive(Potion.wither)) {
                MARGIN += 72;
            }
            float absorbRemaining = absorb;
            for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; i--) {
                int row = MathHelper.ceiling_float_int((i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4)
                    y += this.rand.nextInt(2);
                if (i == regen)
                    y -= 2;
                drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
                if (highlight)
                    if (i * 2 + 1 < healthLast) {
                        drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9);
                    } else if (i * 2 + 1 == healthLast) {
                        drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
                    }
                if (absorbRemaining <= 0.0F) {
                    if (i * 2 + 1 < health) {
                        drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
                    } else if (i * 2 + 1 == health) {
                        drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
                    }
                } else {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
                    } else {
                        drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
                    }
                    absorbRemaining -= 2.0F;
                }
            }
            GlStateManager.disableBlend();
            this.mc.mcProfiler.endSection();
            post(RenderGameOverlayEvent.ElementType.HEALTH);
        }
    }

    public void renderFood(int width, int height) {
        if (!pre(RenderGameOverlayEvent.ElementType.FOOD)) {
            this.mc.mcProfiler.startSection("food");
            EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
            GlStateManager.enableBlend();
            int left = width / 2 + 91;
            int top = height - right_height;
            right_height += 10;
            boolean unused = false;
            FoodStats stats = this.mc.thePlayer.getFoodStats();
            int level = stats.getFoodLevel();
            int levelLast = stats.getPrevFoodLevel();
            for (int i = 0; i < 10; i++) {
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;
                int y = top;
                int icon = 16;
                byte backgound = 0;
                if (this.mc.thePlayer.isPotionActive(Potion.hunger)) {
                    icon += 36;
                    backgound = 13;
                }
                if (unused)
                    backgound = 1;
                if (player.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (level * 3 + 1) == 0)
                    y = top + this.rand.nextInt(3) - 1;
                drawTexturedModalRect(x, y, 16 + backgound * 9, 27, 9, 9);
                if (unused)
                    if (idx < levelLast) {
                        drawTexturedModalRect(x, y, icon + 54, 27, 9, 9);
                    } else if (idx == levelLast) {
                        drawTexturedModalRect(x, y, icon + 63, 27, 9, 9);
                    }
                if (idx < level) {
                    drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                } else if (idx == level) {
                    drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
                }


                GlStateManager.disableBlend();
                this.mc.mcProfiler.endSection();
                post(RenderGameOverlayEvent.ElementType.FOOD);
            }
        }
    }

    protected void renderSleepFade(int width, int height) {
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int sleepTime = this.mc.thePlayer.getSleepTimer();
            float opacity = sleepTime / 100.0F;
            if (opacity > 1.0F) {
                opacity = 1.0F - (sleepTime - 100) / 10.0F;
            }

            int color = (int) (220.0F * opacity) << 24 | 0x101020;
            drawRect(0, 0, width, height, color);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }
    }


    protected void renderExperience(int width, int height) {
        bind(icons);
        if (!pre(RenderGameOverlayEvent.ElementType.EXPERIENCE)) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
                this.mc.mcProfiler.startSection("expBar");
                int cap = this.mc.thePlayer.xpBarCap();
                int left = width / 2 - 91;

                if (cap > 0) {
                    short barWidth = 182;
                    int color = (int) (this.mc.thePlayer.experience * (barWidth + 1));
                    int top = height - 32 + 3;
                    drawTexturedModalRect(left, top, 0, 64, barWidth, 5);
                    if (color > 0) {
                        drawTexturedModalRect(left, top, 0, 69, color, 5);
                    }
                }

                this.mc.mcProfiler.endSection();
                if (this.mc.playerController.gameIsSurvivalOrAdventure() && this.mc.thePlayer.experienceLevel > 0) {
                    this.mc.mcProfiler.startSection("expLevel");
                    boolean flag1 = false;
                    int color = flag1 ? 16777215 : 8453920;
                    String text = "" + this.mc.thePlayer.experienceLevel;
                    int x = (width - this.fontrenderer.getStringWidth(text)) / 2;
                    int y = height - 31 - 4;
                    this.fontrenderer.drawString(text, x + 1, y, 0);
                    this.fontrenderer.drawString(text, x - 1, y, 0);
                    this.fontrenderer.drawString(text, x, y + 1, 0);
                    this.fontrenderer.drawString(text, x, y - 1, 0);
                    this.fontrenderer.drawString(text, x, y, color);
                    this.mc.mcProfiler.endSection();
                }
            }

            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            post(RenderGameOverlayEvent.ElementType.EXPERIENCE);
        }
    }

    protected void renderJumpBar(int width, int height) {
        bind(icons);
        if (!pre(RenderGameOverlayEvent.ElementType.JUMPBAR)) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            this.mc.mcProfiler.startSection("jumpBar");
            float charge = this.mc.thePlayer.getHorseJumpPower();
            int x = width / 2 - 91;
            int filled = (int) (charge * 183.0F);
            int top = height - 32 + 3;
            drawTexturedModalRect(x, top, 0, 84, 182, 5);
            if (filled > 0) {
                drawTexturedModalRect(x, top, 0, 89, filled, 5);
            }

            GlStateManager.enableBlend();
            this.mc.mcProfiler.endSection();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            post(RenderGameOverlayEvent.ElementType.JUMPBAR);
        }
    }

    protected void renderToolHightlight(ScaledResolution res) {
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.mc.mcProfiler.startSection("toolHighlight");
            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
                String name = this.highlightingItemStack.getDisplayName();
                if (this.highlightingItemStack.hasDisplayName()) {
                    name = EnumChatFormatting.ITALIC + name;
                }

                name = this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, name);
                int opacity = (int) (this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) {
                    opacity = 255;
                }

                if (opacity > 0) {
                    int y = res.getScaledHeight() - 59;
                    if (!this.mc.playerController.shouldDrawHUD()) {
                        y += 14;
                    }

                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    FontRenderer font = this.highlightingItemStack.getItem().getFontRenderer(this.highlightingItemStack);

                    if (font != null) {
                        int x = (res.getScaledWidth() - font.getStringWidth(name)) / 2;
                        font.drawStringWithShadow(name, x, y, 0xFFFFFF | opacity << 24);
                    } else {
                        int x = (res.getScaledWidth() - this.fontrenderer.getStringWidth(name)) / 2;
                        this.fontrenderer.drawStringWithShadow(name, x, y, 0xFFFFFF | opacity << 24);
                    }

                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }

            this.mc.mcProfiler.endSection();
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.renderSelectedItem(res);
        }
    }


    protected void renderHUDText(int width, int height) {
        this.mc.mcProfiler.startSection("forgeHudText");
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        ArrayList<String> listL = new ArrayList<>();
        ArrayList<String> listR = new ArrayList<>();
        if (this.mc.isDemo()) {
            long time = this.mc.theWorld.getTotalWorldTime();
            if (time >= 120500L) {
                listR.add(I18n.format("demo.demoExpired"));
            } else {
                listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - time))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo && !pre(RenderGameOverlayEvent.ElementType.DEBUG)) {
            listL.addAll(this.debugOverlay.getLeft());
            listR.addAll(this.debugOverlay.getRight());
            post(RenderGameOverlayEvent.ElementType.DEBUG);
        }

        RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(((MixinGuiIngameForge) this).getEventParent(), listL, listR);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            int top = 2;
            Iterator<String> var7 = listL.iterator();


            while (var7.hasNext()) {
                String msg = var7.next();
                if (msg != null) {
                    drawRect(1, top - 1, 2 + this.fontrenderer.getStringWidth(msg) + 1, top + this.fontrenderer.FONT_HEIGHT - 1, -1873784752);
                    this.fontrenderer.drawString(msg, 2, top, 14737632);
                    top += this.fontrenderer.FONT_HEIGHT;
                }
            }

            top = 2;
            var7 = listR.iterator();

            while (var7.hasNext()) {
                String msg = var7.next();
                if (msg != null) {
                    int w = this.fontrenderer.getStringWidth(msg);
                    int left = width - 2 - w;
                    drawRect(left - 1, top - 1, left + w + 1, top + this.fontrenderer.FONT_HEIGHT - 1, -1873784752);
                    this.fontrenderer.drawString(msg, left, top, 14737632);
                    top += this.fontrenderer.FONT_HEIGHT;
                }
            }
        }

        this.mc.mcProfiler.endSection();
        post(RenderGameOverlayEvent.ElementType.TEXT);
    }

    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float hue = this.recordPlayingUpFor - partialTicks;
            int opacity = (int) (hue * 256.0F / 20.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (width / 2), (height - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int color = this.recordIsPlaying ? (Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & 0xFFFFFF) : 16777215;
                this.fontrenderer.drawString(this.recordPlaying, -this.fontrenderer.getStringWidth(this.recordPlaying) / 2, -4, color | opacity << 24);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }
    }


    protected void renderTitle(int width, int height, float partialTicks) {
        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float age = this.titlesTimer - partialTicks;
            int opacity = 255;
            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float f3 = (this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - age;
                opacity = (int) (f3 * 255.0F / this.titleFadeIn);
            }

            if (this.titlesTimer <= this.titleFadeOut) {
                opacity = (int) (age * 255.0F / this.titleFadeOut);
            }

            opacity = MathHelper.clamp_int(opacity, 0, 255);
            if (opacity > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(((float) width / 2), ((float) height / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & 0xFF000000;
                getFontRenderer().drawString(this.displayedTitle, ((float) -getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 0xFFFFFF | l, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                getFontRenderer().drawString(this.displayedSubTitle, ((float) -getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 0xFFFFFF | l, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
            new RenderOverlayEvent.Title(this.displayedTitle, this.displayedSubTitle, partialTicks).call();
        }
    }


    protected void renderChat(int width, int height) {
        this.mc.mcProfiler.startSection("chat");
        RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(((MixinGuiIngameForge) this).getEventParent(), 0, height - 48);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(event.posX, event.posY, 0.0F);
            this.persistantChatGUI.drawChat(this.updateCounter);
            GlStateManager.popMatrix();
            post(RenderGameOverlayEvent.ElementType.CHAT);
            this.mc.mcProfiler.endSection();
        }
    }

    protected void renderPlayerList(int width, int height) {
        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = this.mc.thePlayer.sendQueue;
        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            if (pre(RenderGameOverlayEvent.ElementType.PLAYER_LIST)) {
                return;
            }

            this.overlayPlayerList.renderPlayerlist(width, this.mc.theWorld.getScoreboard(), scoreobjective);
            post(RenderGameOverlayEvent.ElementType.PLAYER_LIST);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }
    }


    protected void renderHealthMount(int width, int height) {
        EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
        Entity tmp = player.ridingEntity;
        if (tmp instanceof EntityLivingBase) {
            bind(icons);
            if (!pre(RenderGameOverlayEvent.ElementType.HEALTHMOUNT)) {
                boolean unused = false;
                int left_align = width / 2 + 91;
                this.mc.mcProfiler.endStartSection("mountHealth");
                GlStateManager.enableBlend();
                EntityLivingBase mount = (EntityLivingBase) tmp;
                int health = (int) Math.ceil(mount.getHealth());
                float healthMax = mount.getMaxHealth();
                int hearts = (int) (healthMax + 0.5F) / 2;
                if (hearts > 30) {
                    hearts = 30;
                }

                int BACKGROUND = 52 + (unused ? 1 : 0);

                for (int heart = 0; hearts > 0; heart += 20) {
                    int top = height - right_height;
                    int rowCount = Math.min(hearts, 10);
                    hearts -= rowCount;

                    for (int i = 0; i < rowCount; i++) {
                        int x = left_align - i * 8 - 9;
                        drawTexturedModalRect(x, top, BACKGROUND, 9, 9, 9);
                        if (i * 2 + 1 + heart < health) {
                            drawTexturedModalRect(x, top, 88, 9, 9, 9);
                        } else if (i * 2 + 1 + heart == health) {
                            drawTexturedModalRect(x, top, 97, 9, 9, 9);
                        }
                    }
                    right_height += 10;
                }

                GlStateManager.disableBlend();
                post(RenderGameOverlayEvent.ElementType.HEALTHMOUNT);
            }
        }
    }

    private boolean pre(RenderGameOverlayEvent.ElementType type) {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(((MixinGuiIngameForge) this).getEventParent(), type));
    }

    private void post(RenderGameOverlayEvent.ElementType type) {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(((MixinGuiIngameForge) this).getEventParent(), type));
    }

    private void bind(ResourceLocation res) {
        this.mc.getTextureManager().bindTexture(res);
    }

    private static class GuiOverlayDebugCrystal extends GuiOverlayDebug {
        private GuiOverlayDebugCrystal(Minecraft mc) {
            super(mc);
        }

        protected void renderDebugInfoLeft() {
        }

        protected void renderDebugInfoRight(ScaledResolution res) {
        }

        private List<String> getLeft() {
            return call();
        }

        private List<String> getRight() {
            return getDebugInfoRight();
        }
    }
}