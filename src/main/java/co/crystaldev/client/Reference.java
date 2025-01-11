package co.crystaldev.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.group.objects.enums.Rank;

import java.util.Arrays;
import java.util.HashSet;

public class Reference {
    public static final String NAME = "Timmeke_ Crystal Client";

    public static final String ID = "crystalclient";

    public static final String VERSION = "1.1.16-projectassfucker";

    public static final String GIT_COMMIT_ID = "37aa61df290702c2b191a4ef4ab4f92e7ab7563a";

    public static final String GIT_COMMIT_ID_ABBR = "37aa61d";

    public static final String GIT_BRANCH = "offline";

    public static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(ColorObject.class, new ColorObject.Adapter())
            .registerTypeAdapter(Rank.Adapter.class, new Rank.Adapter())
            .disableHtmlEscaping()
            .create();

    public static final Gson GSON_PRETTY = (new GsonBuilder())
            .registerTypeAdapter(ColorObject.class, new ColorObject.Adapter())
            .registerTypeAdapter(Rank.Adapter.class, new Rank.Adapter())
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static final Logger LOGGER = LogManager.getLogger("Crystal Client");

    public static final KeyBinding OPEN_GUI = new KeyBinding("crystalclient.key.open_gui", 54, "Crystal Client");

    public static final KeyBinding CREATE_WAYPOINT = new KeyBinding("crystalclient.key.create_waypoint", 48, "Crystal Client");

    public static final ResourceLocation MOTION_BLUR_SHADER = new ResourceLocation("shaders/post/motion_blur.json");

    public static final ResourceLocation BLUR_SHADER = new ResourceLocation("shaders/post/menu_blur.json");

    public static final HashSet<String> RESOURCE_DOMAINS = new HashSet<>(Arrays.asList("minecraft", "realms", "crystalclient", "schematica", "mapwriter", "wdl", "emoticons"));
}
