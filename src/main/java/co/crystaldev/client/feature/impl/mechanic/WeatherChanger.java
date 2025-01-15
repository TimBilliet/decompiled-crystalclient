package co.crystaldev.client.feature.impl.mechanic;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.DropdownMenu;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.mixin.accessor.net.minecraft.world.MixinWorld;
import co.crystaldev.client.util.ColorObject;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@ModuleInfo(name = "Weather Changer", description = "Forces the global weather to your selected weather", category = Category.MECHANIC)
public class WeatherChanger extends Module implements IRegistrable {
    @DropdownMenu(label = "State", values = {"Rain", "Storming", "Snow", "Clear"}, defaultValues = {"Clear"})
    public Dropdown<String> state;

    @Slider(label = "Weather Modifier", placeholder = "{value}x", minimum = 0.1D, maximum = 1.0D, standard = 1.0D)
    public double weatherModifier = 1.0D;

    @Colour(label = "Color Modifier")
    public ColorObject colorModifier = new ColorObject(255, 255, 255, 255);

    private static WeatherChanger INSTANCE;

    private int updateLCG = (new Random()).nextInt();

    public WeatherChanger() {
        INSTANCE = this;
        this.enabled = false;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Weather Modifier", f -> (getState() != State.CLEAR));
        setOptionVisibility("Color Modifier", f -> (getState() != State.CLEAR));
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        if (this.mc.theWorld != null && this.mc.theWorld.isRemote) {
            WorldClient worldClient = this.mc.theWorld;
            if (((World) worldClient).rand.nextInt(100000) == 0 && getState() == State.STORMING) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int i1 = this.updateLCG >> 2;
                for (ChunkCoordIntPair chunkcoordintpair : ((MixinWorld) worldClient).getActiveChunkSet()) {
                    int k = chunkcoordintpair.chunkXPos * 16;
                    int l = chunkcoordintpair.chunkZPos * 16;
                    BlockPos blockpos = adjustPosToNearbyEntity(new BlockPos(k + (i1 & 0xF), 0, l + (i1 >> 8 & 0xF)));
                    if (worldClient.isRainingAt(blockpos))
                        worldClient.addWeatherEffect(new EntityLightningBolt((World) worldClient, blockpos.getX(), blockpos.getY(), blockpos.getZ()));
                }
            }
        }
    }

    protected BlockPos adjustPosToNearbyEntity(BlockPos pos) {
        WorldClient worldClient = this.mc.theWorld;
        BlockPos blockpos = worldClient.getPrecipitationHeight(pos);
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), worldClient.getHeight(), blockpos.getZ()))).expand(3.0D, 3.0D, 3.0D);
        List<EntityLivingBase> list = worldClient.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, p_apply_1_ -> (p_apply_1_ != null && p_apply_1_.isEntityAlive() && worldClient.canSeeSky(p_apply_1_.getPosition())));
        return !list.isEmpty() ? ((EntityLivingBase) list.get(((World) worldClient).rand.nextInt(list.size()))).getPosition() : blockpos;
    }

    public State getState() {
        String state = this.state.getCurrentSelection();
        if (state != null)
            switch (state) {
                case "Rain":
                    return State.RAIN;
                case "Storming":
                    return State.STORMING;
                case "Snow":
                    return State.SNOW;
            }
        return State.CLEAR;
    }

    public static WeatherChanger getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, WorldEvent.Unload.class, ev -> {
            ev.world.setRainStrength(0.0F);
            ev.world.setThunderStrength(0.0F);
            ev.world.getWorldInfo().setRaining(false);
        });
        EventBus.register(this, ClientTickEvent.Pre.class, this::onClientTick);
    }

    public enum State {
        RAIN, STORMING, SNOW, CLEAR;
    }
}
