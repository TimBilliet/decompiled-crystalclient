package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.init.ShutdownEvent;
import com.github.timmekeclient.feature.annotations.Hidden;
import com.github.timmekeclient.feature.annotations.properties.*;
import com.github.timmekeclient.feature.base.*;
import com.github.timmekeclient.gui.GuiOptions;
import com.github.timmekeclient.handler.ModuleHandler;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.RenderUtils;
import com.github.timmekeclient.util.enums.AnchorRegion;
import com.github.timmekeclient.util.objects.ModulePosition;
import com.github.timmekeclient.util.type.Tuple;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

@ModuleInfo(name = "Info HUD", description = "Transforms all HUD Modules into an ordered list onscreen", category = Category.HUD, nameAliases = {"List HUD"})
public class InfoHud extends HudModule implements IRegistrable {
    @Toggle(label = "Display Title")
    public boolean displayTitle = true;

    @DropdownMenu(label = "Brace Style", values = {"[Content]", "(Content)", "<Content>", "Content:"}, defaultValues = {"[Content]"})
    public Dropdown<String> braceStyle;

    @Colour(label = "Header Color", isTextRender = true)
    public ColorObject headerColor = ColorObject.fromColor((GuiOptions.getInstance()).mainColor)
            .setBold(true).setUnderline(true).setAlpha(255);

    @Colour(label = "Primary Color", isTextRender = true)
    public ColorObject primaryColor = ColorObject.fromColor((GuiOptions.getInstance()).mainColor)
            .setBold(true).setAlpha(255);

    @Colour(label = "Text Color", isTextRender = true)
    public ColorObject secondaryColor = ColorObject.fromColor(Color.WHITE).setAlpha(255);

    @Property(label = "Ordering")
    @Hidden
    public String moduleOrdering = "";

    private static InfoHud INSTANCE;

    private final LinkedList<Tuple<Integer, HudModuleText>> registeredModules = new LinkedList<>();

    public LinkedList<Tuple<Integer, HudModuleText>> getRegisteredModules() {
        return this.registeredModules;
    }

    public InfoHud() {
        this.enabled = false;
        this.displayWhileDisabled = false;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 5.0F, 5.0F);
        this.priority = -100000;
        INSTANCE = this;
    }

    public void disable() {
        super.disable();
        saveOrdering();
    }

    public void configPostInit() {
        super.configPostInit();
        this.registeredModules.clear();
        if (this.moduleOrdering.isEmpty()) {
            int i = 0;
            for (Module module : ModuleHandler.getModules()) {
                if (module instanceof HudModuleText && ((HudModuleText) module).hasInfoHud) {
                    this.registeredModules.add(new Tuple(Integer.valueOf(i), module));
                    i++;
                }
            }
            saveOrdering();
        } else {
            for (JsonElement element : Reference.GSON.fromJson(this.moduleOrdering, JsonArray.class)) {
                JsonObject obj = element.getAsJsonObject();
                int i = obj.get("index").getAsInt();
                String module = obj.get("module").getAsString();
                for (Module m : ModuleHandler.getModules()) {
                    if ((m.name.equals(module) || Arrays.<String>asList(m.nameAliases).contains(module)) && m instanceof HudModuleText)
                        this.registeredModules.add(new Tuple(Integer.valueOf(i), m));
                }
            }
        }
        int index = this.registeredModules.size();
        for (Module module : ModuleHandler.getModules()) {
            if (!(module instanceof HudModuleText) || !((HudModuleText) module).hasInfoHud || this.registeredModules.stream().anyMatch(t -> ((HudModuleText) t.getItem2()).name.equals(module.name)))
                continue;
            this.registeredModules.add(new Tuple(Integer.valueOf(index), module));
            index++;
        }
        saveOrdering();
    }

    public void draw() {
        int x = getRenderX();
        int y = getRenderY();
        int width = 0;
        int ny = y;
        if (this.displayTitle) {
            width = Math.max(RenderUtils.drawString("HUD Information", x, ny, this.headerColor) - x, width);
            ny += 11;
        }
        String[] braces = ((String) this.braceStyle.getCurrentSelection()).split("Content");
        for (Tuple<Integer, HudModuleText> tuple : this.registeredModules) {
            HudModuleText module = (HudModuleText) tuple.getItem2();
            if (module.infoHudEnabled && module.awaitingInfoHudRender) {
                Tuple<String, String> hud = module.getInfoHud();
                if (hud == null)
                    continue;
                module.awaitingInfoHudRender = false;
                width = Math.max(RenderUtils.drawString(" " + (String) hud.getItem2(), RenderUtils.drawString(braces[0] + (String) hud.getItem1() + braces[1], x, ny, this.primaryColor), ny, this.secondaryColor) - x, width);
                ny += 9;
            }
        }
        this.width = width;
        this.height = ny - y;
    }

    public boolean shouldModuleRender(Module module) {
        if (module == this)
            return true;
        if (!(module instanceof HudModuleText) || !((HudModuleText) module).hasInfoHud)
            return true;
        return !((HudModuleText) module).infoHudEnabled;
    }

    private void saveOrdering() {
        JsonArray arr = new JsonArray();
        for (Tuple<Integer, HudModuleText> tuple : this.registeredModules) {
            JsonObject obj = new JsonObject();
            obj.addProperty("index", (Number) tuple.getItem1());
            obj.addProperty("module", ((HudModuleText) tuple.getItem2()).name);
            arr.add((JsonElement) obj);
        }
        this.moduleOrdering = Reference.GSON.toJson((JsonElement) arr);
    }

    public void registerEvents() {
        EventBus.register(this, ShutdownEvent.class, (byte) 0, ev -> saveOrdering());
    }

    public static InfoHud getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\InfoHud.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */