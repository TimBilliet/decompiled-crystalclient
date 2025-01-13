package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.Client;
import co.crystaldev.client.Config;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.ConfigEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.screens.schematica.ScreenSchematicaBase;
import co.crystaldev.client.handler.ClientCommandHandler;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.SchematicaGuiType;
import co.crystaldev.client.util.objects.MissingSchematicBlock;
import co.crystaldev.client.util.objects.Vec3d;
import co.crystaldev.client.util.type.GlueList;
import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderType;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.command.CommandSchematicUndo;
import com.github.lunatrius.schematica.command.CommandSchematicaReplace;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ModuleInfo(name = "Schematica", description = "Settings for the Schematica mod", category = Category.FACTIONS, nameAliases = {"Printer"})
public class Schematica extends Module implements IRegistrable {
    @Toggle(label = "Auto Tick")
    public boolean autoTick = true;

    @Toggle(label = "Auto Break Misplaced Blocks")
    public boolean autoBreak = false;

    @HoverOverlay({"When a portion of the schematic is isolated from other solid blocks, Crystal Client", "will build over to the isolated area.", " ", "&lNote: &rThis option may misplace more blocks with certain schematics"})
    @Toggle(label = "Bridge to Unreachable Blocks")
    public boolean autoBridge = false;

    @Toggle(label = "Open Dispensers While Printing")
    public boolean openDispensersWhilePrinting = true;

    @Toggle(label = "Load Schematics at Y1")
    public boolean loadAtY1 = false;

    @Toggle(label = "Remove Tracers from Y253")
    public boolean trayMode = false;

    @Toggle(label = "Persist After Disconnect")
    public boolean persist = true;

    @Toggle(label = "Move Schematic with Arrow Keys")
    public boolean arrowKeys = true;

    @Slider(label = "Auto Tick Timeout", placeholder = "{value}ms", minimum = 1.0D, maximum = 10.0D, standard = 2.0D, integers = true)
    public int autoTickTimeout = 2;

    @PageBreak(label = "Printer Settings")
    @Toggle(label = "Destroy Instantly")
    public boolean destroyInstantly = false;

    @HoverOverlay({"When enabled, Schematica will be able to print off of water/lava blocks."})
    @Toggle(label = "Place on Liquids")
    public boolean placeOnLiquid = true;

    @Toggle(label = "Place Instantly")
    public boolean placeInstantly = true;

    @Slider(label = "Placement Delay", minimum = 0.0D, maximum = 20.0D, standard = 0.0D, integers = true)
    public int placementDelay = 0;

    @Slider(label = "Placement Distance", minimum = 0.0D, maximum = 10.0D, standard = 5.0D, integers = true)
    public int placementDistance = 5;

    @Slider(label = "Timeout", minimum = 0.0D, maximum = 100.0D, standard = 2.0D, integers = true)
    public int timeout = 2;

    @PageBreak(label = "Rendering Settings")
    @Toggle(label = "Fix Dispenser Meta")
    public boolean dispenserMetaFix = true;

    @Toggle(label = "Highlight Air")
    public boolean highlightAir = true;

    @Toggle(label = "Highlight Schematic in Liquids")
    public boolean highlightInLiquid = true;

    @Toggle(label = "Schematic ESP")
    public boolean schemEsp = true;

    @DropdownMenu(label = "ESP Mode", values = {"ESP", "Tracers"}, defaultValues = {"ESP", "Tracers"}, limitlessSelections = true)
    public Dropdown<String> espMode;

    @Slider(label = "Displayed ESP Limit", minimum = 1.0D, maximum = 5000.0D, standard = 2000.0D, integers = true)
    public int espLimit = 2000;

    @PageBreak(label = "Keybindings")
    @Keybind(label = "Open Schematic Load GUI")
    public KeyBinding loadSchematic = new KeyBinding("schematica.key.load", 181, "Crystal Client - Schematica");

    @Keybind(label = "Open Save Schematic GUI")
    public KeyBinding saveSchematic = new KeyBinding("schematica.key.save", 55, "Crystal Client - Schematica");

    @Keybind(label = "Open Schematic Control GUI")
    public KeyBinding manipulateSchematic = new KeyBinding("schematica.key.control", 74, "Crystal Client - Schematica");

    @Keybind(label = "Move Here")
    public KeyBinding moveHere = new KeyBinding("schematica.key.moveHere", 0, "Crystal Client - Schematica");

    @Keybind(label = "Trace All Missing Blocks")
    public KeyBinding traceAll = new KeyBinding("crystalclient.key.trace_all_materials", 0, "Crystal Client - Schematica");

    @Keybind(label = "Toggle Tracers")
    public KeyBinding toggleTracers = new KeyBinding("crystalclient.key.toggle_schem_tracers", 0, "Crystal Client - Schematica");

    @Keybind(label = "Increment Layer")
    public KeyBinding layerInc = new KeyBinding("schematica.key.layerInc", 0, "Crystal Client - Schematica");

    @Keybind(label = "Decrement Layer")
    public KeyBinding layerDec = new KeyBinding("schematica.key.layerDec", 0, "Crystal Client - Schematica");

    @Keybind(label = "Toggle Layer Rendering")
    public KeyBinding layerToggle = new KeyBinding("schematica.key.layerToggle", 0, "Crystal Client - Schematica");

    @Keybind(label = "Toggle Schematic Rendering")
    public KeyBinding renderToggle = new KeyBinding("schematica.key.renderToggle", 0, "Crystal Client - Schematica");

    @Keybind(label = "Toggle Printer")
    public KeyBinding printerToggle = new KeyBinding("schematica.key.printerToggle", 0, "Crystal Client - Schematica");

    @Keybind(label = "Toggle Printer Block Break")
    public KeyBinding blockBreakToggle = new KeyBinding("schematica.key.blockDestroyToggle", 0, "Crystal Client - Schematica");

    @Keybind(label = "Toggle Auto Bridge")
    public KeyBinding autoBridgeToggle = new KeyBinding("crystalclient.key.toggle_schem_auto_bridge", 0, "Crystal Client - Schematica");

    @PageBreak(label = "Tracer Colors")
    @Colour(label = "Missing Block")
    public ColorObject missingBlockColor = new ColorObject(60, 180, 255, 150);

    @Colour(label = "Incorrect Block")
    public ColorObject incorrectBlock = new ColorObject(241, 65, 65, 150);

    @Colour(label = "Incorrect Block Data")
    public ColorObject incorrectBlockMeta = new ColorObject(236, 211, 50, 150);

    @PageBreak(label = "Hotbar Slots")
    @Toggle(label = "Hotbar Slot 1")
    public boolean hotbarSlot1 = false;

    @Toggle(label = "Hotbar Slot 2")
    public boolean hotbarSlot2 = false;

    @Toggle(label = "Hotbar Slot 3")
    public boolean hotbarSlot3 = false;

    @Toggle(label = "Hotbar Slot 4")
    public boolean hotbarSlot4 = false;

    @Toggle(label = "Hotbar Slot 5")
    public boolean hotbarSlot5 = true;

    @Toggle(label = "Hotbar Slot 6")
    public boolean hotbarSlot6 = true;

    @Toggle(label = "Hotbar Slot 7")
    public boolean hotbarSlot7 = true;

    @Toggle(label = "Hotbar Slot 8")
    public boolean hotbarSlot8 = true;

    @Toggle(label = "Hotbar Slot 9")
    public boolean hotbarSlot9 = true;

    private static Schematica INSTANCE;

    public static com.github.lunatrius.schematica.Schematica SCHEMATICA_MOD_INSTANCE;

    private final ExecutorService workerThread;

    public ExecutorService getWorkerThread() {
        return this.workerThread;
    }

    private long lastClosedDispenser = -1L;

    private boolean accessing = false;

    public final List<MissingSchematicBlock> missingBlocks = new GlueList<>();

    public final Map<BlockPos, AxisAlignedBB> wrongMetaBlocks = new ConcurrentHashMap<>(), incorrectBlocks = new ConcurrentHashMap<>();

    public Schematica() {
        INSTANCE = this;
        SCHEMATICA_MOD_INSTANCE = new com.github.lunatrius.schematica.Schematica();
        SCHEMATICA_MOD_INSTANCE.init();
        this.canBeDisabled = false;
        this.workerThread = Executors.newSingleThreadExecutor();
        ConfigurationHandler.loadConfiguration();
        ClientCommandHandler.getInstance().registerCommand((ICommand) new CommandSchematicaReplace());
        ClientCommandHandler.getInstance().registerCommand((ICommand) new CommandSchematicUndo());
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Displayed ESP Limit", f -> this.schemEsp);
        setOptionVisibility("ESP Mode", f -> this.schemEsp);
    }

    private void onSchematicKeyPress(InputEvent.Key event) {
        if (event.isKeyDown() && this.mc.currentScreen == null)
            if (this.toggleTracers.isPressed() && ClientProxy.currentSchematic.schematic != null) {
                Client.sendMessage(getToggleMessage("Tracers", this.schemEsp = !this.schemEsp), true);
                Config.getInstance().saveModuleConfig(this);
            } else if (this.traceAll.isPressed() && ClientProxy.currentSchematic.schematic != null) {
                List<MissingSchematicBlock> missing = traceAllMaterials();
                this.missingBlocks.clear();
                this.missingBlocks.addAll(missing);
                if (missing.isEmpty()) {
                    Client.sendMessage("No missing blocks were found!", true);
                } else {
                    Client.sendMessage("Missing blocks are now being traced", true);
                }
            } else if (this.loadSchematic.isPressed()) {
                ScreenSchematicaBase.openGui(SchematicaGuiType.LOAD_SCHEMATIC);
            } else if (this.saveSchematic.isPressed()) {
                ScreenSchematicaBase.openGui(SchematicaGuiType.SAVE_SCHEMATIC);
            } else if (this.manipulateSchematic.isPressed()) {
                ScreenSchematicaBase.openGui(SchematicaGuiType.CONTROL_SCHEMATIC);
            } else if (this.layerInc.isPressed()) {
                SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
                if (schematic != null && schematic.isRenderingLayer) {
                    schematic.renderingLayer = MathHelper.clamp_int(schematic.renderingLayer + 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            } else if (this.layerDec.isPressed()) {
                SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
                if (schematic != null && schematic.isRenderingLayer) {
                    schematic.renderingLayer = MathHelper.clamp_int(schematic.renderingLayer - 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            } else if (this.layerToggle.isPressed()) {
                SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
                if (schematic != null) {
                    schematic.isRenderingLayer = !schematic.isRenderingLayer;
                    RenderSchematic.INSTANCE.refresh();
                }
            } else if (this.renderToggle.isPressed()) {
                SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
                if (schematic != null) {
                    schematic.isRendering = !schematic.isRendering;
                    RenderSchematic.INSTANCE.refresh();
                }
            } else if (this.printerToggle.isPressed()) {
                if (ClientProxy.currentSchematic.schematic != null) {
                    boolean printing = SchematicPrinter.INSTANCE.togglePrinting();
                    Client.sendMessage(getToggleMessage("Schematic Printer", printing), true);
                }
            } else if (this.blockBreakToggle.isPressed()) {
                if (ClientProxy.currentSchematic.schematic != null) {
                    boolean blockBreak = this.autoBreak = !this.autoBreak;
                    Client.sendMessage(getToggleMessage("Automatic Block Breaking", blockBreak), true);
                }
            } else if (this.autoBridgeToggle.isPressed()) {
                if (ClientProxy.currentSchematic.schematic != null) {
                    boolean bridge = this.autoBridge = !this.autoBridge;
                    Client.sendMessage(getToggleMessage("Automatic Bridging", bridge), true);
                }
            } else if (this.moveHere.isPressed()) {
                SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
                if (schematic != null) {
                    ClientProxy.moveSchematicToPlayer(schematic);
                    RenderSchematic.INSTANCE.refresh();
                }
            }
    }

    private void onRenderWorld(RenderWorldEvent.Post event) {
        if (!this.schemEsp || this.mc.thePlayer == null)
            return;
        boolean wasBlend = GL11.glGetBoolean(3042);
        boolean wasTex2d = GL11.glGetBoolean(3553);
        boolean wasDepthTest = GL11.glGetBoolean(2929);
        boolean wasLineSmooth = GL11.glGetBoolean(2848);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        try {
            int index = 0;
            for (MissingSchematicBlock block : this.missingBlocks) {
                if (index >= this.espLimit || this.accessing)
                    break;
                renderSchemEsp(block.getMcPos(), block.getAabb(), this.missingBlockColor, event.partialTicks);
                index++;
            }
            for (Map.Entry<BlockPos, AxisAlignedBB> entry : this.wrongMetaBlocks.entrySet()) {
                if (index >= this.espLimit || this.accessing)
                    break;
                renderSchemEsp(entry.getKey(), entry.getValue(), this.incorrectBlockMeta, event.partialTicks);
                index++;
            }
            for (Map.Entry<BlockPos, AxisAlignedBB> entry : this.incorrectBlocks.entrySet()) {
                if (index >= this.espLimit || this.accessing)
                    break;
                renderSchemEsp(entry.getKey(), entry.getValue(), this.incorrectBlock, event.partialTicks);
                index++;
            }
        } catch (ConcurrentModificationException concurrentModificationException) {
        }
        if (!wasBlend)
            GL11.glDisable(3042);
        if (wasTex2d)
            GL11.glEnable(3553);
        if (wasDepthTest)
            GL11.glEnable(2929);
        if (!wasLineSmooth)
            GL11.glDisable(2848);
        GL11.glDepthMask(true);
        GL11.glColor4d(255.0D, 255.0D, 255.0D, 255.0D);
        GL11.glPopMatrix();
    }

    private void onSchematicArrowKeyPress(InputEvent.Key event) {
        if (!this.arrowKeys || ClientProxy.currentSchematic.schematic == null || this.mc.currentScreen != null)
            return;
        EnumFacing facing = this.mc.thePlayer.getHorizontalFacing();
        boolean sneaking = this.mc.thePlayer.isSneaking();
        boolean changed = false;
        if (Keyboard.getEventKeyState())
            switch (Keyboard.getEventKey()) {
                case 200:
                    if (sneaking) {
                        ClientProxy.currentSchematic.schematic.position.y++;
                    } else {
                        switch (facing) {
                            case NORTH:
                                ClientProxy.currentSchematic.schematic.position.z--;
                                break;
                            case SOUTH:
                                ClientProxy.currentSchematic.schematic.position.z++;
                                break;
                            case EAST:
                                ClientProxy.currentSchematic.schematic.position.x++;
                                break;
                            case WEST:
                                ClientProxy.currentSchematic.schematic.position.x--;
                                break;
                        }
                    }
                    changed = true;
                    break;
                case 208:
                    if (sneaking) {
                        ClientProxy.currentSchematic.schematic.position.y--;
                    } else {
                        switch (facing) {
                            case NORTH:
                                ClientProxy.currentSchematic.schematic.position.z++;
                                break;
                            case SOUTH:
                                ClientProxy.currentSchematic.schematic.position.z--;
                                break;
                            case EAST:
                                ClientProxy.currentSchematic.schematic.position.x--;
                                break;
                            case WEST:
                                ClientProxy.currentSchematic.schematic.position.x++;
                                break;
                        }
                    }
                    changed = true;
                    break;
                case 203:
                    switch (facing) {
                        case NORTH:
                            ClientProxy.currentSchematic.schematic.position.x--;
                            break;
                        case SOUTH:
                            ClientProxy.currentSchematic.schematic.position.x++;
                            break;
                        case EAST:
                            ClientProxy.currentSchematic.schematic.position.z--;
                            break;
                        case WEST:
                            ClientProxy.currentSchematic.schematic.position.z++;
                            break;
                    }
                    changed = true;
                    break;
                case 205:
                    switch (facing) {
                        case NORTH:
                            ClientProxy.currentSchematic.schematic.position.x++;
                            break;
                        case SOUTH:
                            ClientProxy.currentSchematic.schematic.position.x--;
                            break;
                        case EAST:
                            ClientProxy.currentSchematic.schematic.position.z++;
                            break;
                        case WEST:
                            ClientProxy.currentSchematic.schematic.position.z--;
                            break;
                    }
                    changed = true;
                    break;
            }
        if (changed) {
            clearTracerLists();
            RenderSchematic.INSTANCE.refresh();
            SchematicPrinter.INSTANCE.refresh();
        }
    }

    public void onUpdate() {
        ConfigurationHandler.loadConfiguration();
    }

    public void enable() {
        super.enable();
        com.github.lunatrius.schematica.Schematica.proxy.registerEvents();
        ConfigurationHandler.loadConfiguration();
    }

    public void disable() {
        clearTracerLists();
        super.disable();
        com.github.lunatrius.schematica.Schematica.proxy.unregisterEvents();
    }

    private void renderSchemEsp(BlockPos pos, AxisAlignedBB aabb, ColorObject color, float partialTicks) {
        if (this.espMode.isSelected("Tracers")) {
            RenderUtils.drawTracer(Vec3d.getNormalizedFromBlockPos(pos), color, this.mc.thePlayer, partialTicks);
        }
        if (this.espMode.isSelected("ESP")) {
            RenderUtils.setGlColor((Color) color);
            RenderGlobal.drawSelectionBoundingBox(RenderUtils.normalize(aabb));
            RenderUtils.resetColor();
        }
    }

    public void addTracer(BlockPos pos, AxisAlignedBB aabb, RenderType type) {
        this.accessing = true;
        if (type == RenderType.INCORRECT_BLOCK) {
            this.incorrectBlocks.put(pos, aabb);
        } else if (type == RenderType.WRONG_META) {
            this.wrongMetaBlocks.put(pos, aabb);
        }
        this.accessing = false;
    }

    public void clearTracerLists() {
        this.accessing = true;
        this.missingBlocks.clear();
        this.wrongMetaBlocks.clear();
        this.incorrectBlocks.clear();
        this.accessing = false;
    }

    public List<MissingSchematicBlock> traceAllMaterials() {
        return traceAllMaterials(null);
    }

    public List<MissingSchematicBlock> traceAllMaterials(Block block) {
        GlueList<MissingSchematicBlock> glueList = new GlueList<>();
        SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
        if (schematic == null)
            return glueList;
        WorldClient worldClient = this.mc.theWorld;
        for (MBlockPos pos : BlockPosHelper.getAllInBox(BlockPos.ORIGIN, new BlockPos(schematic.getWidth() - 1, schematic.getHeight() - 1, schematic.getLength() - 1))) {
            if ((schematic.isRenderingLayer && schematic.renderingLayer != pos.getY()) || !schematic.isInside((BlockPos) pos))
                continue;
            IBlockState schBlockState = schematic.getBlockState((BlockPos) pos);
            Block schBlock = schBlockState.getBlock();
            MBlockPos mBlockPos1 = pos.add(schematic.position);
            if (!worldClient.getChunkFromBlockCoords(mBlockPos1).isLoaded())
                continue;
            boolean isSchAirBlock = schematic.isAirBlock(pos);
            boolean isMcAirBlock = worldClient.isAirBlock(mBlockPos1);
            if (!isSchAirBlock && isMcAirBlock && (block == null || block == schBlock)) {
                MissingSchematicBlock missingBlock;
                try {
                    // schBlock.func_180654_a((IBlockAccess)schematic, (BlockPos)pos);
                    schBlock.setBlockBoundsBasedOnState(schematic, pos);
                    missingBlock = new MissingSchematicBlock(mBlockPos1, pos, schBlock.getCollisionBoundingBox(worldClient, mBlockPos1, null).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D), schBlockState);
                } catch (Exception ex) {
                    missingBlock = new MissingSchematicBlock(mBlockPos1, pos, new AxisAlignedBB(mBlockPos1, mBlockPos1.add(1, 1, 1)), schBlockState);
                }
                glueList.add(missingBlock);
            }
        }
        return glueList;
    }

    public static Schematica getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, InputEvent.Key.class, this::onSchematicArrowKeyPress);
        EventBus.register(this, InputEvent.Key.class, this::onSchematicKeyPress);
        EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            com.github.lunatrius.schematica.Schematica.proxy.onClientTick();
            try {
                this.accessing = true;
                if (!this.missingBlocks.isEmpty())
                    if (mc.theWorld != null) {
                        this.missingBlocks.removeIf(a -> BlockStateHelper.areBlockStatesEqual(mc.theWorld.getBlockState(a.getMcPos()), a.getSchBlockState()));
                    }
                this.accessing = false;
            } catch (ConcurrentModificationException ex) {
                Reference.LOGGER.error("Unable to remove items from array", ex);
            }
        });
        EventBus.register(this, ConfigEvent.ModuleSave.Post.class, ev -> {
            if (ev.getModule() instanceof Schematica)
                ConfigurationHandler.loadConfiguration();
        });
        EventBus.register(this, GuiScreenEvent.Post.class, ev -> {
            if (SchematicPrinter.INSTANCE.isPrinting() && ev.gui instanceof net.minecraft.client.gui.inventory.GuiDispenser && !this.openDispensersWhilePrinting) {
                this.mc.thePlayer.closeScreen();
                long currentMs = System.currentTimeMillis();
                if (currentMs - this.lastClosedDispenser > 60000L)
                    NotificationHandler.addNotification("Schematica Module", "The dispenser you've opened has been automatically closed by the Schematica module due to you having printer mode enabled and the toggle 'Open Dispensers While Printing' is disabled.", 15000L);
                this.lastClosedDispenser = currentMs;
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\factions\Schematica.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */