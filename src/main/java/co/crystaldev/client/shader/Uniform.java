package co.crystaldev.client.shader;

import java.util.Objects;
import java.util.function.Supplier;

public class Uniform<T> {
    private final UniformType<T> uniformType;

    private final Supplier<T> uniformValuesSupplier;

    private final String name;

    private int uniformID;

    private T previousUniformValue;

    public Uniform(Shader shader, UniformType<T> uniformType, String name, Supplier<T> uniformValuesSupplier) {
        this.uniformType = uniformType;
        this.uniformValuesSupplier = uniformValuesSupplier;
        this.name = name;
        init(shader, name);
    }

    private void init(Shader shader, String name) {
        this.uniformID = ShaderHelper.getInstance().glGetUniformLocation(shader.getProgram(), name);
    }

    public void update() {
        T newUniformValue = this.uniformValuesSupplier.get();
        if (!Objects.deepEquals(this.previousUniformValue, newUniformValue)) {
            if (this.uniformType == UniformType.FLOAT) {
                ShaderHelper.getInstance().glUniform1f(this.uniformID, (Float) newUniformValue);
            } else if (this.uniformType == UniformType.VEC3) {
                Float[] values = (Float[]) newUniformValue;
                ShaderHelper.getInstance().glUniform3f(this.uniformID, values[0], values[1], values[2]);
            }
            this.previousUniformValue = newUniformValue;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\Uniform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */