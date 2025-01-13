package co.crystaldev.client.shader;

import co.crystaldev.client.Reference;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static final ShaderManager INSTANCE = new ShaderManager();

    private final Map<Class<? extends Shader>, Shader> shaders = new HashMap<>();

    private Class<? extends Shader> activeShaderType;

    private Shader activeShader;

    public <T extends Shader> T getShader(Class<T> shaderClass) {
        Shader shader = this.shaders.get(shaderClass);
        if (shader == null) {
            shader = newInstance(shaderClass);
            this.shaders.put(shaderClass, shader);
        }
        return (T) shader;
    }

    public <T extends Shader> T enableShader(T shader) {
        if (shader == null)
            return null;
        if (this.activeShaderType == shader.getClass())
            return (T) this.activeShader;
        if (this.activeShader != null)
            disableShader();
        this.activeShaderType = (Class) shader.getClass();
        this.activeShader = (Shader) shader;
        this.activeShader.enable();
        this.activeShader.updateUniforms();
        return shader;
    }

    public <T extends Shader> T enableShader(Class<T> shaderClass) {
        if (this.activeShaderType == shaderClass)
            return (T) this.activeShader;
        if (this.activeShader != null)
            disableShader();
        Shader shader = this.shaders.get(shaderClass);
        if (shader == null) {
            shader = newInstance(shaderClass);
            this.shaders.put(shaderClass, shader);
        }
        if (shader == null)
            return null;
        this.activeShaderType = shaderClass;
        this.activeShader = shader;
        this.activeShader.enable();
        this.activeShader.updateUniforms();
        return (T) shader;
    }

    private <T extends Shader> T newInstance(Class<T> shaderClass) {
        try {
            return (T) shaderClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception ex) {
            Reference.LOGGER.error("An exception was thrown while constructing a shader", ex);
            return null;
        }
    }

    public void disableShader() {
        if (this.activeShader != null) {
            this.activeShader.disable();
            this.activeShaderType = null;
            this.activeShader = null;
        }
    }

    public boolean isShaderEnabled() {
        return (this.activeShader != null);
    }

    public boolean onRenderWorldRendererBuffer() {
        return (isShaderEnabled() && !this.activeShader.isUsingFixedPipeline());
    }

    public boolean areShadersSupported() {
        return ShaderHelper.getInstance().isShadersSupported();
    }

    public Map<Class<? extends Shader>, Shader> getShaders() {
        return this.shaders;
    }

    public Class<? extends Shader> getActiveShaderType() {
        return this.activeShaderType;
    }

    public Shader getActiveShader() {
        return this.activeShader;
    }

    public static ShaderManager getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\ShaderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */