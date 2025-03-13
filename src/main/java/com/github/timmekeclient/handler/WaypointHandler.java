package com.github.timmekeclient.handler;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.init.ShutdownEvent;
import com.github.timmekeclient.event.impl.network.ServerDisconnectEvent;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.settings.Waypoints;
import com.github.timmekeclient.gui.screens.ScreenWaypoints;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.FileUtils;
import com.github.timmekeclient.util.enums.EnumActionShift;
import com.github.timmekeclient.util.objects.Waypoint;
import com.github.timmekeclient.util.type.Tuple;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WaypointHandler implements IRegistrable {
    private static final File waypointFile = new File(Client.getClientRunDirectory(), "waypoints.json");

    private static WaypointHandler INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    private final List<Waypoint> registeredWaypoints;

    private final List<Tuple<EnumActionShift, Waypoint>> queue;

    public List<Waypoint> getRegisteredWaypoints() {
        return this.registeredWaypoints;
    }

    public WaypointHandler() {
        INSTANCE = this;
        EventBus.register(new Waypoints());
        this.registeredWaypoints = new LinkedList<>();
        this.queue = new ArrayList<>();
        populateWaypoints();
    }

    public void addWaypoint(String name, String serverIp, BlockPos pos, ColorObject color) {
        this.queue.add(new Tuple<>(EnumActionShift.ADD, (new Waypoint(name, serverIp, pos, color)).setWorld(Client.getCurrentWorldName())));
    }

    public void addWaypoint(Waypoint waypoint) {
        this.queue.add(new Tuple<>(EnumActionShift.ADD, waypoint));
    }

    public void removeWaypoint(Waypoint waypoint) {
        this.queue.add(new Tuple<>(EnumActionShift.REMOVE, waypoint));
    }

    public void removeWaypointIf(Predicate<Waypoint> predicate) {
        List<Tuple<EnumActionShift, Waypoint>> toRemove = this.registeredWaypoints.stream()
                .filter(predicate)
                .map(wp -> new Tuple<>(EnumActionShift.REMOVE, wp))
                .collect(Collectors.toList());
        this.queue.addAll(toRemove);
    }

    private void populateWaypoints() {
        if (waypointFile.exists())
            try {
                this.registeredWaypoints.clear();
                FileReader fr = new FileReader(waypointFile);
                this.registeredWaypoints.addAll(Reference.GSON_PRETTY.fromJson(fr, (new TypeToken<LinkedList<Waypoint>>() {

                }).getType()));
                fr.close();
            } catch (Exception ex) {
                Reference.LOGGER.error("An exception was thrown while populating waypoints.", ex);
            }
    }

    public void saveWaypoints() {
        if (this.registeredWaypoints == null)
            return;
        List<Waypoint> filteredWaypoints = this.registeredWaypoints.stream().filter(wp -> !wp.isServerSided()).collect(Collectors.toList());
        if (!filteredWaypoints.isEmpty()) {
            if (!Client.getClientRunDirectory().exists())
                Client.getClientRunDirectory().mkdirs();
            String json = null;
            while (json == null || !FileUtils.isValidJson(json, JsonArray.class))
                json = Reference.GSON.toJson(filteredWaypoints);
            try {
                FileWriter fileWriter = new FileWriter(waypointFile);
                fileWriter.write(json);
                fileWriter.close();
            } catch (IOException ex) {
                Reference.LOGGER.error("An exception was thrown while saving to the waypoints file.", ex);
            }
        } else if (waypointFile.exists()) {
            waypointFile.delete();
        }
    }

    public static WaypointHandler getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ServerDisconnectEvent.class, ev -> this.registeredWaypoints.removeIf(Waypoint::isServerSided));
        EventBus.register(this, ShutdownEvent.class, ev -> saveWaypoints());
        EventBus.register(this, ClientTickEvent.Pre.class, ev -> {
            if (!this.queue.isEmpty()) {
                boolean reload = false;
                for (Tuple<EnumActionShift, Waypoint> tuple : this.queue) {
                    Waypoint wp = tuple.getItem2();
                    if (tuple.getItem1() == EnumActionShift.ADD) {
                        this.registeredWaypoints.removeIf(wp::equals);
                        this.registeredWaypoints.add(wp);
                        continue;
                    }
                    if (!wp.isServerSided() && !wp.isCanBeDeleted())
                        continue;
                    this.registeredWaypoints.remove(wp);
                    reload = true;
                }
                saveWaypoints();
                if (reload && this.mc.currentScreen instanceof ScreenWaypoints) {
                    ((ScreenWaypoints) this.mc.currentScreen).initWaypoints();
                }

                this.queue.clear();
            }
        });
    }
}
