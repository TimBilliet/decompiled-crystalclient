package com.github.timmekeclient;

import com.github.timmekeclient.group.objects.enums.Rank;
import com.github.timmekeclient.util.ColorObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;

public class Reference {
    public static final String NAME = "Timmeke_ Client";

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

    public static final Logger LOGGER = LogManager.getLogger("Timmeke_ Client");

    public static final KeyBinding OPEN_GUI = new KeyBinding("timmekeclient.key.open_gui", 54, "Timmeke_ Client");

    public static final KeyBinding CREATE_WAYPOINT = new KeyBinding("timmekeclient.key.create_waypoint", 48, "Timmeke_ Client");

    public static final ResourceLocation MOTION_BLUR_SHADER = new ResourceLocation("shaders/post/motion_blur.json");

    public static final ResourceLocation BLUR_SHADER = new ResourceLocation("shaders/post/menu_blur.json");

    public static final HashSet<String> RESOURCE_DOMAINS = new HashSet<>(Arrays.asList("minecraft", "realms", "timmekeclient", "schematica", "mapwriter", "wdl", "emoticons"));
}
