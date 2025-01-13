package com.github.lunatrius.schematica.client.renderer;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.MixinRenderGlobal;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.MixinViewFrustum;
import co.crystaldev.client.mixin.accessor.net.minecraft.util.MixinEnumFacing;
import co.crystaldev.client.util.TileEntityUtils;
import com.github.lunatrius.core.client.renderer.GeometryTessellator;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.core.util.vector.Vector3d;
import com.github.lunatrius.schematica.client.renderer.chunk.OverlayRenderDispatcher;
import com.github.lunatrius.schematica.client.renderer.chunk.container.SchematicChunkRenderContainer;
import com.github.lunatrius.schematica.client.renderer.chunk.container.SchematicChunkRenderContainerList;
import com.github.lunatrius.schematica.client.renderer.chunk.container.SchematicChunkRenderContainerVbo;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.ISchematicRenderChunkFactory;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlayList;
import com.github.lunatrius.schematica.client.renderer.chunk.proxy.SchematicRenderChunkList;
import com.github.lunatrius.schematica.client.renderer.chunk.proxy.SchematicRenderChunkVbo;
import com.github.lunatrius.schematica.client.renderer.shader.ShaderProgram;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public class RenderSchematic extends RenderGlobal implements IRegistrable {
    public static final RenderSchematic INSTANCE = new RenderSchematic(Minecraft.getMinecraft());

    public static final int RENDER_DISTANCE = 32;

    public static final int CHUNKS_XZ = 66;

    public static final int CHUNKS_Y = 16;

    public static final int CHUNKS = 69696;

    public static final int PASS = 2;

    private static final ShaderProgram SHADER_ALPHA = new ShaderProgram("schematica", null, "shaders/alpha.frag");

    private static final Vector3d PLAYER_POSITION_OFFSET = new Vector3d();

    private final Minecraft mc;

    private final Profiler profiler;

    private final RenderManager renderManager;

    private final MBlockPos tmp = new MBlockPos();

    private SchematicWorld world;

    private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();

    private final Set<RenderOverlay> overlaysToUpdate = Sets.newLinkedHashSet();

    private List<ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);

    private ViewFrustumOverlay viewFrustum;

    private double frustumUpdatePosX = Double.MIN_VALUE;

    private double frustumUpdatePosY = Double.MIN_VALUE;

    private double frustumUpdatePosZ = Double.MIN_VALUE;

    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;

    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;

    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;

    private double lastViewEntityX = Double.MIN_VALUE;

    private double lastViewEntityY = Double.MIN_VALUE;

    private double lastViewEntityZ = Double.MIN_VALUE;

    private double lastViewEntityPitch = Double.MIN_VALUE;

    private double lastViewEntityYaw = Double.MIN_VALUE;

    private final ChunkRenderDispatcher renderDispatcher;

    private final OverlayRenderDispatcher renderDispatcherOverlay = new OverlayRenderDispatcher();

    private SchematicChunkRenderContainer renderContainer;

    private int renderDistanceChunks = -1;

    private int countEntitiesTotal;

    private int countEntitiesRendered;

    private int countTileEntitiesTotal;

    private int countTileEntitiesRendered;

    private boolean vboEnabled;

    private ISchematicRenderChunkFactory renderChunkFactory;

    private double prevRenderSortX;

    private double prevRenderSortY;

    private double prevRenderSortZ;

    private boolean displayListEntitiesDirty = true;

    private int frameCount = 0;

    private boolean awaitingRefresh = false;

    public RenderSchematic(Minecraft minecraft) {
        super(minecraft);
        this.mc = minecraft;
        this.profiler = minecraft.mcProfiler;
        this.renderManager = minecraft.getRenderManager();
        this.renderDispatcher = ((MixinRenderGlobal) this.mc.renderGlobal).getRenderDispatcher();
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.vboEnabled = OpenGlHelper.useVbo();
        if (this.vboEnabled) {
            initVbo();
        } else {
            initList();
        }
    }

    private void initVbo() {
        this.renderContainer = (SchematicChunkRenderContainer) new SchematicChunkRenderContainerVbo();
        this.renderChunkFactory = new ISchematicRenderChunkFactory() {
            public RenderChunk makeRenderChunk(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
                return (RenderChunk) new SchematicRenderChunkVbo(world, renderGlobal, pos, index);
            }

            public RenderOverlay makeRenderOverlay(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
                return new RenderOverlay(world, renderGlobal, pos, index);
            }
        };
    }

    private void initList() {
        this.renderContainer = (SchematicChunkRenderContainer) new SchematicChunkRenderContainerList();
        this.renderChunkFactory = new ISchematicRenderChunkFactory() {
            public RenderChunk makeRenderChunk(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
                return (RenderChunk) new SchematicRenderChunkList(world, renderGlobal, pos, index);
            }

            public RenderOverlay makeRenderOverlay(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
                return (RenderOverlay) new RenderOverlayList(world, renderGlobal, pos, index);
            }
        };
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    public void makeEntityOutlineShader() {
    }

    public void renderEntityOutlineFramebuffer() {
    }

    protected boolean isRenderEntityOutlines() {
        return false;
    }

    public void setWorldAndLoadRenderers(WorldClient worldClient) {
        if (worldClient instanceof SchematicWorld) {
            setWorldAndLoadRenderers((SchematicWorld) worldClient);
        } else {
            setWorldAndLoadRenderers(null);
        }
    }

    public void setWorldAndLoadRenderers(SchematicWorld world) {
        if (this.world != null)
            this.world.removeWorldAccess((IWorldAccess) this);
        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.set((World) world);
        this.world = world;
        if (world != null) {
            world.addWorldAccess((IWorldAccess) this);
            loadRenderers();
        }
    }

    private void renderSchematic(SchematicWorld schematic, float partialTicks) {
        if (this.world != schematic) {
            this.world = schematic;
            loadRenderers();
        }
        PLAYER_POSITION_OFFSET.set(ClientProxy.playerPosition).sub(this.world.position.x, this.world.position.y, this.world.position.z);
        if (OpenGlHelper.shadersSupported && ConfigurationHandler.enableAlpha) {
            GL20.glUseProgram(SHADER_ALPHA.getProgram());
            GL20.glUniform1f(GL20.glGetUniformLocation(SHADER_ALPHA.getProgram(), "alpha_multiplier"), ConfigurationHandler.alpha);
        }
        int fps = Math.max(Minecraft.getDebugFPS(), 30);
        renderWorld(partialTicks, System.nanoTime() + (1000000000 / fps));
        if (OpenGlHelper.shadersSupported && ConfigurationHandler.enableAlpha)
            GL20.glUseProgram(0);
    }

    private void renderOverlay(SchematicWorld schematic, boolean isRenderingSchematic) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GeometryTessellator tessellator = GeometryTessellator.getInstance();
        tessellator.setTranslation(-ClientProxy.playerPosition.x, -ClientProxy.playerPosition.y, -ClientProxy.playerPosition.z);
        tessellator.setDelta(ConfigurationHandler.blockDelta);
        if (ClientProxy.isRenderingGuide) {
            tessellator.beginQuads();
            tessellator.drawCuboid((BlockPos) ClientProxy.pointA, 63, 1069481984);
            tessellator.drawCuboid((BlockPos) ClientProxy.pointB, 63, 1056964799);
            tessellator.draw();
        }
        tessellator.beginLines();
        if (ClientProxy.isRenderingGuide) {
            tessellator.drawCuboid((BlockPos) ClientProxy.pointA, 63, 1069481984);
            tessellator.drawCuboid((BlockPos) ClientProxy.pointB, 63, 1056964799);
            tessellator.drawCuboid((BlockPos) ClientProxy.pointMin, (BlockPos) ClientProxy.pointMax, 63, 2130755328);
        }
        if (isRenderingSchematic) {
            this.tmp.set(schematic.position.x + schematic.getWidth() - 1, schematic.position.y + schematic.getHeight() - 1, schematic.position.z + schematic.getLength() - 1);
            tessellator.drawCuboid((BlockPos) schematic.position, (BlockPos) this.tmp, 63, 2143223999);
        }
        tessellator.draw();
        GlStateManager.depthMask(false);
        this.renderContainer.renderOverlay();
        GlStateManager.depthMask(true);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    private void renderWorld(float partialTicks, long finishTimeNano) {
        GlStateManager.enableCull();
        this.profiler.endStartSection("culling");
        Frustum frustum = new Frustum();
        Entity entity = this.mc.getRenderViewEntity();
        double x = PLAYER_POSITION_OFFSET.x;
        double y = PLAYER_POSITION_OFFSET.y;
        double z = PLAYER_POSITION_OFFSET.z;
        frustum.setPosition(x, y, z);
        GlStateManager.shadeModel(7425);
        this.profiler.endStartSection("prepareterrain");
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        this.profiler.endStartSection("terrain_setup");
        setupTerrain(entity, partialTicks, (ICamera) frustum, this.frameCount++, isInsideWorld(x, y, z));
        this.profiler.endStartSection("updatechunks");
        updateChunks(finishTimeNano / 2L);
        this.profiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        renderBlockLayer(EnumWorldBlockLayer.SOLID, partialTicks, 2, entity);
        renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, 2, entity);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        renderBlockLayer(EnumWorldBlockLayer.CUTOUT, partialTicks, 2, entity);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.profiler.endStartSection("entities");
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        renderEntities(entity, (ICamera) frustum, partialTicks);
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        disableLightmap();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        GlStateManager.alphaFunc(516, 0.1F);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.shadeModel(7425);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        this.profiler.endStartSection("translucent");
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, 2, entity);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.shadeModel(7424);
        GlStateManager.enableCull();
    }

    private boolean isInsideWorld(double x, double y, double z) {
        return (x >= -1.0D && y >= -1.0D && z >= -1.0D && x <= this.world.getWidth() && y <= this.world.getHeight() && z <= this.world.getLength());
    }

    private void disableLightmap() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void refresh() {
        this.awaitingRefresh = true;
    }

    public void loadRenderers() {
        if (this.world != null) {
            this.displayListEntitiesDirty = true;
            this.renderDistanceChunks = ConfigurationHandler.renderDistance;
            boolean vbo = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();
            if (vbo && !this.vboEnabled) {
                initList();
            } else if (!vbo && this.vboEnabled) {
                initVbo();
            }
            if (this.viewFrustum != null)
                this.viewFrustum.deleteGlResources();
            stopChunkUpdates();
            this.viewFrustum = new ViewFrustumOverlay((World) this.world, this.renderDistanceChunks, this, this.renderChunkFactory);
            double posX = PLAYER_POSITION_OFFSET.x;
            double posZ = PLAYER_POSITION_OFFSET.z;
            this.viewFrustum.updateChunkPositions(posX, posZ);
        }
    }

    protected void stopChunkUpdates() {
        this.chunksToUpdate.clear();
        this.overlaysToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
        this.renderDispatcherOverlay.stopChunkUpdates();
    }

    public void checkOcclusionQueryResult(int p_72720_1_, int p_72720_2_) {
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
        int entityPass = 0;
        this.profiler.startSection("prepare");
        TileEntityRendererDispatcher.instance.cacheActiveRenderInfo((World) this.world, this.mc.getTextureManager(), this.mc.fontRendererObj, renderViewEntity, partialTicks);
        this.renderManager.cacheActiveRenderInfo((World) this.world, this.mc.fontRendererObj, renderViewEntity, this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
        this.countEntitiesTotal = 0;
        this.countEntitiesRendered = 0;
        this.countTileEntitiesTotal = 0;
        this.countTileEntitiesRendered = 0;
        double x = PLAYER_POSITION_OFFSET.x;
        double y = PLAYER_POSITION_OFFSET.y;
        double z = PLAYER_POSITION_OFFSET.z;
        TileEntityRendererDispatcher.staticPlayerX = x;
        TileEntityRendererDispatcher.staticPlayerY = y;
        TileEntityRendererDispatcher.staticPlayerZ = z;
        TileEntityRendererDispatcher.instance.entityX = x;
        TileEntityRendererDispatcher.instance.entityY = y;
        TileEntityRendererDispatcher.instance.entityZ = z;
        this.renderManager.setRenderPosition(x, y, z);
        this.mc.entityRenderer.enableLightmap();
        this.profiler.endStartSection("blockentities");
        RenderHelper.enableStandardItemLighting();
        for (ContainerLocalRenderInformation renderInfo : this.renderInfos) {
            for (TileEntity tileEntity : renderInfo.renderChunk.getCompiledChunk().getTileEntities()) {
                AxisAlignedBB renderBB = TileEntityUtils.getRenderBoundingBox(tileEntity);
                this.countTileEntitiesTotal++;
                if (!TileEntityUtils.shouldRenderInPass(0) || !camera.isBoundingBoxInFrustum(renderBB))
                    continue;
                if (!this.mc.theWorld.isAirBlock(tileEntity.getPos().add((Vec3i) this.world.position)))
                    continue;
                TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
                this.countTileEntitiesRendered++;
            }
        }
        this.mc.entityRenderer.disableLightmap();
        this.profiler.endSection();
    }

    public String getDebugInfoRenders() {
        int total = this.viewFrustum.renderChunks.length;
        int rendered = 0;
        for (ContainerLocalRenderInformation renderInfo : this.renderInfos) {
            CompiledChunk compiledChunk = renderInfo.renderChunk.compiledChunk;
            if (compiledChunk != CompiledChunk.DUMMY && !compiledChunk.isEmpty())
                rendered++;
        }
        return String.format("C: %d/%d %sD: %d, %s", new Object[]{Integer.valueOf(rendered), Integer.valueOf(total), this.mc.renderChunksMany ? "(s) " : "", Integer.valueOf(this.renderDistanceChunks), this.renderDispatcher.getDebugInfo()});
    }

    public String getDebugInfoEntities() {
        return String.format("E: %d/%d", new Object[]{Integer.valueOf(this.countEntitiesRendered), Integer.valueOf(this.countEntitiesTotal)});
    }

    public String getDebugInfoTileEntities() {
        return String.format("TE: %d/%d", new Object[]{Integer.valueOf(this.countTileEntitiesRendered), Integer.valueOf(this.countTileEntitiesTotal)});
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        if (ConfigurationHandler.renderDistance != this.renderDistanceChunks || this.vboEnabled != OpenGlHelper.useVbo())
            loadRenderers();
        this.profiler.startSection("camera");
        double posX = PLAYER_POSITION_OFFSET.x;
        double posY = PLAYER_POSITION_OFFSET.y;
        double posZ = PLAYER_POSITION_OFFSET.z;
        double deltaX = posX - this.frustumUpdatePosX;
        double deltaY = posY - this.frustumUpdatePosY;
        double deltaZ = posZ - this.frustumUpdatePosZ;
        int chunkCoordX = MathHelper.floor_double(posX) >> 4;
        int chunkCoordY = MathHelper.floor_double(posY) >> 4;
        int chunkCoordZ = MathHelper.floor_double(posZ) >> 4;
        if (this.frustumUpdatePosChunkX != chunkCoordX || this.frustumUpdatePosChunkY != chunkCoordY || this.frustumUpdatePosChunkZ != chunkCoordZ || deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 16.0D) {
            this.frustumUpdatePosX = posX;
            this.frustumUpdatePosY = posY;
            this.frustumUpdatePosZ = posZ;
            this.frustumUpdatePosChunkX = chunkCoordX;
            this.frustumUpdatePosChunkY = chunkCoordY;
            this.frustumUpdatePosChunkZ = chunkCoordZ;
            this.viewFrustum.updateChunkPositions(posX, posZ);
        }
        this.profiler.endStartSection("renderlistcamera");
        this.renderContainer.initialize(posX, posY, posZ);
        this.profiler.endStartSection("culling");
        BlockPos posEye = new BlockPos(posX, posY + viewEntity.getEyeHeight(), posZ);
        RenderChunk renderchunk = ((MixinViewFrustum) this.viewFrustum).callGetRenderChunk(posEye);
        RenderOverlay renderoverlay = this.viewFrustum.getRenderOverlay(posEye);
        this.displayListEntitiesDirty = (this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || posX != this.lastViewEntityX || posY != this.lastViewEntityY || posZ != this.lastViewEntityZ || viewEntity.rotationPitch != this.lastViewEntityPitch || viewEntity.rotationYaw != this.lastViewEntityYaw);
        this.lastViewEntityX = posX;
        this.lastViewEntityY = posY;
        this.lastViewEntityZ = posZ;
        this.lastViewEntityPitch = viewEntity.rotationPitch;
        this.lastViewEntityYaw = viewEntity.rotationYaw;
        if (this.displayListEntitiesDirty) {
            this.displayListEntitiesDirty = false;
            this.renderInfos = Lists.newArrayListWithCapacity(69696);
            LinkedList<ContainerLocalRenderInformation> renderInfoList = Lists.newLinkedList();
            boolean renderChunksMany = this.mc.renderChunksMany;
            if (renderchunk == null) {
                int chunkY = (posEye.getY() > 0) ? 248 : 8;
                for (int chunkX = -this.renderDistanceChunks; chunkX <= this.renderDistanceChunks; chunkX++) {
                    for (int chunkZ = -this.renderDistanceChunks; chunkZ <= this.renderDistanceChunks; chunkZ++) {
                        BlockPos pos = new BlockPos((chunkX << 4) + 8, chunkY, (chunkZ << 4) + 8);
                        RenderChunk renderChunk = ((MixinViewFrustum) this.viewFrustum).callGetRenderChunk(pos);
                        RenderOverlay renderOverlay = this.viewFrustum.getRenderOverlay(pos);
                        if (renderChunk != null && camera.isBoundingBoxInFrustum(renderChunk.boundingBox)) {
                            renderChunk.setFrameIndex(frameCount);
                            renderOverlay.setFrameIndex(frameCount);
                            renderInfoList.add(new ContainerLocalRenderInformation(renderChunk, renderOverlay, null, 0));
                        }
                    }
                }
            } else {
                boolean add = false;
                ContainerLocalRenderInformation renderInfo = new ContainerLocalRenderInformation(renderchunk, renderoverlay, null, 0);
                Set<EnumFacing> visibleSides = getVisibleSides(posEye);
                if (visibleSides.size() == 1) {
                    Vector3f viewVector = getViewVector(viewEntity, partialTicks);
                    EnumFacing facing = EnumFacing.getFacingFromVector(viewVector.x, viewVector.y, viewVector.z).getOpposite();
                    visibleSides.remove(facing);
                }
                if (visibleSides.isEmpty())
                    add = true;
                if (add && !playerSpectator) {
                    this.renderInfos.add(renderInfo);
                } else {
                    if (playerSpectator && this.world.getBlockState(posEye).getBlock().isOpaqueCube())
                        renderChunksMany = false;
                    renderchunk.setFrameIndex(frameCount);
                    renderoverlay.setFrameIndex(frameCount);
                    renderInfoList.add(renderInfo);
                }
            }
            while (!renderInfoList.isEmpty()) {
                ContainerLocalRenderInformation renderInfo = renderInfoList.poll();
                RenderChunk renderChunk = renderInfo.renderChunk;
                EnumFacing facing = renderInfo.facing;
                BlockPos posChunk = renderChunk.getPosition();
                this.renderInfos.add(renderInfo);
                for (EnumFacing side : MixinEnumFacing.getValues()) {
                    RenderChunk neighborRenderChunk = getNeighborRenderChunk(posEye, posChunk, side);
                    RenderOverlay neighborRenderOverlay = getNeighborRenderOverlay(posEye, posChunk, side);
                    if ((!renderChunksMany || !renderInfo.setFacing.contains(side.getOpposite())) && (!renderChunksMany || facing == null || renderChunk.getCompiledChunk().isVisible(facing.getOpposite(), side)) && neighborRenderChunk != null && neighborRenderChunk.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(neighborRenderChunk.boundingBox)) {
                        ContainerLocalRenderInformation renderInfoNext = new ContainerLocalRenderInformation(neighborRenderChunk, neighborRenderOverlay, side, renderInfo.counter + 1);
                        renderInfoNext.setFacing.addAll(renderInfo.setFacing);
                        renderInfoNext.setFacing.add(side);
                        renderInfoList.add(renderInfoNext);
                    }
                }
            }
        }
        this.renderDispatcher.clearChunkUpdates();
        this.renderDispatcherOverlay.clearChunkUpdates();
        Set<RenderChunk> set = this.chunksToUpdate;
        Set<RenderOverlay> set1 = this.overlaysToUpdate;
        this.chunksToUpdate = Sets.newLinkedHashSet();
        for (ContainerLocalRenderInformation renderInfo : this.renderInfos) {
            RenderChunk renderChunk = renderInfo.renderChunk;
            RenderOverlay renderOverlay = renderInfo.renderOverlay;
            if (renderChunk.isNeedsUpdate() || set.contains(renderChunk)) {
                this.displayListEntitiesDirty = true;
                this.chunksToUpdate.add(renderChunk);
            }
            if (renderOverlay.isNeedsUpdate() || set1.contains(renderOverlay)) {
                this.displayListEntitiesDirty = true;
                this.overlaysToUpdate.add(renderOverlay);
            }
        }
        this.chunksToUpdate.addAll(set);
        this.overlaysToUpdate.addAll(set1);
        this.profiler.endSection();
    }

    private Set<EnumFacing> getVisibleSides(BlockPos pos) {
        VisGraph visgraph = new VisGraph();
        BlockPos posChunk = new BlockPos(pos.getX() & 0xFFFFFFF0, pos.getY() & 0xFFFFFFF0, pos.getZ() & 0xFFFFFFF0);
        for (BlockPos.MutableBlockPos mutableBlockPos : BlockPos.getAllInBoxMutable(posChunk, posChunk.add(15, 15, 15))) {
            if (this.world.getBlockState((BlockPos) mutableBlockPos).getBlock().isOpaqueCube())
                visgraph.func_178606_a(mutableBlockPos);
//        visgraph.setOpaqueCube((BlockPos)mutableBlockPos);
        }
        return visgraph.func_178609_b(pos);
//    return visgraph.getVisibleFacings(pos);
    }

    private RenderChunk getNeighborRenderChunk(BlockPos posEye, BlockPos posChunk, EnumFacing side) {
        BlockPos offset = posChunk.offset(side, 16);
        if (MathHelper.abs_int(posEye.getX() - offset.getX()) > this.renderDistanceChunks * 16)
            return null;
        if (offset.getY() < 0 || offset.getY() >= 256)
            return null;
        if (MathHelper.abs_int(posEye.getZ() - offset.getZ()) > this.renderDistanceChunks * 16)
            return null;
        return ((MixinViewFrustum) this.viewFrustum).callGetRenderChunk(offset);
    }

    private RenderOverlay getNeighborRenderOverlay(BlockPos posEye, BlockPos posChunk, EnumFacing side) {
        BlockPos offset = posChunk.offset(side, 16);
        if (MathHelper.abs_int(posEye.getX() - offset.getX()) > this.renderDistanceChunks * 16)
            return null;
        if (offset.getY() < 0 || offset.getY() >= 256)
            return null;
        if (MathHelper.abs_int(posEye.getZ() - offset.getZ()) > this.renderDistanceChunks * 16)
            return null;
        return this.viewFrustum.getRenderOverlay(offset);
    }

    public int renderBlockLayer(EnumWorldBlockLayer layer, double partialTicks, int pass, Entity entity) {
        RenderHelper.disableStandardItemLighting();
        if (layer == EnumWorldBlockLayer.TRANSLUCENT) {
            this.profiler.startSection("translucent_sort");
            double posX = PLAYER_POSITION_OFFSET.x;
            double posY = PLAYER_POSITION_OFFSET.y;
            double posZ = PLAYER_POSITION_OFFSET.z;
            double deltaX = posX - this.prevRenderSortX;
            double deltaY = posY - this.prevRenderSortY;
            double deltaZ = posZ - this.prevRenderSortZ;
            if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 1.0D) {
                this.prevRenderSortX = posX;
                this.prevRenderSortY = posY;
                this.prevRenderSortZ = posZ;
                int i = 0;
                for (ContainerLocalRenderInformation renderInfo : this.renderInfos) {
                    if (renderInfo.renderChunk.compiledChunk.isLayerStarted(layer) && i++ < 15) {
                        this.renderDispatcher.updateTransparencyLater(renderInfo.renderChunk);
                        this.renderDispatcherOverlay.updateTransparencyLater((RenderChunk) renderInfo.renderOverlay);
                    }
                }
            }
            this.profiler.endSection();
        }
        this.profiler.startSection("filterempty");
        int count = 0;
        boolean isTranslucent = (layer == EnumWorldBlockLayer.TRANSLUCENT);
        int start = isTranslucent ? (this.renderInfos.size() - 1) : 0;
        int end = isTranslucent ? -1 : this.renderInfos.size();
        int step = isTranslucent ? -1 : 1;
        int index;
        for (index = start; index != end; index += step) {
            ContainerLocalRenderInformation renderInfo = this.renderInfos.get(index);
            RenderChunk renderChunk = renderInfo.renderChunk;
            RenderOverlay renderOverlay = renderInfo.renderOverlay;
            if (!renderChunk.getCompiledChunk().isLayerEmpty(layer)) {
                count++;
                this.renderContainer.addRenderChunk(renderChunk, layer);
            }
            if (isTranslucent && renderOverlay != null && !renderOverlay.getCompiledChunk().isLayerEmpty(layer)) {
                count++;
                this.renderContainer.addRenderOverlay(renderOverlay);
            }
        }
        this.profiler.endStartSection("render_" + layer);
        renderBlockLayer(layer);
        this.profiler.endSection();
        return count;
    }

    private void renderBlockLayer(EnumWorldBlockLayer layer) {
        this.mc.entityRenderer.enableLightmap();
        this.renderContainer.renderChunkLayer(layer);
        this.mc.entityRenderer.disableLightmap();
    }

    public void updateClouds() {
    }

    public void renderSky(float partialTicks, int pass) {
    }

    public void renderClouds(float partialTicks, int pass) {
    }

    public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
        return false;
    }

    public void updateChunks(long finishTimeNano) {
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);
        Iterator<RenderChunk> chunkIterator = this.chunksToUpdate.iterator();
        while (chunkIterator.hasNext()) {
            RenderChunk renderChunk = chunkIterator.next();
            if (!this.renderDispatcher.updateChunkLater(renderChunk))
                break;
            renderChunk.setNeedsUpdate(false);
            chunkIterator.remove();
        }
        this.displayListEntitiesDirty |= this.renderDispatcherOverlay.runChunkUploads(finishTimeNano);
        Iterator<RenderOverlay> overlayIterator = this.overlaysToUpdate.iterator();
        while (overlayIterator.hasNext()) {
            RenderOverlay renderOverlay = overlayIterator.next();
            if (!this.renderDispatcherOverlay.updateChunkLater((RenderChunk) renderOverlay))
                break;
            renderOverlay.setNeedsUpdate(false);
            overlayIterator.remove();
        }
    }

    public void renderWorldBorder(Entity entity, float partialTicks) {
    }

    public void drawBlockDamageTexture(Tessellator tessellator, WorldRenderer worldRenderer, Entity entity, float partialTicks) {
    }

    public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPosition, int p_72731_3_, float partialTicks) {
    }

    public void markBlockForUpdate(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    public void notifyLightSet(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
    }

    private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        if (this.world == null || this.viewFrustum == null)
            return;
        MBlockPos position = this.world.position;
        this.viewFrustum.func_178162_a(x1 - position.x, y1 - position.y, z1 - position.z, x2 - position.x, y2 - position.y, z2 - position.z);
    }

    public void func_174961_a(String name, BlockPos pos) {
    }

    public void playSound(String name, double x, double y, double z, float volume, float pitch) {
    }

    public void playSoundToNearExcept(EntityPlayer player, String name, double x, double y, double z, float volume, float pitch) {
    }

    public void spawnParticle(int p_180442_1_, boolean p_180442_2_, double p_180442_3_, double p_180442_5_, double p_180442_7_, double p_180442_9_, double p_180442_11_, double p_180442_13_, int... p_180442_15_) {
    }

    public void onEntityCreate(Entity entityIn) {
    }

    public void onEntityDestroy(Entity entityIn) {
    }

    public void deleteAllDisplayLists() {
    }

    public void broadcastSound(int p_180440_1_, BlockPos pos, int p_180440_3_) {
    }

    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_) {
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    }

    public void setDisplayListEntitiesDirty() {
        this.displayListEntitiesDirty = true;
    }

    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, ev -> {
            EntityPlayerSP player = (this.mc.getRenderViewEntity() instanceof EntityPlayerSP) ? (EntityPlayerSP) this.mc.getRenderViewEntity() : this.mc.thePlayer;
            if (player != null) {
                this.profiler.startSection("schematica");
                ClientProxy.setPlayerData((EntityPlayer) player, ev.partialTicks);
                SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
                boolean isRenderingSchematic = (schematic != null && schematic.isRendering);
                this.profiler.startSection("schematic");
                if (isRenderingSchematic) {
                    GlStateManager.pushMatrix();
                    renderSchematic(schematic, ev.partialTicks);
                    GlStateManager.popMatrix();
                }
                this.profiler.endStartSection("guide");
                if (ClientProxy.isRenderingGuide || isRenderingSchematic) {
                    GlStateManager.pushMatrix();
                    renderOverlay(schematic, isRenderingSchematic);
                    GlStateManager.popMatrix();
                }
                this.profiler.endSection();
                this.profiler.endSection();
            }
        });
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (this.awaitingRefresh) {
                this.awaitingRefresh = false;
                Schematica.getInstance().clearTracerLists();
                loadRenderers();
            }
        });
    }

    class ContainerLocalRenderInformation {
        final RenderChunk renderChunk;

        final RenderOverlay renderOverlay;

        final EnumFacing facing;

        final Set<EnumFacing> setFacing;

        final int counter;

        ContainerLocalRenderInformation(RenderChunk renderChunk, RenderOverlay renderOverlay, EnumFacing facing, int counter) {
            this.setFacing = EnumSet.noneOf(EnumFacing.class);
            this.renderChunk = renderChunk;
            this.renderOverlay = renderOverlay;
            this.facing = facing;
            this.counter = counter;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\RenderSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */