package mchorse.emoticons.skin_n_bones.api.animation;

import co.crystaldev.client.Client;
import co.crystaldev.client.util.javax.*;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AnimationMesh {
    public static final boolean DEBUG = false;

    public Animation owner;

    public ResourceLocation texture;

    public String name;

    public BOBJLoader.CompiledData data;

    private final BOBJArmature armature;

    private BOBJArmature currentArmature;

    public float alpha = 1.0F;

    public FloatBuffer vertices;

    public FloatBuffer normals;

    public FloatBuffer textcoords;

    public IntBuffer indices;

    public int vertexBuffer;

    public int normalBuffer;

    public int texcoordBuffer;

    public int indexBuffer;

    public AnimationMesh(Animation owner, String name, BOBJLoader.CompiledData data) {
        this.owner = owner;
        this.name = name;
        this.data = data;
        this.armature = this.data.mesh.armature;
        this.armature.initArmature();
        this.currentArmature = this.armature;
        initBuffers();
    }

    public BOBJArmature getArmature() {
        return this.armature;
    }

    public BOBJArmature getCurrentArmature() {
        return this.currentArmature;
    }

    public void setCurrentArmature(BOBJArmature armature) {
        this.currentArmature = armature;
    }

    private void initBuffers() {
        this.vertices = BufferUtils.createFloatBuffer(this.data.posData.length);
        this.vertices.put(this.data.posData).flip();
        this.normals = BufferUtils.createFloatBuffer(this.data.normData.length);
        this.normals.put(this.data.normData).flip();
        this.textcoords = BufferUtils.createFloatBuffer(this.data.texData.length);
        this.textcoords.put(this.data.texData).flip();
        this.indices = BufferUtils.createIntBuffer(this.data.indexData.length);
        this.indices.put(this.data.indexData).flip();
        this.vertexBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(34962, this.vertexBuffer);
        GL15.glBufferData(34962, this.vertices, 35048);
        this.normalBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(34962, this.normalBuffer);
        GL15.glBufferData(34962, this.normals, 35044);
        this.texcoordBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(34962, this.texcoordBuffer);
        GL15.glBufferData(34962, this.textcoords, 35044);
        this.indexBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(34963, this.indexBuffer);
        GL15.glBufferData(34963, this.indices, 35044);
        GL15.glBindBuffer(34962, 0);
        GL15.glBindBuffer(34963, 0);
    }

    public void setFiltering(int filtering) {
        GL11.glTexParameteri(3553, 10241, filtering);
        GL11.glTexParameteri(3553, 10240, filtering);
    }

    public int getFiltering() {
        return GL11.glGetTexParameteri(3553, 10241);
    }

    public void delete() {
        GL15.glDeleteBuffers(this.vertexBuffer);
        GL15.glDeleteBuffers(this.normalBuffer);
        GL15.glDeleteBuffers(this.texcoordBuffer);
        GL15.glDeleteBuffers(this.indexBuffer);
        this.vertices = null;
        this.normals = null;
        this.textcoords = null;
        this.indices = null;
    }

    public void updateMesh() {
        Vector4f sumVertex = new Vector4f();
        Vector4f resultVertex = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
        Vector3f sumNormal = new Vector3f();
        Vector3f resultNormal = new Vector3f(0.0F, 0.0F, 0.0F);
        float[] oldVertices = this.data.posData;
        float[] newVertices = new float[oldVertices.length];
        float[] oldNormals = this.data.normData;
        float[] newNormals = new float[oldNormals.length];
        Matrix4f[] matrices = this.currentArmature.matrices;
        for (int i = 0, c = newVertices.length / 4; i < c; i++) {
            int count = 0;
            for (int w = 0; w < 4; w++) {
                float weight = this.data.weightData[i * 4 + w];
                if (weight > 0.0F) {
                    int index = this.data.boneIndexData[i * 4 + w];
                    sumVertex.set(oldVertices[i * 4], oldVertices[i * 4 + 1], oldVertices[i * 4 + 2], 1.0F);
                    matrices[index].transform((Tuple4f) sumVertex);
                    sumVertex.scale(weight);
                    resultVertex.add((Tuple4f) sumVertex);
                    sumNormal.set(oldNormals[i * 3], oldNormals[i * 3 + 1], oldNormals[i * 3 + 2]);
                    matrices[index].transform(sumNormal);
                    sumNormal.scale(weight);
                    resultNormal.add((Tuple3f) sumNormal);
                    count++;
                }
            }
            if (count == 0) {
                resultNormal.set(oldNormals[i * 3], oldNormals[i * 3 + 1], oldNormals[i * 3 + 2]);
                resultVertex.set(oldVertices[i * 4], oldVertices[i * 4 + 1], oldVertices[i * 4 + 2], 1.0F);
            }
            newVertices[i * 4] = resultVertex.x;
            newVertices[i * 4 + 1] = resultVertex.y;
            newVertices[i * 4 + 2] = resultVertex.z;
            newVertices[i * 4 + 3] = resultVertex.w;
            newNormals[i * 3] = resultNormal.x;
            newNormals[i * 3 + 1] = resultNormal.y;
            newNormals[i * 3 + 2] = resultNormal.z;
            resultVertex.set(0.0F, 0.0F, 0.0F, 0.0F);
            resultNormal.set(0.0F, 0.0F, 0.0F);
        }
        updateVertices(newVertices);
        updateNormals(newNormals);
    }

    public void updateVertices(float[] data) {
        this.vertices.clear();
        this.vertices.put(data).flip();
        GL15.glBindBuffer(34962, this.vertexBuffer);
        GL15.glBufferData(34962, this.vertices, 35048);
    }

    public void updateNormals(float[] data) {
        this.normals.clear();
        this.normals.put(data).flip();
        GL15.glBindBuffer(34962, this.normalBuffer);
        GL15.glBufferData(34962, this.normals, 35048);
    }

    public void render(Minecraft mc, AnimationMeshConfig config) {
        if (config != null && (!config.visible || this.alpha <= 0.0F))
            return;
        ResourceLocation texture = getTexture(config);
        boolean smooth = (config != null && config.smooth);
        boolean normals = (config != null && config.normals);
        boolean lighting = (config == null || config.lighting);
        float lastX = Client.getLastBrightnessX();
        float lastY = Client.getLastBrightnessY();
        if (texture != null) {
            mc.getTextureManager().bindTexture(texture);
            if (config != null)
                setFiltering(config.filtering);
        }
        if (smooth && normals)
            GL11.glShadeModel(7425);
        if (!normals)
            RenderHelper.disableStandardItemLighting();
        if (!lighting)
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        int color = (config != null) ? config.color : 16777215;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = this.alpha;
        GlStateManager.color(r, g, b, a);
        GL15.glBindBuffer(34962, this.vertexBuffer);
        GL11.glVertexPointer(4, 5126, 0, 0L);
        GL15.glBindBuffer(34962, this.normalBuffer);
        GL11.glNormalPointer(5126, 0, 0L);
        GL15.glBindBuffer(34962, this.texcoordBuffer);
        GL11.glTexCoordPointer(2, 5126, 0, 0L);
        GL11.glEnableClientState(32884);
        GL11.glEnableClientState(32885);
        GL11.glEnableClientState(32888);
        GL15.glBindBuffer(34963, this.indexBuffer);
        GL11.glDrawElements(4, this.data.indexData.length, 5125, 0L);
        GL15.glBindBuffer(34963, 0);
        GL15.glBindBuffer(34962, 0);
        GL11.glDisableClientState(32884);
        GL11.glDisableClientState(32885);
        GL11.glDisableClientState(32888);
        if (smooth && normals)
            GL11.glShadeModel(7424);
        if (!normals)
            RenderHelper.enableStandardItemLighting();
        if (!lighting)
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
        if (!mc.gameSettings.showDebugInfo || !mc.gameSettings.hideGUI) ;
        this.alpha = 1.0F;
    }

    private ResourceLocation getTexture(AnimationMeshConfig config) {
        if (config == null)
            return this.texture;
        return (config.texture == null) ? this.texture : config.texture;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\AnimationMesh.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */