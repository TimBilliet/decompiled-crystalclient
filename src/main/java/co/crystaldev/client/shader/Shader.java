package co.crystaldev.client.shader;

import net.minecraft.client.shader.ShaderLinkHelper;
import org.lwjgl.opengl.OpenGLException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Shader {
  private final String vertex;
  
  private final String fragment;
  
  private final VertexFormat vertexFormat;
  
  protected int program;
  
  private final List<Uniform<?>> uniforms;
  
  public Shader(String vertex, String fragment) throws Exception {
    this(vertex, fragment, null);
  }
  
  private Shader(String vertex, String fragment, VertexFormat vertexFormat) throws Exception {
    this.uniforms = new ArrayList<>();
    this.vertex = vertex;
    this.fragment = fragment;
    this.vertexFormat = vertexFormat;
    init();
  }
  
  private void init() throws Exception {
    this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
    if (this.vertex != null) {
      ShaderLoader fragmentShaderLoader = ShaderLoader.load(ShaderLoader.ShaderType.VERTEX, this.vertex);
      fragmentShaderLoader.attachShader(this);
    } 
    if (this.fragment != null) {
      ShaderLoader fragmentShaderLoader = ShaderLoader.load(ShaderLoader.ShaderType.FRAGMENT, this.fragment);
      fragmentShaderLoader.attachShader(this);
    } 
    ShaderHelper.getInstance().glLinkProgram(this.program);
    int linkStatus = ShaderHelper.getInstance().glGetProgrami(this.program, (ShaderHelper.getInstance()).GL_LINK_STATUS);
    if (linkStatus == 0)
      throw new OpenGLException("Error encountered when linking program containing VS " + this.vertex + " and FS " + this.fragment + ": " + ShaderHelper.getInstance().glGetProgramInfoLog(this.program, 32768)); 
    registerUniforms();
  }
  
  protected void registerUniforms() {}
  
  public void updateUniforms() {
    for (Uniform<?> uniform : this.uniforms)
      uniform.update(); 
  }
  
  public void enable() {
    ShaderHelper.getInstance().glUseProgram(this.program);
  }
  
  public void disable() {
    ShaderHelper.getInstance().glUseProgram(0);
  }
  
  public boolean isUsingFixedPipeline() {
    return (this.vertexFormat == null);
  }
  
  public <T> void registerUniform(UniformType<T> uniformType, String name, Supplier<T> uniformValuesSupplier) {
    this.uniforms.add(new Uniform<>(this, uniformType, name, uniformValuesSupplier));
  }
  
  public int getProgram() {
    return this.program;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\Shader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */