package co.crystaldev.client.shader;

public class UniformType<T> {
  public static final UniformType<Float> FLOAT = new UniformType(1);
  
  public static final UniformType<Integer> INT = new UniformType(1);
  
  public static final UniformType<Float[]> VEC2 = new UniformType(2);
  
  public static final UniformType<Float[]> VEC3 = new UniformType(3);
  
  private final int amount;
  
  public int getAmount() {
    return this.amount;
  }
  
  public UniformType(int amount) {
    this.amount = amount;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\UniformType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */