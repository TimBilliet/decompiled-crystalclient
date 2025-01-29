package co.crystaldev.client.feature.impl.factions;


import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.network.socket.client.group.PacketGroupChat;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.Vec3d;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;
import net.minecraftforge.fluids.BlockFluidBase;
import org.lwjgl.opengl.GL11;


@ModuleInfo(name = "Float Finder", description = "Finds the float coordinates from a specified float block. /float for more.", category = Category.FACTIONS)
public class FloatFinder extends Module implements IRegistrable {

    @Keybind(label = "Select barrel block")
    public KeyBinding selectAdjustBlock = new KeyBinding("crystalclient.key.select_barrel_block", 0, "Crystal Client - Float Finder");

    @HoverOverlay({"Required for flipping barrels"})
    @Keybind(label = "Select power block")
    public KeyBinding selectPowerBlock = new KeyBinding("crystalclient.key.select_power_block", 0, "Crystal Client - Float Finder");

    @Keybind(label = "Calculate float location")
    public KeyBinding calcFloatLocation = new KeyBinding("crystalclient.key.calc_float_location", 0, "Crystal Client - Float Finder");

    @Toggle(label = "Recalculate on change")
    public boolean recalcOnChange = false;

    @Slider(label = "Checking interval", placeholder = "{value}ms", minimum = 50.0D, maximum = 1000.0D, standard = 300.0D, integers = true)
    public int checkInterval = 300;

    @HoverOverlay({"Maximum amount of blocks to check sideways"})
    @Slider(label = "Max sideways blocks", placeholder = "{value} blocks", minimum = 10.0D, maximum = 600.0D, standard = 250, integers = true)
    public int checkBlocksSideways = 250;

    @PageBreak(label = "Visuals Customization")
    @Colour(label = "Line Color")
    public ColorObject lineColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryRed, 255));

    @Colour(label = "Barrel block Color")
    public ColorObject barrelBlockColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).mainColor, 180));

    @Colour(label = "Power block Color")
    public ColorObject powerBlockColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).mainRed, 180));

    @Colour(label = "End block Color")
    public ColorObject endBlockColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryColor, 180));

    @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int lineWidth = 3;

    public BlockPos barrelBlockPos;
    private BlockPos barrelNextBlockPos;
    public BlockPos horizontal;
    public BlockPos vertical;
    public BlockPos powerBlockPos;
    private boolean calledFromKeybind = false;
    private EnumFacing barrelDirection;
    private static long lastExecutionTime = System.currentTimeMillis();
    private BlockPos previousFloat = new BlockPos(0, 0, 0);
    private static FloatFinder INSTANCE;
    public String shootDirection;
    private boolean incorrectBarrelDirSent = false;
    private boolean incorrectVerticalSent = false;
    private boolean incorrectHorizontalSent = false;

    public FloatFinder() {
        this.enabled = false;
        INSTANCE = this;
    }

    public static FloatFinder getInstance() {
        return INSTANCE;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Checking interval", f -> this.recalcOnChange);
    }

    private BlockPos verticalScan() {
        BlockPos top = null;
        for (int i = 1; i < 255; i++) {
            BlockPos currentAbove = barrelBlockPos.add(0, i, 0);
            BlockPos curentNextAbove = barrelNextBlockPos.add(0, i, 0);
            if (cantPassThroughBlock(currentAbove, false) || cantPassThroughBlock(curentNextAbove, false)) {
                top = currentAbove.add(0, -1, 0);
                break;
            }
        }
        return top;
    }

    private BlockPos horizontalScanIteration(int x, int z) {
        for (int i = 1; i < checkBlocksSideways; i++) {
            BlockPos currentNext = vertical.add(i * x, 0, i * z);
            if (cantPassThroughBlock(currentNext, true)) {
                return currentNext.add(-x, 0, -z);
            }
        }
        return null;
    }

    private BlockPos horizontalScan() {
        BlockPos side = null;
        if (barrelDirection == EnumFacing.WEST) {
            side = (powerBlockPos.getX() > barrelBlockPos.getX()) ? horizontalScanIteration(-1, 0) : horizontalScanIteration(1, 0);
        } else if (barrelDirection == EnumFacing.EAST) {
            side = powerBlockPos.getX() < barrelBlockPos.getX() ? horizontalScanIteration(1, 0) : horizontalScanIteration(-1, 0);
        } else if (barrelDirection == EnumFacing.SOUTH) {
            side = (powerBlockPos.getZ() < barrelBlockPos.getZ()) ? horizontalScanIteration(0, 1) : horizontalScanIteration(0, -1);
        } else if (barrelDirection == EnumFacing.NORTH) {
            side = (powerBlockPos.getZ() > barrelBlockPos.getZ()) ? horizontalScanIteration(0, -1) : horizontalScanIteration(0, 1);
        }
        return side;
    }

    private EnumFacing detectDirection() {
        EnumFacing direction = null;
        if (mc.theWorld != null) {
            IBlockState state = mc.theWorld.getBlockState(barrelBlockPos);
            Block block = state.getBlock();
            if (block instanceof BlockStairs) {
                direction = state.getValue(BlockStairs.FACING);
            } else if (block instanceof BlockTrapDoor && state.getValue(BlockTrapDoor.OPEN)) {
                EnumFacing dir = state.getValue(BlockTrapDoor.FACING);
                if (dir == EnumFacing.WEST) {
                    direction = EnumFacing.EAST;
                } else if (dir == EnumFacing.EAST) {
                    direction = EnumFacing.WEST;
                } else if (dir == EnumFacing.NORTH) {
                    direction = EnumFacing.SOUTH;
                } else {
                    direction = EnumFacing.NORTH;
                }
            }
        }
        return direction;
    }

    private void findFloat() {
        barrelDirection = detectDirection();
        if (barrelDirection == null) {
            vertical = null;
            horizontal = null;
            if (GroupOptions.getInstance().sharedFloatPos && !incorrectBarrelDirSent) {
                Client.sendPacket(new PacketGroupChat("ZQX_D"));
                incorrectBarrelDirSent = true;
            }
            if (calledFromKeybind)
                Client.sendMessage("&fInvalid barrel block or state", true);
            previousFloat = null;
            return;
        }
        barrelNextBlockPos = determinePosNextToBarrel();
        vertical = verticalScan();
        if (vertical == null) {
            if (GroupOptions.getInstance().sharedFloatPos && !incorrectVerticalSent) {
                Client.sendPacket(new PacketGroupChat("ZQX_V"));
                incorrectVerticalSent = true;
            }
            if (calledFromKeybind)
                Client.sendMessage("&fCould not find a top block", true);
            previousFloat = null;
            return;
        }
        horizontal = horizontalScan();
        if (horizontal == null) {
            if (GroupOptions.getInstance().sharedFloatPos && !incorrectHorizontalSent) {
                Client.sendPacket(new PacketGroupChat("ZQX_H"));
                incorrectHorizontalSent = true;
            }
            if (calledFromKeybind)
                Client.sendMessage("&fCould not find a side block", true);
            previousFloat = null;
            return;
        }
        if (calledFromKeybind || previousFloat == null || !previousFloat.equals(horizontal)) {
            Client.sendMessage(String.format("&fFloat position set to &bx%s y%s z%s.", horizontal.getX(), horizontal.getY(), horizontal.getZ()), true);
            if (GroupOptions.getInstance().sharedFloatPos) {
                Client.sendPacket(new PacketGroupChat(String.format("ZQX_%s_%s_%s_%s_%s_%s_%s", horizontal.getX(), horizontal.getY(), horizontal.getZ(), barrelBlockPos.getX(), barrelBlockPos.getY(), barrelBlockPos.getZ(), shootDirection)));
            }
            //Client.sendPacket(new PacketFloatFinder());
        }
        incorrectVerticalSent = false;
        incorrectHorizontalSent = false;
        incorrectBarrelDirSent = false;
        previousFloat = horizontal;
    }

    private boolean cantPassThroughBlock(BlockPos pos, boolean checkingHorizontal) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (block instanceof BlockFluidBase || block instanceof BlockWeb || block instanceof BlockAir || block.isReplaceable(mc.theWorld, pos) || (block.isPassable(mc.theWorld, pos) && !(block instanceof BlockTrapDoor)))
            return false;
        if (!(block instanceof BlockTrapDoor)) {
            return true;
        }
        try {
            IBlockState trapdoorState = mc.theWorld.getBlockState(pos);
            int metadata = block.getMetaFromState(trapdoorState);
            if (!checkingHorizontal && (metadata & 8) != 0) {
                Client.sendErrorMessage("Trapdoor is at the top of a block, you might blow up your cannon!", true);
                return true;
            }
            if ((metadata & 4) == 0) {
                return true;
            }
            return block.getMetaFromState(trapdoorState) != 12 && block.getMetaFromState(trapdoorState) != 13 && block.getMetaFromState(trapdoorState) != 14 && block.getMetaFromState(trapdoorState) != 15 && block.getMetaFromState(trapdoorState) != 4 && block.getMetaFromState(trapdoorState) != 5 && block.getMetaFromState(trapdoorState) != 6 && block.getMetaFromState(trapdoorState) != 7;

        } catch (Throwable ignored) {

        }
        return true;
    }

    private BlockPos determinePosNextToBarrel() {
        switch (barrelDirection) {
            case WEST:
                shootDirection = "NORTH/SOUTH";
                return barrelBlockPos.east();
            case EAST:
                shootDirection = "NORTH/SOUTH";
                return barrelBlockPos.west();
            case NORTH:
                shootDirection = "EAST/WEST";
                return barrelBlockPos.south();
            case SOUTH:
                shootDirection = "EAST/WEST";
                return barrelBlockPos.north();
        }
        return null;
    }

    private void onRenderWorld(RenderWorldEvent.Post event) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        if (barrelBlockPos != null) {
            GL11.glPushMatrix();
            if (barrelBlockColor.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor(barrelBlockColor);
            AxisAlignedBB bb = RenderUtils.normalize(RenderUtils.posToAABB(barrelBlockPos));
            RenderUtils.drawFilledBoundingBox(bb.expand(0.015D, 0.015D, 0.015D));
            ShaderManager.getInstance().disableShader();
            GL11.glPopMatrix();
        }
        if (powerBlockPos != null) {
            GL11.glPushMatrix();
            if (powerBlockColor.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor(powerBlockColor);
            AxisAlignedBB bb = RenderUtils.normalize(RenderUtils.posToAABB(powerBlockPos));
            RenderUtils.drawFilledBoundingBox(bb.expand(0.015D, 0.015D, 0.015D));
            ShaderManager.getInstance().disableShader();
            GL11.glPopMatrix();
        }
        if (horizontal != null && vertical != null && barrelBlockPos != null) {
            GL11.glPushMatrix();
            if (endBlockColor.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor(endBlockColor);
            AxisAlignedBB bb = RenderUtils.normalize(RenderUtils.posToAABB(horizontal));
            RenderUtils.drawFilledBoundingBox(bb.expand(0.015D, 0.015D, 0.015D));
            ShaderManager.getInstance().disableShader();
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glEnable(2848);
            GL11.glLineWidth(lineWidth);
            GL11.glDepthMask(true);
            if (lineColor.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor(lineColor);
            Vec3d barrel = RenderUtils.normalize(new Vec3d(barrelBlockPos.getX() + 0.5D, barrelBlockPos.getY() + 1.0D, barrelBlockPos.getZ() + 0.5D));
            Vec3d top = RenderUtils.normalize(new Vec3d(vertical.getX() + 0.5D, vertical.getY() + 0.5D, vertical.getZ() + 0.5D));
            Vec3d side = RenderUtils.normalize(new Vec3d(horizontal.getX() + 0.5D, horizontal.getY() + 0.5D, horizontal.getZ() + 0.5D));
            RenderUtils.drawLine(barrel, top);
            RenderUtils.drawLine(top, side);
            ShaderManager.getInstance().disableShader();
            GL11.glDisable(2848);
            GL11.glPopMatrix();
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0F);
        GL11.glPopMatrix();
    }

    public void findFloatOnce() {
        if (barrelBlockPos != null) {
            if (powerBlockPos != null) {
                Client.sendMessage("&fFinding float...", true);
                calledFromKeybind = true;
                findFloat();
            } else {
                Client.sendMessage("&fYou must set a power block first!", true);
            }
        } else {
            Client.sendMessage("&fYou must set a barrel block first!", true);
        }
    }

    public void selectBarrelBlock() {
        if (mc.thePlayer == null)
            return;
        barrelBlockPos = mc.objectMouseOver.getBlockPos();
        horizontal = null;
        vertical = null;
        Client.sendMessage(String.format("&fBarrel position set to &bx%s y%s z%s.", barrelBlockPos.getX(), barrelBlockPos.getY(), barrelBlockPos.getZ()), true);
    }

    public void selectPowerBlock() {
        if (mc.thePlayer == null)
            return;
        powerBlockPos = mc.objectMouseOver.getBlockPos().up();
        horizontal = null;
        vertical = null;
        Client.sendMessage(String.format("&fPower position set to &bx%s y%s z%s.", powerBlockPos.getX(), powerBlockPos.getY(), powerBlockPos.getZ()), true);
    }

    private void onKeyInput(InputEvent.Key event) {
        if (selectAdjustBlock.isPressed()) {
            selectBarrelBlock();
        } else if (calcFloatLocation.isPressed()) {
            findFloatOnce();
        } else if (selectPowerBlock.isPressed()) {
            selectPowerBlock();
        }
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
        EventBus.register(this, InputEvent.Key.class, this::onKeyInput);
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (recalcOnChange && barrelBlockPos != null && powerBlockPos != null) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastExecutionTime > checkInterval) {
                    lastExecutionTime = currentTime;
                    try {
                        calledFromKeybind = false;
                        findFloat();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
