package co.crystaldev.client.util.javax;

import java.io.Serializable;

public class Point2f extends Tuple2f implements Serializable {
  static final long serialVersionUID = -4801347926528714435L;
  
  public Point2f(float x, float y) {
    super(x, y);
  }
  
  public Point2f(float[] p) {
    super(p);
  }
  
  public Point2f(Point2f p1) {
    super(p1);
  }
  
  public Point2f(Point2d p1) {
    super(p1);
  }
  
  public Point2f(Tuple2d t1) {
    super(t1);
  }
  
  public Point2f(Tuple2f t1) {
    super(t1);
  }
  
  public Point2f() {}
  
  public final float distanceSquared(Point2f p1) {
    float dx = this.x - p1.x;
    float dy = this.y - p1.y;
    return dx * dx + dy * dy;
  }
  
  public final float distance(Point2f p1) {
    float dx = this.x - p1.x;
    float dy = this.y - p1.y;
    return (float)Math.sqrt((dx * dx + dy * dy));
  }
  
  public final float distanceL1(Point2f p1) {
    return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y);
  }
  
  public final float distanceLinf(Point2f p1) {
    return Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\javax\Point2f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */