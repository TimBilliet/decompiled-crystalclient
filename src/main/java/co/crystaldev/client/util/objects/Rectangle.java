package co.crystaldev.client.util.objects;

public class Rectangle {
  public int left;
  
  public int top;
  
  public int right;
  
  public int bottom;
  
  public Rectangle(int x1, int y1, int x2, int y2) {
    this.left = Math.min(x1, x2);
    this.top = Math.min(y1, y2);
    this.right = Math.max(x1, x2);
    this.bottom = Math.max(y1, y2);
  }
  
  public boolean intersects(Rectangle other) {
    return (this.left < other.right && this.right > other.left && this.top < other.bottom && this.bottom > other.top);
  }
  
  public boolean encapsulates(Rectangle other) {
    return (this.left <= other.left && this.right >= other.right && this.top <= other.top && this.bottom >= other.bottom);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Rectangle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */