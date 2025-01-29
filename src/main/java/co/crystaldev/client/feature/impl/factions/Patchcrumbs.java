package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.Client;
import co.crystaldev.client.cache.SandCache;
import co.crystaldev.client.duck.EntityExt;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.entity.EntitySpawnEvent;
import co.crystaldev.client.event.impl.init.ModuleOptionUpdateEvent;
import co.crystaldev.client.event.impl.network.PacketReceivedEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.ExplosionEvent;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.socket.client.group.PacketPatchcrumbUpdate;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.Vec3d;
import co.crystaldev.client.util.objects.crumbs.CrumbEntity;
import co.crystaldev.client.util.objects.crumbs.Patchcrumb;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.util.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static co.crystaldev.client.util.objects.crumbs.Patchcrumb.Direction.EAST_WEST;
import static co.crystaldev.client.util.objects.crumbs.Patchcrumb.Direction.NORTH_SOUTH;

@ModuleInfo(name = "Patchcrumbs", description = "Displays an indicator to help with patching", category = Category.FACTIONS)
public class Patchcrumbs extends Module implements IRegistrable {
    @Toggle(label = "Tracers")
    public boolean tracers = true;

    @Toggle(label = "Show Location Indicator")
    public boolean showText = true;

    @Toggle(label = "Use /ff")
    public boolean useFacChat = false;

    @Keybind(label = "Shout Location")
    public KeyBinding shoutLocation = new KeyBinding("crystalclient.key.shout_patchcrumb_location", 0, "Crystal Client - Crumbs");

    @DropdownMenu(label = "Direction", values = {"N/S", "E/W", "Both", "Auto"}, defaultValues = {"Auto"})
    public Dropdown<String> direction;

    @Slider(label = "Y-Offset", placeholder = "{value} blocks", minimum = -20.0D, maximum = 20.0D, standard = 0.0D, integers = true)
    public int offset = 0;

    @PageBreak(label = "Detection Methods")
    @Toggle(label = "ABC")
    public boolean useSandStacks = true;

    @Toggle(label = "Use Sand Entities")
    public boolean useSandEntities = true;

    @Toggle(label = "Use Explosions")
    public boolean useExplosions = false;

    @HoverOverlay("Ignores all other detection methods")
    @Toggle(label = "Use Float Finder")
    public boolean useFloatFinder = false;

    @Toggle(label = "Announce Received Coords")
    public boolean announceReceivedCoords = false;

    @PageBreak(label = "Crumb Configuration")
    @Slider(label = "Timeout", placeholder = "{value}s", minimum = 1.0D, maximum = 30.0D, standard = 10.0D, integers = true)
    public int timeout = 10;

    @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int lineWidth = 3;

    @Colour(label = "Primary Color")
    public ColorObject boxColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).mainColor, 180));

    @Colour(label = "Secondary Color")
    public ColorObject boundaryColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryColor, 180));

    @Colour(label = "Tracer Color")
    public ColorObject tracerColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryRed, 200));

    @Colour(label = "Text Color")
    public ColorObject textColor = ColorObject.fromColor((GuiOptions.getInstance()).neutralTextColor);

    @Colour(label = "Secondary Text Color")
    public ColorObject secondaryTextColor = new ColorObject(208, 208, 208, 255);

    private static Patchcrumbs INSTANCE;

    private static final String VERTICAL_LINE = Character.toString('|');

    private final SandCache sandCache = new SandCache();

    private long lastClean = System.currentTimeMillis();

    private boolean updated = false;

    public boolean isUpdated() {
        return this.updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    private long lastUpdate = 0L;

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    private long previous = 0L;

    private Patchcrumb currentCrumb;

    private final List<CrumbEntity> entities;

    private final List<Entity> velocityEntities;

    private int checkedEntities;

    private double lastYLevel;

    private boolean checkDirty;

    public Patchcrumbs() {
        this.currentCrumb = null;
        this.entities = new ArrayList<>();
        this.velocityEntities = new ArrayList<>();
        this.checkedEntities = -1;
        this.lastYLevel = -1.0D;
        this.checkDirty = false;
        INSTANCE = this;
        this.enabled = false;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Announce Received Coords", f -> this.useFloatFinder);
    }

    private void onPacketReceive(PacketReceivedEvent.Post event) {
        if (!useFloatFinder && this.useSandStacks && event.packet instanceof S22PacketMultiBlockChange) {
            S22PacketMultiBlockChange packet = (S22PacketMultiBlockChange) event.packet;
            for (S22PacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {
                if (data.getBlockState() != null && data.getBlockState().getBlock() instanceof net.minecraft.block.BlockFalling)
                    for (CrumbEntity entity : this.entities) {
                        if (entity == null || entity.getPos() == null || data.getPos() == null)
                            continue;
                        if (Math.sqrt(entity.getPos().distanceSq(data.getPos())) <= 4.0D)
                            entity.setBypassSandCheck(true);
                    }
            }
        }
    }

    public void clearCrumbFromFloatFinder(){
        currentCrumb = null;
        entities.clear();
        velocityEntities.clear();
    }

    public void setCrumbsFromFloatFinder(int x, int y, int z, String direction) {
        Patchcrumb.Direction dir = Patchcrumb.Direction.fromString(direction);
        double newX = x;
        double newZ = z;
        if (dir == NORTH_SOUTH) {
            newZ = mc.thePlayer.posZ;
        } else if (dir == EAST_WEST) {
            newX = mc.thePlayer.posX;
        }
        BlockPos pos = new BlockPos(newX, y, newZ);
        currentCrumb = new Patchcrumb(pos, new AxisAlignedBB(pos, pos.add(1, 1, 1)), dir, Patchcrumb.Source.FLOATFINDER);
        entities.clear();
        velocityEntities.clear();
    }

    private void onRenderWorld(RenderWorldEvent.Post event) {
        if (this.currentCrumb == null)
            return;
        if (this.currentCrumb.expired() && !useFloatFinder) {
            this.currentCrumb = null;
            return;
        }
        if (this.tracers && !useFloatFinder)
            RenderUtils.drawTracer(Vec3d.getNormalizedFromBlockPos(this.currentCrumb.getPos()), this.tracerColor, this.mc.thePlayer, event.partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(this.lineWidth);
        if (this.boundaryColor.isChroma())
            ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
        RenderUtils.setGlColor(this.boundaryColor);
        AxisAlignedBB northSouthBB = RenderUtils.normalize(this.currentCrumb.getBoundingBox()).expand(0.05D, 0.0D, 480.05D);
        AxisAlignedBB eastWestBB = RenderUtils.normalize(this.currentCrumb.getBoundingBox()).expand(480.05D, 0.0D, 0.05D);
        switch (this.currentCrumb.getDirection()) {
            case NORTH_SOUTH:
                RenderGlobal.drawSelectionBoundingBox(northSouthBB);
                break;
            case EAST_WEST:
                RenderGlobal.drawSelectionBoundingBox(eastWestBB);
                break;
            default:
                RenderGlobal.drawSelectionBoundingBox(northSouthBB);
                RenderGlobal.drawSelectionBoundingBox(eastWestBB);
                break;
        }
        if (this.boxColor.isChroma())
            ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
        RenderUtils.setGlColor(this.boxColor);
        AxisAlignedBB tntBox = RenderUtils.normalize(this.currentCrumb.getBoundingBox()).expand(0.05D, 0.0D, 0.05D);
        RenderGlobal.drawSelectionBoundingBox(tntBox);
        RenderUtils.drawFilledBoundingBox(tntBox);
        ShaderManager.getInstance().disableShader();
        GL11.glLineWidth(1.0F);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
        if (this.showText && !useFloatFinder)
            renderText();
    }

    private void onClientTick(ClientTickEvent.Post event) {
        if (this.mc.theWorld == null || useFloatFinder)
            return;
        if (currentCrumb != null && currentCrumb.getSource() == Patchcrumb.Source.FLOATFINDER)
            currentCrumb = null;
        Iterator<CrumbEntity> iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            CrumbEntity entity = iterator.next();
            if (entity.isDead()) {
                boolean flag = true;
                BlockPos entityPos = entity.getPos();
                if (entity instanceof CrumbEntity.TNT) {
                    BlockPos pos = ((CrumbEntity.TNT) entity).getTntPrimed().getPosition();
                    flag = (pos.getX() == entityPos.getX() && pos.getZ() == entityPos.getZ());
                } else if (entity instanceof CrumbEntity.FallingBlock) {
                    BlockPos pos = ((CrumbEntity.FallingBlock) entity).getFalling().getPosition();
                    flag = (pos.getX() == entityPos.getX() && pos.getZ() == entityPos.getZ());
                }
                if (flag && (isSandUnder(entity) || entity.isBypassSandCheck()))
                    if (entity.ticksSinceDeath() > 5L) {
                        if (!entity.isDirty()) {
                            makeCrumb(entity.getX(), entity.getY(), entity.getZ(), null, Patchcrumb.Source.ENTITY);
                            this.entities.forEach(CrumbEntity::setDead);
                            this.velocityEntities.clear();
                            this.entities.clear();
                            this.sandCache.clean();
                            return;
                        }
                    } else if (entity.ticksSinceDeath() > 4L) {
                        if (this.checkDirty && !isSolid(entityPos) && !isSolid(entityPos.down()))
                            entity.setDirty(true);
                        continue;
                    }
                iterator.remove();
            }
        }
        if (this.entities.isEmpty()) {
            this.checkDirty = false;
            this.lastYLevel = 0.0D;
        }
        for (Entity entity : this.mc.theWorld.loadedEntityList)
            checkEntity(entity);
        Iterator<Entity> entityIterator = this.velocityEntities.iterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity.isDead) {
                entityIterator.remove();
                continue;
            }
            if (Math.abs(entity.motionX) + Math.abs(entity.motionZ) != 0.0D)
                continue;
            if (this.lastYLevel == -1.0D) {
                this.lastYLevel = ((EntityExt) entity).getInitialYLevel();
            } else if (Math.abs(((EntityExt) entity).getInitialYLevel() - this.lastYLevel) > 2.0D) {
                this.checkDirty = true;
            }
            if (entity instanceof net.minecraft.entity.item.EntityFallingBlock) {
                BlockPos pos = entity.getPosition();
                if (!isWater(pos) || !isWater(pos.down(1)))
                    return;
                this.entities.add(new CrumbEntity.FallingBlock(entity));
            } else {
                this.entities.add(new CrumbEntity.TNT(entity));
            }
            entityIterator.remove();
        }
        if (System.currentTimeMillis() - this.lastClean > 120000L) {
            this.sandCache.clean();
            this.lastClean = System.currentTimeMillis();
        }
    }

    private void checkEntity(Entity entity) {
        if (entity instanceof net.minecraft.entity.item.EntityTNTPrimed || (this.useSandEntities && entity instanceof net.minecraft.entity.item.EntityFallingBlock)) {
            int id = ((EntityExt) entity).getCrystalEntityId();
            if (this.checkedEntities >= id)
                return;
            double motX = Math.abs(entity.motionX);
            double motZ = Math.abs(entity.motionZ);
            if (motX + motZ != 0.0D)
                if (motX < 1.0D && motZ < 1.0D) {
                    entity.motionX = entity.motionZ = 0.0D;
                } else {
                    if (!this.velocityEntities.contains(entity))
                        this.velocityEntities.add(entity);
                    return;
                }
            if (this.lastYLevel <= 0.0D) {
                this.lastYLevel = ((EntityExt) entity).getInitialYLevel();
            } else if (Math.abs(((EntityExt) entity).getInitialYLevel() - this.lastYLevel) > 2.0D) {
                this.checkDirty = true;
            }
            if (entity instanceof net.minecraft.entity.item.EntityFallingBlock) {
                BlockPos pos = entity.getPosition();
                if (!isWater(pos) || !isWater(pos.down(1)))
                    return;
                this.entities.add(new CrumbEntity.FallingBlock(entity));
            } else {
                this.entities.add(new CrumbEntity.TNT(entity));
            }
            this.checkedEntities = Math.max(id, this.checkedEntities);
        }
    }

    private void renderText() {
        String text;
        BlockPos pos = this.currentCrumb.getPos();
        Vec3d vec = RenderUtils.normalize(pos);
        double x = pos.getX() - this.mc.thePlayer.posX;
        double y = pos.getY() - this.mc.thePlayer.posY;
        double z = pos.getZ() - this.mc.thePlayer.posZ;
        double maxDistance = this.mc.gameSettings.renderDistanceChunks * 12.0D;
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance > maxDistance) {
            vec = new Vec3d(vec.x / distance * maxDistance, vec.y / distance * maxDistance, vec.z / distance * maxDistance);
            distance = maxDistance;
        }
        float size = ((float) distance * 0.1F + 1.0F) * 0.015F;
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(vec.x, vec.y, vec.z);
        GlStateManager.rotate(-(this.mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.mc.getRenderManager()).playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-size, -size, -size);
        switch (this.currentCrumb.getDirection()) {
            case NORTH_SOUTH:
                text = String.format("X: %d %s Y: %d", pos.getX(), VERTICAL_LINE, pos.getY());
                break;
            case EAST_WEST:
                text = String.format("Z: %d %s Y: %d", pos.getZ(), VERTICAL_LINE, pos.getY());
                break;
            default:
                text = String.format("X: %d %s Y: %d %s Z: %d", pos.getX(), VERTICAL_LINE, pos.getY(), VERTICAL_LINE, pos.getZ());
                break;
        }
        int tx = -(this.mc.fontRendererObj.getStringWidth(text) / 2);
        int ty = -(this.mc.fontRendererObj.FONT_HEIGHT / 2);
        for (String str : text.split(" ")) {
            RenderUtils.drawString(str, tx, ty, NumberUtils.isNumber(str) ? this.secondaryTextColor : this.textColor, true);
            tx += this.mc.fontRendererObj.getStringWidth(str + " ");
        }
        GlStateManager.enableDepth();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    public void makeCrumb(double x, double y, double z, Patchcrumb.Direction direction, Patchcrumb.Source source) {
        boolean flag = true;
        BlockPos pos = new BlockPos(x, Math.round(y + this.offset), z);
        if (this.currentCrumb != null) {
            BlockPos blockPos = this.currentCrumb.getPos();
            if (blockPos.getX() == pos.getX() && blockPos.getZ() == pos.getZ())
                flag = false;
        }
        if (direction == null)
            switch (this.direction.getCurrentSelection()) {
                case "N/S":
                    direction = NORTH_SOUTH;
                    break;
                case "E/W":
                    direction = Patchcrumb.Direction.EAST_WEST;
                    break;
                case "Auto":
                    if (this.mc.theWorld.isBlockLoaded(pos)) {
                        boolean northSouth = ((isSolid(pos.north()) || isSolid(pos.south())) && (isWater(pos.north(2)) || isWater(pos.south(2))) && (isSolid(pos.north(3)) || isSolid(pos.south(3))));
                        boolean eastWest = ((isSolid(pos.east()) || isSolid(pos.west())) && (isWater(pos.east(2)) || isWater(pos.west(2))) && (isSolid(pos.east(3)) || isSolid(pos.west(3))));
                        if (northSouth) {
                            direction = NORTH_SOUTH;
                        } else if (eastWest) {
                            direction = Patchcrumb.Direction.EAST_WEST;
                        }
                        if (direction != null)
                            break;
                    }
                default:
                    direction = Patchcrumb.Direction.BOTH;
                    break;
            }
        Patchcrumb crumb = new Patchcrumb(pos, new AxisAlignedBB(pos, pos.add(1, 1, 1)), direction, source);
        if (this.currentCrumb == null || flag) {
            updateStatus();
            this.currentCrumb = crumb;
            if (source != Patchcrumb.Source.GROUP) {
                PacketPatchcrumbUpdate p = new PacketPatchcrumbUpdate(pos.getX(), MathHelper.floor_double(y), pos.getZ(), direction);
                Client.sendPacket(p);
            }
        }
    }

    private void updateStatus() {
        long ms = System.currentTimeMillis();
        if (this.previous != ms) {
            this.lastUpdate = this.previous;
            this.previous = ms;
            if (this.lastUpdate != 0L)
                this.updated = true;
        }
    }

    private boolean isSandUnder(CrumbEntity entity) {
        if (!this.useSandStacks)
            return true;
        BlockPos pos = entity.getPos();
        for (int i = 1; i < 15; i++) {
            if (this.sandCache.isBlockSand(pos.down(i)))
                return true;
        }
        return false;
    }

    private boolean isSolid(BlockPos pos) {
        if (this.mc.theWorld == null)
            return false;
        Block block = this.mc.theWorld.getBlockState(pos).getBlock();
        return (!(block instanceof net.minecraft.block.BlockLiquid) && block.getMaterial() != Material.air && !block.isReplaceable(this.mc.theWorld, pos));
    }

    private boolean isWater(BlockPos pos) {
        if (this.mc.theWorld == null)
            return false;
        return this.mc.theWorld.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid;
    }

    public static Patchcrumbs getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, PacketReceivedEvent.Post.class, this::onPacketReceive);
        EventBus.register(this, ClientTickEvent.Post.class, this::onClientTick);
        EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
        EventBus.register(this, EntitySpawnEvent.Post.class, ev -> checkEntity(ev.getEntity()));
        EventBus.register(this, ExplosionEvent.class, ev -> {
            if (ev.size > 3.0F && this.useExplosions)
                this.entities.add(new CrumbEntity.Explosion(ev.posX, ev.posY, ev.posZ));
        });
        EventBus.register(this, InputEvent.Key.class, ev -> {
            if (this.shoutLocation.isPressed()) {
                if (this.currentCrumb == null)
                    return;
                BlockPos pos = this.currentCrumb.getPos();
                String message = String.format("%s[Current Shot] X: %d, Y: %d, Z: %d", this.useFacChat ? "/ff " : "", pos.getX(), pos.getY(), pos.getZ());
                this.mc.thePlayer.sendChatMessage(message);
            }
        });
        EventBus.register(this, ModuleOptionUpdateEvent.class, ev -> {
            if (ev.getModule() == this && ev.getOptionName().equals("Use Explosions") && this.useExplosions)
                NotificationHandler.addNotification("&c&lNOTE:&r Having the explosion detection method may interfere with the accuracy of Patchcrumbs, only use this setting if you are unable to render TNT or Sand on this server.");
        });
    }
}