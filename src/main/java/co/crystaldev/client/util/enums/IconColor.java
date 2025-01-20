package co.crystaldev.client.util.enums;

import java.awt.*;

public enum IconColor {
    RED("Red", new Color(245, 73, 73)),
    GOLD("Gold", new Color(255, 166, 0)),
    GREEN("Green", new Color(131, 239, 84)),
    BLUE("Blue", new Color(55, 223, 246)),
    PURPLE("Purple", new Color(174, 129, 234)),
    PINK("Pink", new Color(243, 80, 225)),
    WHITE("White", Color.WHITE),
    CHROMA("Chroma", Color.WHITE);

    IconColor(String formattedName, Color color) {
        this.formattedName = formattedName;
        this.color = color;
    }

    private final String formattedName;

    private final Color color;

    public String getFormattedName() {
        return this.formattedName;
    }

    public Color getColor() {
        return this.color;
    }

    public static IconColor fromName(String str) {
        for (IconColor color : values()) {
            if (color.getFormattedName().equalsIgnoreCase(str))
                return color;
            if (color.toString().equalsIgnoreCase(str))
                return color;
        }
        return WHITE;
    }
}
