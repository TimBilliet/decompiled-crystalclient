package mchorse.mclib.utils;

public class MathUtils {
  public static int clamp(int x, int min, int max) {
    return (x < min) ? min : ((x > max) ? max : x);
  }
  
  public static float clamp(float x, float min, float max) {
    return (x < min) ? min : ((x > max) ? max : x);
  }
  
  public static double clamp(double x, double min, double max) {
    return (x < min) ? min : ((x > max) ? max : x);
  }
  
  public static int cycler(int x, int min, int max) {
    return (x < min) ? max : ((x > max) ? min : x);
  }
  
  public static float cycler(float x, float min, float max) {
    return (x < min) ? max : ((x > max) ? min : x);
  }
  
  public static double cycler(double x, double min, double max) {
    return (x < min) ? max : ((x > max) ? min : x);
  }
  
  public static int gridIndex(int x, int y, int size, int width) {
    x /= size;
    y /= size;
    return x + y * width / size;
  }
  
  public static int gridRows(int count, int size, int width) {
    double x = (count * size) / width;
    return (count <= 0) ? 1 : (int)Math.ceil(x);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\MathUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */