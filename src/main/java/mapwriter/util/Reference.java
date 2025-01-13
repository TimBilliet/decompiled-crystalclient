package mapwriter.util;

import net.minecraft.util.ResourceLocation;

import java.util.regex.Pattern;

public final class Reference {
    public static final String catLargeMapConfig = "largemap";

    public static final String catSmallMapConfig = "smallmap";

    public static final String catFullMapConfig = "fullscreenmap";

    public static final Pattern patternInvalidChars = Pattern.compile("[^\\p{L}\\p{Nd}_]");

    public static final Pattern patternInvalidChars2 = Pattern.compile("[^\\p{L}\\p{Nd}_ -]");

    public static final ResourceLocation backgroundTexture = new ResourceLocation("mapwriter", "textures/map/background.png");

    public static final ResourceLocation roundMapTexture = new ResourceLocation("mapwriter", "textures/map/border_round.png");

    public static final ResourceLocation squareMapTexture = new ResourceLocation("mapwriter", "textures/map/border_square.png");

    public static final ResourceLocation playerArrowTexture = new ResourceLocation("mapwriter", "textures/map/arrow_player.png");

    public static final ResourceLocation northArrowTexture = new ResourceLocation("mapwriter", "textures/map/arrow_north.png");

    public static final ResourceLocation leftArrowTexture = new ResourceLocation("mapwriter", "textures/map/arrow_text_left.png");

    public static final ResourceLocation rightArrowTexture = new ResourceLocation("mapwriter", "textures/map/arrow_text_right.png");
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwrite\\util\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */