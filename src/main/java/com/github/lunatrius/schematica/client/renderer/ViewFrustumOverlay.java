package com.github.lunatrius.schematica.client.renderer;

import com.github.lunatrius.schematica.client.renderer.chunk.overlay.ISchematicRenderChunkFactory;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ViewFrustumOverlay extends ViewFrustum {
    public RenderOverlay[] renderOverlays;

    public ViewFrustumOverlay(World world, int renderDistanceChunks, RenderGlobal renderGlobal, ISchematicRenderChunkFactory renderChunkFactory) {
        super(world, renderDistanceChunks, renderGlobal, (IRenderChunkFactory) renderChunkFactory);
        createRenderOverlays(renderChunkFactory);
    }

    protected void createRenderOverlays(ISchematicRenderChunkFactory renderChunkFactory) {
        int amount = this.countChunksX * this.countChunksY * this.countChunksZ;
        this.renderOverlays = new RenderOverlay[amount];
        int count = 0;
        for (int x = 0; x < this.countChunksX; x++) {
            for (int y = 0; y < this.countChunksY; y++) {
                for (int z = 0; z < this.countChunksZ; z++) {
                    int index = (z * this.countChunksY + y) * this.countChunksX + x;
                    BlockPos pos = new BlockPos(x * 16, y * 16, z * 16);
                    this.renderOverlays[index] = renderChunkFactory.makeRenderOverlay(this.world, this.renderGlobal, pos, count++);
                }
            }
        }
    }

    public void deleteGlResources() {
        super.deleteGlResources();
        for (RenderOverlay renderOverlay : this.renderOverlays)
            renderOverlay.deleteGlResources();
    }

    public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
        super.updateChunkPositions(viewEntityX, viewEntityZ);
        int xx = MathHelper.floor_double(viewEntityX) - 8;
        int zz = MathHelper.floor_double(viewEntityZ) - 8;
        int yy = this.countChunksX * 16;
        for (int chunkX = 0; chunkX < this.countChunksX; chunkX++) {
            int x = getPosition(xx, yy, chunkX);
            for (int chunkZ = 0; chunkZ < this.countChunksZ; chunkZ++) {
                int z = getPosition(zz, yy, chunkZ);
                for (int chunkY = 0; chunkY < this.countChunksY; chunkY++) {
                    int y = chunkY * 16;
                    RenderOverlay renderOverlay = this.renderOverlays[(chunkZ * this.countChunksY + chunkY) * this.countChunksX + chunkX];
                    BlockPos blockpos = new BlockPos(x, y, z);
                    if (!blockpos.equals(renderOverlay.getPosition()))
                        renderOverlay.setPosition(blockpos);
                }
            }
        }
    }

    private int getPosition(int xz, int y, int chunk) {
        int chunks = chunk * 16;
        int i = chunks - xz + y / 2;
        if (i < 0)
            i -= y - 1;
        return chunks - i / y * y;
    }

    public void func_178162_a(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        super.markBlocksForUpdate(fromX, fromY, fromZ, toX, toY, toZ);
        int x0 = MathHelper.bucketInt(fromX, 16);
        int y0 = MathHelper.bucketInt(fromY, 16);
        int z0 = MathHelper.bucketInt(fromZ, 16);
        int x1 = MathHelper.bucketInt(toX, 16);
        int y1 = MathHelper.bucketInt(toY, 16);
        int z1 = MathHelper.bucketInt(toZ, 16);
        for (int xi = x0; xi <= x1; xi++) {
            int x = xi % this.countChunksX;
            if (x < 0)
                x += this.countChunksX;
            for (int yi = y0; yi <= y1; yi++) {
                int y = yi % this.countChunksY;
                if (y < 0)
                    y += this.countChunksY;
                for (int zi = z0; zi <= z1; zi++) {
                    int z = zi % this.countChunksZ;
                    if (z < 0)
                        z += this.countChunksZ;
                    int index = (z * this.countChunksY + y) * this.countChunksX + x;
                    RenderOverlay renderOverlay = this.renderOverlays[index];
                    renderOverlay.setNeedsUpdate(true);
                }
            }
        }
    }

    public RenderOverlay getRenderOverlay(BlockPos pos) {
        int x = MathHelper.bucketInt(pos.getX(), 16);
        int y = MathHelper.bucketInt(pos.getY(), 16);
        int z = MathHelper.bucketInt(pos.getZ(), 16);
        if (y >= 0 && y < this.countChunksY) {
            x %= this.countChunksX;
            if (x < 0)
                x += this.countChunksX;
            z %= this.countChunksZ;
            if (z < 0)
                z += this.countChunksZ;
            int index = (z * this.countChunksY + y) * this.countChunksX + x;
            return this.renderOverlays[index];
        }
        return null;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\ViewFrustumOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */