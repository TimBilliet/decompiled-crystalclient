package co.crystaldev.client.gui;

import co.crystaldev.client.util.ColorObject;
import java.awt.Color;
import net.minecraft.util.MathHelper;

public class GuiOptions {
    private static GuiOptions INSTANCE = null;

    public Color backgroundColor = new Color(22, 22, 22, 72);

    public Color backgroundColor1 = new Color(24, 23, 35, 186);

    public Color sidebarBackground = new Color(44, 52, 65, 60);

    public Color mainColor = new Color(27, 114, 244);

    public Color secondaryColor = new Color(0, 192, 250);

    public Color mainEnabled = new Color(38, 61, 178);

    public Color secondaryEnabled = new Color(57, 103, 222);

    public Color mainDisabled = new Color(60, 60, 60, 255);

    public Color secondaryDisabled = new Color(80, 80, 80, 255);

    public Color mainRed = new Color(241, 43, 37);

    public Color secondaryRed = new Color(253, 109, 109);

    public Color neutralButtonBackground = new Color(15, 15, 15, 50);

    public Color hoveredButtonBackground = new Color(50, 50, 50, 140);

    public Color unselectedTextColor = new Color(76, 76, 76);

    public Color neutralTextColor = new Color(188, 188, 188, 220);

    public Color hoveredTextColor = new Color(255, 255, 255);

    public Color getColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp_int(alpha, 0, 255));
    }

    public ColorObject getColorObject(Color color, int alpha) {
        return new ColorObject(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp_int(alpha, 0, 255), (color instanceof ColorObject && ((ColorObject)color)
                .isChroma()));
    }

    public static GuiOptions getInstance() {
        return (INSTANCE == null) ? (INSTANCE = new GuiOptions()) : INSTANCE;
    }

    public enum Theme {
        DARK_RED("Dark Red", new Color(102, 0, 0), new Color(130, 8, 8), new Color(189, 22, 22), new Color(168, 35, 17)),
        RED("Red", new Color(203, 52, 37), new Color(195, 71, 70), new Color(172, 18, 19), new Color(172, 46, 45)),
        ORANGE("Orange", new Color(250, 117, 0), new Color(244, 139, 27), new Color(178, 99, 38), new Color(222, 134, 57)),
        GREEN("Green", new Color(51, 190, 27), new Color(27, 241, 88), new Color(26, 136, 28), new Color(32, 208, 41)),
        PACIFIC_BLUE("Pacific Blue", new Color(0, 96, 120), new Color(9, 144, 177), new Color(12, 133, 172), new Color(0, 175, 196)),
        MIDNIGHT_BLUE("Midnight Blue", new Color(27, 44, 73), new Color(3, 58, 102), new Color(32, 78, 137), new Color(46, 114, 187)),
        PURPLE("Purple", new Color(101, 47, 243), new Color(132, 91, 234), new Color(115, 52, 227), new Color(112, 76, 195)),
        PINK("Pink", new Color(250, 81, 170), new Color(238, 122, 183), new Color(233, 29, 134), new Color(222, 78, 150)),
        DEFAULT("Default", new Color(27, 114, 244), new Color(0, 192, 250), new Color(38, 61, 178), new Color(57, 103, 222));

        Theme(String name, Color mainColor, Color secondaryColor, Color mainEnabled, Color secondaryEnabled) {
            this.name = name;
            this.mainColor = mainColor;
            this.secondaryColor = secondaryColor;
            this.mainEnabled = mainEnabled;
            this.secondaryEnabled = secondaryEnabled;
        }

        public final String name;

        public final Color mainColor;

        public final Color secondaryColor;

        public final Color mainEnabled;

        public final Color secondaryEnabled;

        public void setTheme() {
            GuiOptions opts = GuiOptions.getInstance();
            opts.mainColor = this.mainColor;
            opts.secondaryColor = this.secondaryColor;
            opts.mainEnabled = this.mainEnabled;
            opts.secondaryEnabled = this.secondaryEnabled;
        }
    }
}
