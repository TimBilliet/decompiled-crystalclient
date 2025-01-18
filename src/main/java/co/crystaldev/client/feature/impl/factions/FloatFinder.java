package co.crystaldev.client.feature.impl.factions;


import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.GuiOptions;
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

    @Keybind(label = "Select adjust block")
    public KeyBinding selectAdjustBlock = new KeyBinding("crystalclient.key.select_adjust_block", 0, "Crystal Client - Float Finder");

    @Keybind(label = "Calculate float location")
    public KeyBinding calcFloatLocation = new KeyBinding("crystalclient.key.calc_float_location", 0, "Crystal Client - Float Finder");

    @Toggle(label = "Recalculate on change")
    public boolean recalcOnChange = false;

    @Slider(label = "Checking interval", placeholder = "{value}ms", minimum = 50.0D, maximum = 1000.0D, standard = 200.0D, integers = true)
    public int checkInterval = 200;

    @PageBreak(label = "Visuals Customization")
    @Colour(label = "Line Color")
    public ColorObject lineColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryRed, 255));

    @Colour(label = "Stair block Color")
    public ColorObject barrelBlockColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).mainColor, 180));

    @Colour(label = "End block Color")
    public ColorObject endBlockColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryColor, 180));

    @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int lineWidth = 3;

    private BlockPos barrelBlockPos;
    private BlockPos barrelNextBlockPos;
    private BlockPos horizontal;
    private BlockPos vertical;
    private int checkAmountSideways = 300;
    private boolean calledFromKeybind = false;
    private EnumFacing barrelDirection;

    public FloatFinder() {
        this.enabled = false;

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

    private BlockPos horizontalScan() {
        BlockPos side = null;
        switch (barrelDirection) {
            case NORTH:
                for (int i = 1; i < checkAmountSideways; i++) {
                    BlockPos currentNext = vertical.add(0, 0, -i);
                    if (cantPassThroughBlock(currentNext, true)) {
                        side = currentNext.add(0, 0, 1);
                        break;
                    }
                }
                break;
            case SOUTH:
                for (int i = 1; i < checkAmountSideways; i++) {
                    BlockPos currentNext = vertical.add(0, 0, i);
                    if (cantPassThroughBlock(currentNext, true)) {
                        side = currentNext.add(0, 0, -1);
                        break;
                    }
                }
                break;
            case EAST:
                for (int i = 1; i < checkAmountSideways; i++) {
                    BlockPos currentNext = vertical.add(i, 0, 0);
                    if (cantPassThroughBlock(currentNext, true)) {
                        side = currentNext.add(-1, 0, 0);
                        break;
                    }
                }
                break;
            case WEST:
                for (int i = 1; i < checkAmountSideways; i++) {
                    BlockPos currentNext = vertical.add(-i, 0, 0);
                    if (cantPassThroughBlock(currentNext, true)) {
                        side = currentNext.add(1, 0, 0);
                        break;
                    }
                }
                break;
        }
        return side;
    }

    private EnumFacing detectDirection() {
        IBlockState state = mc.theWorld.getBlockState(barrelBlockPos);
        Block block = state.getBlock();
        EnumFacing direction = null;
        if (block instanceof BlockStairs) {
            direction = state.getValue(BlockStairs.FACING);
        } else if (true) {
            //TODO implement other barrels and flipped barrels
        }
        return direction;
    }

    private void findFloat() {
        barrelDirection = detectDirection();
        if (barrelDirection == null) {
            vertical = null;
            horizontal = null;
            if (calledFromKeybind)
                Client.sendMessage("&fInvalid barrel block", true);
            return;
        }
        barrelNextBlockPos = determinePosNextToBarrel();
        vertical = verticalScan();
        if (vertical == null) {
            if (calledFromKeybind)
                Client.sendMessage("&fCould not find a top block", true);
            return;
        }
        horizontal = horizontalScan();
        if (horizontal == null) {
            if (calledFromKeybind)
                Client.sendMessage("&fCould not find a side block", true);
            return;
        }
        Client.sendMessage(String.format("&fFloat position set to &bx%s y%s z%s.", horizontal.getX(), horizontal.getY(), horizontal.getZ()), true);
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
                return barrelBlockPos.east();
            case EAST:
                return barrelBlockPos.west();
            case NORTH:
                return barrelBlockPos.south();
            case SOUTH:
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
        if (horizontal != null && vertical != null) {
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

    private void onKeyInput(InputEvent.Key event) {
        if (selectAdjustBlock.isPressed()) {
            if (mc.thePlayer == null)
                return;
            barrelBlockPos = mc.objectMouseOver.getBlockPos();
            horizontal = null;
            vertical = null;
            Client.sendMessage(String.format("&fBarrel position set to &bx%s y%s z%s.", barrelBlockPos.getX(), barrelBlockPos.getY(), barrelBlockPos.getZ()), true);
        } else if (calcFloatLocation.isPressed()) {
            if (barrelBlockPos != null) {
                Client.sendMessage("&fFinding float...", true);
                calledFromKeybind = true;
                findFloat();
            } else {
                Client.sendMessage("&fYou need to set a barrel block first!", true);
            }
        }
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
        EventBus.register(this, InputEvent.Key.class, this::onKeyInput);
    }
}
