package co.crystaldev.client.shader;

import co.crystaldev.client.util.ArrayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.OpenGLException;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderLoader {
  private final ShaderType shaderType;
  
  private final String fileName;
  
  private final int shader;
  
  private int shaderAttachCount = 0;
  
  private ShaderLoader(ShaderType type, int shaderId, String filename) {
    this.shaderType = type;
    this.shader = shaderId;
    this.fileName = filename;
  }
  
  public void attachShader(Shader shader) {
    this.shaderAttachCount++;
    ShaderHelper.getInstance().glAttachShader(shader.getProgram(), this.shader);
  }
  
  public void deleteShader() {
    this.shaderAttachCount--;
    if (this.shaderAttachCount <= 0) {
      ShaderHelper.getInstance().glDeleteShader(this.shader);
      this.shaderType.getSavedShaderLoaders().remove(this.fileName);
    } 
  }
  
  public static ShaderLoader load(ShaderType type, String fileName) throws Exception {
    ShaderLoader shaderLoader = type.getSavedShaderLoaders().get(fileName);
    if (shaderLoader == null) {
      ResourceLocation resourceLocation = new ResourceLocation("crystalclient", "shaders/program/" + fileName + type.getShaderExtension());
      BufferedInputStream bufferedInputStream = new BufferedInputStream(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());
      byte[] bytes = ArrayUtils.toByteArray(bufferedInputStream);
      ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
      buffer.put(bytes);
      buffer.position(0);
      int shaderID = ShaderHelper.getInstance().glCreateShader(type.getGlShaderType());
      ShaderHelper.getInstance().glShaderSource(shaderID, buffer);
      ShaderHelper.getInstance().glCompileShader(shaderID);
      if (ShaderHelper.getInstance().glGetShaderi(shaderID, (ShaderHelper.getInstance()).GL_COMPILE_STATUS) == 0)
        throw new OpenGLException("An error occurred while compiling shader " + fileName + ": " + StringUtils.trim(ShaderHelper.getInstance().glGetShaderInfoLog(shaderID, 32768))); 
      shaderLoader = new ShaderLoader(type, shaderID, fileName);
      type.getSavedShaderLoaders().put(fileName, shaderLoader);
    } 
    return shaderLoader;
  }
  
  public enum ShaderType {
    VERTEX(".vsh", (ShaderHelper.getInstance()).GL_VERTEX_SHADER),
    FRAGMENT(".fsh", (ShaderHelper.getInstance()).GL_FRAGMENT_SHADER);
    
    private final Map<String, ShaderLoader> savedShaderLoaders = new HashMap<>();
    
    private final String shaderExtension;
    
    private final int glShaderType;
    
    ShaderType(String extension, int glShaderType) {
      this.shaderExtension = extension;
      this.glShaderType = glShaderType;
    }
    
    public String getShaderExtension() {
      return this.shaderExtension;
    }
    
    public int getGlShaderType() {
      return this.glShaderType;
    }
    
    public Map<String, ShaderLoader> getSavedShaderLoaders() {
      return this.savedShaderLoaders;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\ShaderLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */