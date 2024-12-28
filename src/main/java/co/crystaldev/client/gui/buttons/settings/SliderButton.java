package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.Reference;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SliderButton extends SettingButton<Double> {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

  private static final long ANIMATION_DURATION = 100L;

  private final String placeholder;

  private boolean useInts = false;

  private final int sliderHeight;

  private final double offset;

  private final double standard;

  private double min;

  private double max;

  private boolean selecting = false;

  private final FadingColor textColor;

  private final FadingColor barColor;

  private final FadingColor knobColor;

  private Animation positionAnimation;

  public SliderButton(int id, int x, int y, int width, int height, String displayText, String placeholder, double currentValue, double min, double max, double standard) {
    super(id, x, y, width, height, displayText, currentValue);
    this.placeholder = placeholder;
    this.min = min;
    this.max = max;
    this.standard = standard;
    if (this.min < 0.0D) {
      this.offset = Math.abs(this.min);
      SliderButton sliderButton = this;
      sliderButton.currentValue = sliderButton.currentValue + this.offset;
      this.min += this.offset;
      this.max += this.offset;
    } else if (this.min > 0.0D) {
      this.offset = -Math.abs(this.min);
      SliderButton sliderButton = this;
      sliderButton.currentValue = sliderButton.currentValue + this.offset;
      this.min += this.offset;
      this.max += this.offset;
    } else {
      this.offset = 0.0D;
    }
    this.sliderHeight = this.height / 3;
    this.positionAnimation = new Animation(1L, getSliderX(), getSliderX());
    this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.barColor = new FadingColor(this.opts.getColor(this.opts.mainColor, 100), this.opts.getColor(this.opts.mainColor, 180));
    this.knobColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
  }

    public SliderButton(int id, int x, int y, int width, int height, String displayText, String placeholder, int currentValue, int min, int max, int standard) {
    this(id, x, y, width, height, displayText, placeholder, currentValue, min, max, (double)standard);
    this.useInts = true;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    boolean overSlider = (this.selecting || (hovered && isOverSlider(mouseX, mouseY)));
    this.textColor.fade(hovered);
    this.barColor.fade(overSlider);
    this.knobColor.fade(overSlider);
    RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.opts.neutralButtonBackground.getRGB());
    this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
    String f = this.placeholder.replace("{value}", DECIMAL_FORMAT.format(getCurrent()));
    this.fontRenderer.drawString(f, this.x + this.width / 2 - 7 - this.fontRenderer.getStringWidth(f), this.y + this.height / 2 - this.fontRenderer
        .getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
    RenderUtils.drawRoundedRect(this.x + this.width / 2.0D, this.y + this.height / 2.0D - this.sliderHeight / 2.0D, (this.x + this.width - 3), this.y + this.height / 2.0D + this.sliderHeight / 2.0D, 5.0D, this.opts.neutralButtonBackground

        .getRGB());
    float currentWidth = this.positionAnimation.getValue();
    if (currentWidth > 2.5F)
      RenderUtils.drawRoundedRect(this.x + this.width / 2.0D, this.y + this.height / 2.0D - this.sliderHeight / 2.0D,
          Math.min(currentWidth, (this.x + this.width - 3)), this.y + this.height / 2.0D + this.sliderHeight / 2.0D, 5.0D, this.barColor
          .getCurrentColor().getRGB());
    RenderUtils.drawCircle(Math.min(currentWidth, (this.x + this.width - 3)), this.y + this.height / 2.0F, 17.0F, this.knobColor
        .getCurrentColor().getRGB());
    double old = this.currentValue;
    if (Mouse.isButtonDown(0) && this.selecting) {
      this.currentValue = mouseToDouble(mouseX);
      this.positionAnimation = new Animation(100L, this.positionAnimation.getValue(), getSliderX());
    } else if (!Mouse.isButtonDown(0) && this.selecting) {
      this.selecting = false;
    }
    if (old != this.currentValue)
      save();
    Screen.scissorEnd(this.scissorPane);
  }

  public boolean shouldOverlayBeRendered(int mouseX, int mouseY) {
    return (mouseX < this.x + this.width / 2);
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    if (mouseButton == 0) {
      if (!isOverSlider(mouseX, mouseY))
        return;
      this.selecting = true;
    } else if (mouseButton == 2) {
      setValue(this.standard + this.offset);
      this.positionAnimation = new Animation(100L, this.positionAnimation.getValue(), getSliderX());
    }
  }

  public void save() {
    if (this.settingObject != null && this.settingField != null)
      try {
        if (this.useInts) {
          this.settingField.setInt(this.settingObject, (int)getCurrent());
        } else {
          this.settingField.setDouble(this.settingObject, getCurrent());
        }
      } catch (IllegalAccessException ex) {
        Reference.LOGGER.error("Unable to assign field to value", ex);
      }
  }

  private boolean isOverSlider(int mouseX, int mouseY) {
    return (mouseX >= this.x + this.width / 2 && mouseX <= this.x + this.width && mouseY <= this.y + this.height / 2 + this.sliderHeight / 2 && mouseY >= this.y + this.height / 2 - this.sliderHeight / 2);
  }

  private double mouseToDouble(int mouseX) {
    int x = this.x + this.width / 2;
    int w = this.width / 2 - 3;
    if (mouseX <= x)
      return this.min;
    if (mouseX >= x + w)
      return this.max;
    double percent = ((float)Math.abs(mouseX - x) / w) * (this.max - this.min) + this.min;//float
    if (this.useInts)
      return MathHelper.clamp_double(Math.round(percent), this.min, this.max);
    BigDecimal bd = new BigDecimal(Double.toString(percent));
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    return MathHelper.clamp_double(bd.doubleValue(), this.min, this.max);
  }

  private int getSliderX() {
    double range = this.max - this.min;
    return this.x + this.width / 2 + (int)(this.currentValue / range * (this.width / 2 - 3));
  }

  public double getCurrent() {
    return this.currentValue - this.offset;
  }

  public double getMin() {
    return this.min - this.offset;
  }

  public double getMax() {
    return this.max - this.offset;
  }
}