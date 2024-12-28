package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.screens.ScreenAdjHelper;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketAdjHelper;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.Adjust;
import co.crystaldev.client.util.objects.Vec3d;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "Adjust Helper", description = "Checks for the best adjust coordinates in a defined area. /adj for more", category = Category.FACTIONS)
public class AdjustHelper extends Module implements IRegistrable {
    @Keybind(label = "GUI Bind")
    public KeyBinding guiBind = new KeyBinding("crystalclient.key.open_adjust_helper_gui", 0, "Crystal Client - Adjust Helper");

    @Keybind(label = "Next Adjust")
    public KeyBinding nextAdjust = new KeyBinding("crystalclient.key.next_adjust", 0, "Crystal Client - Adjust Helper");

    @Keybind(label = "Send Coordinates Bind")
    public KeyBinding coordinatesBind = new KeyBinding("crystalclient.key.shout_current_adjust", 0, "Crystal Client - Adjust Helper");

    @PageBreak(label = "Scan")
    @Toggle(label = "Use Even Y-Levels")
    public boolean evenYLevels = true;

    @Toggle(label = "Display Sticky Indicator")
    public boolean displayStickyIndicator = true;

    @Selector(label = "Stepwise Regression", values = {"Start from top, move downward", "Start from bottom, move upward"})
    public String stepwiseRegression = "Start from top, move downward";

    @Selector(label = "Sorting Method", values = {"Height", "Patches : Distance"})
    public String sortingMethod = "Patches : Distance";

    @Slider(label = "Maximum Y-Level", placeholder = "Y = {value}", minimum = 1.0D, maximum = 255.0D, standard = 254.0D, integers = true)
    public int maxY = 254;

    @Slider(label = "Minimum Y-Level", placeholder = "Y = {value}", minimum = 1.0D, maximum = 255.0D, standard = 200.0D, integers = true)
    public int minY = 200;

    @Slider(label = "Maximum Distance", placeholder = "{value} blocks", minimum = 150.0D, maximum = 500.0D, standard = 320.0D, integers = true)
    public int distance = 320;

    @Slider(label = "Required Gap Size", placeholder = "{value} blocks", minimum = 1.0D, maximum = 6.0D, standard = 2.0D, integers = true)
    public int requiredGap = 2;

    @Slider(label = "Allowed Patches", placeholder = "{value} patches", minimum = 1.0D, maximum = 30.0D, standard = 5.0D, integers = true)
    public int allowedPatches = 5;

    @PageBreak(label = "Adjust Customization")
    @Colour(label = "Pos1 Color")
    public ColorObject pos1Color = new ColorObject(255, 85, 255, 255);

    @Colour(label = "Pos2 Color")
    public ColorObject pos2Color = new ColorObject(85, 255, 85, 255);

    @Colour(label = "Indicator Color")
    public ColorObject indicatorColor = new ColorObject(255, 255, 255, 255);

    @Selector(label = "Indicator Mode", values = {"Box", "Line"})
    public String indicatorMode = "Box";

    @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int lineWidth = 3;

    private static AdjustHelper INSTANCE;
    private static final char LINE = '|';
    //private static final char LINE = '┃';

    public BlockPos pos1 = new BlockPos(0, 0, 0);

    public BlockPos pos2 = new BlockPos(0, 0, 0);

    public Adjust currentAdjust;

    private List<Adjust> bestAdjusts = new ArrayList<>();

    public List<Adjust> getBestAdjusts() {
        return this.bestAdjusts;
    }

    private final BlockPos zero = new BlockPos(0, 0, 0);

    public AdjustHelper() {
        INSTANCE = this;
        this.enabled = false;
    }

    private void onRenderWorld(RenderWorldEvent.Post event) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        if (this.pos1 != null && this.pos1 != this.zero) {
            GL11.glPushMatrix();
            if (this.pos1Color.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor((Color) this.pos1Color);
            AxisAlignedBB bb = RenderUtils.normalize(RenderUtils.posToAABB(this.pos1));
            RenderUtils.drawFilledBoundingBox(bb.expand(0.015D, 0.015D, 0.015D));
            ShaderManager.getInstance().disableShader();
            GL11.glPopMatrix();
        }
        if (this.pos2 != null && this.pos2 != this.zero) {
            GL11.glPushMatrix();
            if (this.pos2Color.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor((Color) this.pos2Color);
            AxisAlignedBB bb = RenderUtils.normalize(RenderUtils.posToAABB(this.pos2));
            RenderUtils.drawFilledBoundingBox(bb.expand(0.015D, 0.015D, 0.015D));
            ShaderManager.getInstance().disableShader();
            GL11.glPopMatrix();
        }
        if (this.currentAdjust != null) {
            GL11.glPushMatrix();
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glLineWidth(this.lineWidth);
            if (this.indicatorColor.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor((Color) this.indicatorColor);
            if (this.indicatorMode.equals("Line")) {
                Vec3d start = RenderUtils.normalize(new Vec3d(this.currentAdjust.origin
                        .getX() + 0.5D, this.currentAdjust.origin
                        .getY() + 0.5D, this.currentAdjust.origin
                        .getZ() + 0.5D));
                Vec3d end = RenderUtils.normalize(new Vec3d(this.currentAdjust.finish
                        .getX() + 0.5D, this.currentAdjust.finish
                        .getY() + 0.5D, this.currentAdjust.finish
                        .getZ() + 0.5D));
                RenderUtils.drawLine(start, end);
            } else {
                AxisAlignedBB bb = new AxisAlignedBB(this.currentAdjust.origin, this.currentAdjust.finish.add(1, 1, 1));
                RenderGlobal.drawSelectionBoundingBox(RenderUtils.normalize(bb));
            }
            if (this.displayStickyIndicator) {
                AxisAlignedBB small;
                if (this.currentAdjust.origin.getX() - this.currentAdjust.finish.getX() == 0) {
                    small = new AxisAlignedBB(this.currentAdjust.origin.getX() + 0.5D - 0.1D, this.currentAdjust.origin.getY() + 0.5D - 0.1D, MathHelper.floor_double(this.mc.thePlayer.posZ) + 0.5D - 0.1D, this.currentAdjust.origin.getX() + 0.5D + 0.1D, this.currentAdjust.origin.getY() + 0.5D + 0.1D, MathHelper.floor_double(this.mc.thePlayer.posZ) + 0.5D + 0.1D);
                } else {
                    small = new AxisAlignedBB(MathHelper.floor_double(this.mc.thePlayer.posX) + 0.5D - 0.1D, this.currentAdjust.origin.getY() + 0.5D - 0.1D, this.currentAdjust.origin.getZ() + 0.5D - 0.1D, MathHelper.floor_double(this.mc.thePlayer.posX) + 0.5D + 0.1D, this.currentAdjust.origin.getY() + 0.5D + 0.1D, this.currentAdjust.origin.getZ() + 0.5D + 0.1D);
                }
                RenderGlobal.drawSelectionBoundingBox(RenderUtils.normalize(small));
            }
            GL11.glDisable(2848);
            GL11.glEnable(2929);
            ShaderManager.getInstance().disableShader();
            GL11.glPopMatrix();
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0F);
        GL11.glPopMatrix();
    }

    private void onKeyInput(InputEvent.Key event) {
        if (this.guiBind.isPressed())
            this.mc.displayGuiScreen((GuiScreen) new ScreenAdjHelper());
        if (this.nextAdjust.isPressed()) {
            if (this.mc.thePlayer == null)
                return;
            if (this.bestAdjusts.isEmpty() || this.currentAdjust == null) {
                Client.sendMessage("&fThere are no current adjusts.", true);
                return;
            }
            int index = this.bestAdjusts.indexOf(this.currentAdjust);
            if (index == -1) {
                Client.sendMessage("&fThere are no current adjusts.", true);
                return;
            }
            if (index == this.bestAdjusts.size() - 1) {
                index = 0;
            } else {
                index++;
            }
            this.currentAdjust = this.bestAdjusts.get(index);
            int zDiff = this.currentAdjust.origin.getZ() - this.currentAdjust.finish.getZ();
            String distance = String.format("Distance: %.2fc", Math.sqrt(this.currentAdjust.origin.distanceSq((Vec3i) this.currentAdjust.finish)) / 16.0D);
            String text = ((zDiff == 0) ? ("&bz:" + this.currentAdjust.origin.getZ()) : ("&bx:" + this.currentAdjust.origin.getX())) + String.format(", y:%d &7(Patches: %d %c %s)", Integer.valueOf(this.currentAdjust.origin.getY()), Integer.valueOf(this.currentAdjust.patches), Character.valueOf('|'), distance);
            Client.sendMessage(String.format("&fAdjust %d/%d: %s", index + 1, this.bestAdjusts.size(), text), true);
        }
        if (this.coordinatesBind.isPressed()) {
            if (this.mc.thePlayer == null)
                return;
            if (this.currentAdjust != null) {
                this.mc.thePlayer.sendChatMessage(this.currentAdjust.coordText);
            } else {
                Client.sendMessage("&fCould not find the current adjust.", true);
            }
        }
    }

    public void disable() {
        this.pos1 = this.zero;
        this.pos2 = this.zero;
        this.currentAdjust = null;
        super.disable();
    }

    public void scan(EnumFacing dir) {
        BlockPos first;
        if (this.pos1 == null || this.pos2 == null || this.pos1 == this.zero || this.pos2 == this.zero) {
            Client.sendMessage("&cYou need to define scan boundaries first. Type /adj for details.", true);
            return;
        }
        Client.sendMessage("&fInitializing scan...", true);
        this.bestAdjusts.clear();
        Vec3i scanVec = dir.getDirectionVec();
        if (getScanDirection(dir).equals(EnumFacing.EAST.getDirectionVec())) {
            first = (this.pos1.getX() < this.pos2.getX()) ? this.pos1 : this.pos2;
        } else {
            first = (this.pos1.getZ() < this.pos2.getZ()) ? this.pos1 : this.pos2;
        }
        BlockPos second = first.equals(this.pos1) ? this.pos2 : this.pos1;
        switch (dir) {
            case NORTH:
                first = new BlockPos(first.getX(), first.getY(), Math.max(first.getZ(), second.getZ()));
                second = new BlockPos(second.getX(), second.getY(), Math.max(first.getZ(), second.getZ()));
                break;

            case SOUTH:
                first = new BlockPos(first.getX(), first.getY(), Math.min(first.getZ(), second.getZ()));
                second = new BlockPos(second.getX(), second.getY(), Math.min(first.getZ(), second.getZ()));
                break;

            case WEST:
                first = new BlockPos(Math.max(first.getX(), second.getX()), first.getY(), first.getZ());
                second = new BlockPos(Math.max(first.getX(), second.getX()), second.getY(), second.getZ());
                break;

            case EAST:
                first = new BlockPos(Math.min(first.getX(), second.getX()), first.getY(), first.getZ());
                second = new BlockPos(Math.min(first.getX(), second.getX()), second.getY(), second.getZ());
                break;
        }

        for (BlockPos pos : Lists.newArrayList(BlockPos.getAllInBox(new BlockPos(first.getX(), this.minY, first.getZ()), new BlockPos(second
                .getX(), this.maxY, second.getZ())))) {

            if (pos.getY() % 2 != (this.evenYLevels ? 0 : 1)) {
                continue;
            }
            int blockCount = 0;
            boolean wasWall = false;
            BlockPos to = null;

            for (int i = 1; i < this.distance; i++) {
                BlockPos scannedBlock = pos.add(scanVec.getX() * i, 0, scanVec.getZ() * i);

                if (blockCount >= this.allowedPatches) {
                    if (to == null) to = scannedBlock;

                } else {

                    boolean hasPatch = blockIsWall(scannedBlock);
                    for (int j = 1; j < this.requiredGap; j++) {
                        BlockPos downwardBlock = scannedBlock.down(j);
                        if (blockIsWall(downwardBlock)) {
                            hasPatch = true;
                        }
                    }

                    if (wasWall != hasPatch) {
                        wasWall = hasPatch;
                    } else {
                        to = scannedBlock;
                    }
                    if (hasPatch) {
                        blockCount++;
                    }
                    if (to == null)
                        to = scannedBlock;
                }
            }
            int patches = 0;
            for (BlockPos pos1 : BlockPos.getAllInBox(pos, to)) {
                if (blockIsWall(pos1)) {
                    patches++;
                    continue;
                }
                for (int j = 1; j < this.requiredGap; j++) {
                    BlockPos downwardBlock = pos1.down(j);
                    if (blockIsWall(downwardBlock)) {
                        patches++;
                        break;
                    }
                }
            }
            double patchIndex = patches / pos.distanceSq((Vec3i) to) + 1.0D;

            String distance = String.format("Distance: %.2fc", Math.sqrt(pos.distanceSq((Vec3i) to)) / 16.0D);
            this.bestAdjusts.add(new Adjust(pos, to, patches, patchIndex, ((scanVec.getX() == 1) ? ("z:" + pos.getZ()) : ("x:" + pos.getX())) +
                    String.format(", y:%d (Patches: %d | %s)", pos.getY(), patches, distance)));
        }

        if (this.bestAdjusts.size() > 0) {
            if (this.stepwiseRegression.equals("Start from top, move downward")) {
                this.bestAdjusts.sort(Comparator.comparingDouble(adj -> adj.origin.distanceSq((Vec3i) adj.finish)));
                this.bestAdjusts = Lists.reverse(this.bestAdjusts).subList(0, Math.min(this.bestAdjusts.size() - 1, 10));
            } else {

                this.bestAdjusts = this.bestAdjusts.subList(0, Math.min(this.bestAdjusts.size() - 1, 10));
            }
            if (this.sortingMethod.equals("Patches : Distance")) {
                this.bestAdjusts.sort(Comparator.comparingDouble(adj -> adj.patchIndex / adj.origin.distanceSq((Vec3i) adj.finish)));
            } else {

                this.bestAdjusts.sort(Comparator.comparingDouble(adj -> adj.origin.getY()));
                this.bestAdjusts = Lists.reverse(this.bestAdjusts);
            }


            this.bestAdjusts.removeIf(adj -> {
                double distance = adj.origin.distanceSq((Vec3i) adj.finish);
                return (distance <= 2.0D && adj.patches == 1);
            });

            if (this.bestAdjusts.isEmpty()) {
                Client.sendMessage("&fAn error occurred while scanning (no adjusts found)", true);

                return;
            }
            this.currentAdjust = this.bestAdjusts.get(0);

            String distance = String.format("Distance: %.2fc", Math.sqrt(this.currentAdjust.origin.distanceSq((Vec3i) this.currentAdjust.finish)) / 16.0D);

            String text = ((scanVec.getX() == 1) ? ("&bz:" + this.currentAdjust.origin.getZ()) : ("&bx:" + this.currentAdjust.origin.getX())) + String.format(", y:%d &7(Patches: %d %c %s)", new Object[]{Integer.valueOf(this.currentAdjust.origin.getY()), Integer.valueOf(this.currentAdjust.patches), Character.valueOf('|'), distance});

            Client.sendMessage(String.format("&fAdjust 1/%d: %s", this.bestAdjusts.size(), text), true);

            Client.sendPacket((Packet) new PacketAdjHelper());
        } else {

            Client.sendMessage("&fAn error occurred while scanning (no adjusts found)", true);
        }
    }


    private Vec3i getScanDirection(EnumFacing dir) {
        if (dir.equals(EnumFacing.NORTH) || dir.equals(EnumFacing.SOUTH)) {
            return EnumFacing.EAST.getDirectionVec();
        }
        return EnumFacing.SOUTH.getDirectionVec();
    }

    private boolean blockIsWall(BlockPos pos) {
        Block blockType = blockAtPos(pos);
        return (blockType.getMaterial() != Material.water && blockType
                .getMaterial() != Material.air);
    }

    private Block blockAtPos(BlockPos pos) {
        return this.mc.theWorld.getBlockState(pos).getBlock();
    }

    public void setAdjusts(List<Adjust> adjusts) {
        if (adjusts.isEmpty()) {
            this.bestAdjusts.clear();
            this.currentAdjust = null;

            return;
        }
        this.bestAdjusts = adjusts;
        this.currentAdjust = adjusts.get(0);

        int zDiff = this.currentAdjust.origin.getZ() - this.currentAdjust.finish.getZ();
        String distance = String.format("Distance: %.2fc", new Object[]{Double.valueOf(Math.sqrt(this.currentAdjust.origin.distanceSq((Vec3i) this.currentAdjust.finish)) / 16.0D)});

        String text = ((zDiff == 0) ? ("&bz:" + this.currentAdjust.origin.getZ()) : ("&bx:" + this.currentAdjust.origin.getX())) + String.format(", y:%d &7(Patches: %d %c %s)", new Object[]{Integer.valueOf(this.currentAdjust.origin.getY()), Integer.valueOf(this.currentAdjust.patches), Character.valueOf('|'), distance});

        Client.sendMessage(String.format("&fAdjust 1/%d: %s", new Object[]{Integer.valueOf(this.bestAdjusts.size()), text}), true);
    }

    public static AdjustHelper getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
        EventBus.register(this, InputEvent.Key.class, this::onKeyInput);
    }
}