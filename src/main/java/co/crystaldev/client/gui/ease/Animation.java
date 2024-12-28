package co.crystaldev.client.gui.ease;


import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.NotNull;

public class Animation implements Cloneable {
  private float value;
  
  private long startTime;
  
  private final long duration;
  
  private final float start;
  
  private final float end;
  
  private IEasingFunction easing;
  
  public IEasingFunction getEasing() {
    return this.easing;
  }
  
  public Animation(float duration, float start, float end) {
    this((long)(duration * 1000.0F), start, end, Easing.LINEAR);
  }
  
  public Animation(float duration, float start, float end, IEasingFunction easing) {
    this((long)(duration * 1000.0F), start, end, easing);
  }
  
  public Animation(long duration, float start, float end) {
    this(duration, start, end, Easing.LINEAR);
  }
  
  public Animation(long duration, float start, float end, IEasingFunction easing) {
    this.duration = duration;
    this.start = this.value = start;
    this.end = end;
    this.easing = easing;
    this.startTime = System.currentTimeMillis();
  }
  
  public void reset() {
    this.startTime = System.currentTimeMillis();
    this.value = this.start;
  }
  
  public Animation setComplete(float complete) {
    complete = MathHelper.clamp_float(complete, 0.0F, 1.0F);
    this.startTime = System.currentTimeMillis();
    this.startTime -= (int)((float)this.duration * complete);
    return this;
  }
  
  public boolean isComplete() {
    return (this.start > this.end) ? ((this.value <= this.end)) : ((this.value >= this.end));
  }
  
  public float getValue() {
    return getEased((this.easing == null) ? (this.easing = Easing.LINEAR) : this.easing);
  }
  
  public float getComplete() {
    return Math.min((float)(System.currentTimeMillis() - this.startTime) / (float)this.duration, 1.0F);
  }
  
  public float getEased(@NotNull IEasingFunction easing) {
    float eased = easing.getValue(getComplete());
    float diff = this.end - this.start;
    return this.value = diff * eased + this.start;
  }
  
  public Animation clone() {
    try {
      return (Animation)super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new AssertionError();
    } 
  }
  
  public static boolean matches(Animation animation, Animation other) {
    return (animation.start == other.start && animation.end == other.end && animation.easing == other.easing);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\ease\Animation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */