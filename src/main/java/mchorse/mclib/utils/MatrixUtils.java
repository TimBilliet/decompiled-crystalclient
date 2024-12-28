package mchorse.mclib.utils;

import co.crystaldev.client.util.javax.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class MatrixUtils {
  public static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
  
  public static final float[] floats = new float[16];
  
  public static Matrix4f matrix;
  
  public static Matrix4f readModelView(Matrix4f matrix4f) {
    buffer.clear();
    GL11.glGetFloat(2982, buffer);
    buffer.get(floats);
    matrix4f.set(floats);
    matrix4f.transpose();
    return matrix4f;
  }
  
  public static void loadModelView(Matrix4f matrix4f) {
    matrixToFloat(floats, matrix4f);
    buffer.clear();
    buffer.put(floats);
    buffer.rewind();
    GL11.glLoadMatrix(buffer);
  }
  
  public static void matrixToFloat(float[] floats, Matrix4f matrix4f) {
    floats[0] = matrix4f.m00;
    floats[1] = matrix4f.m01;
    floats[2] = matrix4f.m02;
    floats[3] = matrix4f.m03;
    floats[4] = matrix4f.m10;
    floats[5] = matrix4f.m11;
    floats[6] = matrix4f.m12;
    floats[7] = matrix4f.m13;
    floats[8] = matrix4f.m20;
    floats[9] = matrix4f.m21;
    floats[10] = matrix4f.m22;
    floats[11] = matrix4f.m23;
    floats[12] = matrix4f.m30;
    floats[13] = matrix4f.m31;
    floats[14] = matrix4f.m32;
    floats[15] = matrix4f.m33;
  }
  
  public static boolean captureMatrix() {
    if (matrix == null) {
      matrix = readModelView(new Matrix4f());
      return true;
    } 
    return false;
  }
  
  public static void releaseMatrix() {
    matrix = null;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\MatrixUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */