package co.crystaldev.client.shader;

import co.crystaldev.client.Reference;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

public class ShaderHelper {
  public static final ShaderHelper instance = new ShaderHelper();
  
  private boolean shadersSupported;
  
  private boolean vbosSupported;
  
  private boolean vaosSupported;
  
  private boolean usingARBShaders;
  
  private boolean usingARBVbos;
  
  private boolean usingARBVaos;
  
  public int GL_LINK_STATUS;
  
  public int GL_ARRAY_BUFFER;
  
  public int GL_DYNAMIC_DRAW;
  
  public int GL_COMPILE_STATUS;
  
  public int GL_VERTEX_SHADER;
  
  public int GL_FRAGMENT_SHADER;
  
  public ShaderHelper() {
    checkCapabilities();
  }
  
  private void checkCapabilities() {
    StringBuilder sb = new StringBuilder();
    ContextCapabilities capabilities = GLContext.getCapabilities();
    boolean openGL33Supported = capabilities.OpenGL30;
    this.vaosSupported = (openGL33Supported || capabilities.GL_ARB_vertex_array_object);
    sb.append("VAOs are ").append(this.vaosSupported ? "" : "not ").append("available. ");
    if (this.vaosSupported) {
      if (capabilities.OpenGL30) {
        sb.append("OpenGL 3.0 is supported. ");
        this.usingARBVaos = false;
      } else {
        sb.append("GL_ARB_vertex_array_object is supported. ");
        this.usingARBVaos = true;
      } 
    } else {
      sb.append("OpenGL 3.0 is not supported and GL_ARB_vertex_array_object is not supported. ");
    } 
    boolean openGL21Supported = capabilities.OpenGL20;
    this.shadersSupported = (openGL21Supported || (capabilities.GL_ARB_vertex_shader && capabilities.GL_ARB_fragment_shader && capabilities.GL_ARB_shader_objects));
    sb.append("Shaders are ").append(this.shadersSupported ? "" : "not ").append("available. ");
    if (this.shadersSupported) {
      if (capabilities.OpenGL20) {
        sb.append("OpenGL 2.0 is supported. ");
        this.usingARBShaders = false;
      } else {
        sb.append("ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported. ");
        this.usingARBShaders = true;
      } 
      this.GL_LINK_STATUS = 35714;
      this.GL_COMPILE_STATUS = 35713;
      this.GL_VERTEX_SHADER = 35633;
      this.GL_FRAGMENT_SHADER = 35632;
    } else {
      sb.append("OpenGL 2.0 is not supported and ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are not supported. ");
    } 
    this.usingARBVbos = (!capabilities.OpenGL15 && capabilities.GL_ARB_vertex_buffer_object);
    this.vbosSupported = (capabilities.OpenGL15 || this.usingARBVbos);
    sb.append("VBOs are ").append(this.vbosSupported ? "" : "not ").append("available. ");
    if (this.vbosSupported) {
      if (this.usingARBVbos) {
        sb.append("ARB_vertex_buffer_object is supported. ");
      } else {
        sb.append("OpenGL 1.5 is supported. ");
      } 
      this.GL_ARRAY_BUFFER = 34962;
      this.GL_DYNAMIC_DRAW = 35048;
    } else {
      sb.append("OpenGL 1.5 is not supported and ARB_vertex_buffer_object is not supported. ");
    } 
    Reference.LOGGER.info(sb.toString());
  }
  
  public void glLinkProgram(int program) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glLinkProgramARB(program);
    } else {
      GL20.glLinkProgram(program);
    } 
  }
  
  public String glGetProgramInfoLog(int program, int maxLength) {
    return this.usingARBShaders ? ARBShaderObjects.glGetInfoLogARB(program, maxLength) : GL20.glGetProgramInfoLog(program, maxLength);
  }
  
  public int glGetProgrami(int program, int pname) {
    return this.usingARBShaders ? ARBShaderObjects.glGetObjectParameteriARB(program, pname) : GL20.glGetProgrami(program, pname);
  }
  
  public void glUseProgram(int program) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glUseProgramObjectARB(program);
    } else {
      GL20.glUseProgram(program);
    } 
  }
  
  public void glBindBuffer(int target, int buffer) {
    if (this.usingARBVbos) {
      ARBVertexBufferObject.glBindBufferARB(target, buffer);
    } else {
      GL15.glBindBuffer(target, buffer);
    } 
  }
  
  public void glBufferData(int target, ByteBuffer data, int usage) {
    if (this.usingARBVbos) {
      ARBVertexBufferObject.glBufferDataARB(target, data, usage);
    } else {
      GL15.glBufferData(target, data, usage);
    } 
  }
  
  public int glGenBuffers() {
    return this.usingARBVbos ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
  }
  
  public void glAttachShader(int program, int shaderIn) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glAttachObjectARB(program, shaderIn);
    } else {
      GL20.glAttachShader(program, shaderIn);
    } 
  }
  
  public void glDeleteShader(int p_153180_0_) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glDeleteObjectARB(p_153180_0_);
    } else {
      GL20.glDeleteShader(p_153180_0_);
    } 
  }
  
  public int glCreateShader(int type) {
    return this.usingARBShaders ? ARBShaderObjects.glCreateShaderObjectARB(type) : GL20.glCreateShader(type);
  }
  
  public void glShaderSource(int shaderIn, ByteBuffer string) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glShaderSourceARB(shaderIn, string);
    } else {
      GL20.glShaderSource(shaderIn, string);
    } 
  }
  
  public void glCompileShader(int shaderIn) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glCompileShaderARB(shaderIn);
    } else {
      GL20.glCompileShader(shaderIn);
    } 
  }
  
  public int glGetShaderi(int shaderIn, int pname) {
    return this.usingARBShaders ? ARBShaderObjects.glGetObjectParameteriARB(shaderIn, pname) : GL20.glGetShaderi(shaderIn, pname);
  }
  
  public String glGetShaderInfoLog(int shaderIn, int maxLength) {
    return this.usingARBShaders ? ARBShaderObjects.glGetInfoLogARB(shaderIn, maxLength) : GL20.glGetShaderInfoLog(shaderIn, maxLength);
  }
  
  public void glUniform1f(int location, float v0) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glUniform1fARB(location, v0);
    } else {
      GL20.glUniform1f(location, v0);
    } 
  }
  
  public void glUniform3f(int location, float v0, float v1, float v2) {
    if (this.usingARBShaders) {
      ARBShaderObjects.glUniform3fARB(location, v0, v1, v2);
    } else {
      GL20.glUniform3f(location, v0, v1, v2);
    } 
  }
  
  public void glEnableVertexAttribArray(int index) {
    if (this.usingARBShaders) {
      ARBVertexShader.glEnableVertexAttribArrayARB(index);
    } else {
      GL20.glEnableVertexAttribArray(index);
    } 
  }
  
  public int glGetUniformLocation(int programObj, CharSequence name) {
    return this.usingARBShaders ? ARBShaderObjects.glGetUniformLocationARB(programObj, name) : GL20.glGetUniformLocation(programObj, name);
  }
  
  public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long buffer_buffer_offset) {
    if (this.usingARBShaders) {
      ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, buffer_buffer_offset);
    } else {
      GL20.glVertexAttribPointer(index, size, type, normalized, stride, buffer_buffer_offset);
    } 
  }
  
  public int glGenVertexArrays() {
    return this.usingARBVaos ? ARBVertexArrayObject.glGenVertexArrays() : GL30.glGenVertexArrays();
  }
  
  public void glBindVertexArray(int array) {
    if (this.usingARBVaos) {
      ARBVertexArrayObject.glBindVertexArray(array);
    } else {
      GL30.glBindVertexArray(array);
    } 
  }
  
  public static ShaderHelper getInstance() {
    return instance;
  }
  
  public boolean isShadersSupported() {
    return this.shadersSupported;
  }
  
  public boolean isVbosSupported() {
    return this.vbosSupported;
  }
  
  public boolean isVaosSupported() {
    return this.vaosSupported;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\ShaderHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */