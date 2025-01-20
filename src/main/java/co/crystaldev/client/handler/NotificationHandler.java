package co.crystaldev.client.handler;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NotificationHandler {
    private static final int NOTIFICATION_WIDTH = 205;

    private static final int MAX_NOTIFICATIONS = 5;

    private static final int RADIUS = 10;

    private static final FontRenderer FONT_RENDERER_TITLE = Fonts.NUNITO_SEMI_BOLD_18;

    private static final FontRenderer FONT_RENDERER = Fonts.SOURCE_SANS_16;

    private static final List<Notification> NOTIFICATIONS = new LinkedList<>();

    public static void draw() {
        int index = 0;
        int max = Math.min(5, NOTIFICATIONS.size());
        int x = (Minecraft.getMinecraft()).displayWidth / 2 - 5 - 205;
        int y = (Minecraft.getMinecraft()).displayHeight / 2 - 5;
        Iterator<Notification> iterator = NOTIFICATIONS.iterator();
        while (iterator.hasNext() &&
                index < max) {
            Notification next = iterator.next();
            boolean complete = next.move();
            String[] text = WordUtils.wrap(next.getText(), 40).split("\n");
            int height = Math.max(((next.getTitle() != null) ? (FONT_RENDERER_TITLE.getStringHeight() + 6) : 0) + FONT_RENDERER.getStringHeight() * text.length + 6 + text.length * 2, 34);
            y -= height + 5;
            if (next.getY() == -1)
                next.setY(y);
            if (y != next.getY()) {
                next.setAnimationY(new Animation(150L, (next.getY() - y), 0.0F, Notification.EASING_FUNCTION));
                next.setY(y);
            }
            if (next.getAnimationY() != null && next.getAnimationY().isComplete())
                next.setAnimationY(null);
            int xLoc = (int) (x + next.getAnimationX().getValue());
            int yLoc = (int) (y + ((next.getAnimationY() != null) ? next.getAnimationY().getValue() : 0.0F));
            double progress = next.getProgress();
            int barWidth = Math.min(205, (int) Math.floor(205.0D * progress));
            float percent = next.getFadingColor().getCurrentColor().getAlpha() / 255.0F;
            GL11.glTranslatef(0.0F, 0.0F, 1000.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawRoundedRect(xLoc, yLoc, (xLoc + 205), (yLoc + height), 10.0D,
                    getColor((GuiOptions.getInstance()).backgroundColor, percent));
            RenderUtils.drawRoundedRect(xLoc, yLoc, (xLoc + 205), (yLoc + height), 10.0D,
                    getColor((GuiOptions.getInstance()).backgroundColor1, percent));
            int textHeight = FONT_RENDERER.getStringHeight();
            int textY = (int) (yLoc + (height - 5.0F) / 2.0F) - textHeight * text.length / 2 - ((next.getTitle() != null) ? (FONT_RENDERER_TITLE.getStringHeight() / 2) : 0);
            if (next.getTitle() != null) {
                FONT_RENDERER_TITLE.drawString(next.getTitle(), xLoc + 34, textY, (percent > 0.019607844F) ? getColor(Color.WHITE, percent) : (new Color(0, 0, 0, 5)).getRGB());
                textY += FONT_RENDERER_TITLE.getStringHeight();
            }
            for (String s : text) {
                FONT_RENDERER.drawString(s, xLoc + 34, textY, (percent > 0.019607844F) ? getColor(Color.WHITE, percent) : (new Color(0, 0, 0, 5)).getRGB());
                textY += textHeight;
            }
            RenderUtils.drawRoundedRect(xLoc, ((yLoc + height) - 5.0F), (xLoc + 205), (yLoc + height), 5.0D,
                    getColor((GuiOptions.getInstance()).sidebarBackground, percent));
            RenderUtils.drawRoundedHorizontalGradientRect(xLoc, ((yLoc + height) - 5.0F), Math.max((xLoc + barWidth), 5.0D), (yLoc + height), 5.0D,
                    getColor((GuiOptions.getInstance()).mainEnabled, percent), getColor((GuiOptions.getInstance()).secondaryEnabled, percent));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, percent);
            int logoSize = 23;
            RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, (xLoc + 3), yLoc + (height - 5.0D) / 2.0D - logoSize / 2.0D, logoSize, logoSize);
            if (complete)
                iterator.remove();
            GL11.glTranslatef(0.0F, 0.0F, -1000.0F);
            index++;
        }
    }

    private static int getColor(Color color, float alphaPercent) {
        return (new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathHelper.clamp_float(color.getAlpha() * alphaPercent, 0.0F, 255.0F))).getRGB();
    }

    public static void addNotification(String title, String text) {
        synchronized (NOTIFICATIONS) {
            title = ChatColor.translate('&', title);
            text = ChatColor.translate('&', text);
            NOTIFICATIONS.add(new Notification(title, text, 5000L, 205));
        }
    }

    public static void addNotification(String title, String text, long delay) {
        synchronized (NOTIFICATIONS) {
            title = ChatColor.translate('&', title);
            text = ChatColor.translate('&', text);
            NOTIFICATIONS.add(new Notification(title, text, Math.max(0L, delay), 205));
        }
    }

    public static void addNotification(String text) {
        synchronized (NOTIFICATIONS) {
            text = ChatColor.translate('&', text);
            NOTIFICATIONS.add(new Notification(text, 5000L, 205));
        }
    }

    public static void addNotification(String text, long delay) {
        synchronized (NOTIFICATIONS) {
            text = ChatColor.translate('&', text);
            NOTIFICATIONS.add(new Notification(text, Math.max(0L, delay), 205));
        }
    }
}
