package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.biome.BiomeGenBase;

@ModuleInfo(name = "Coordinates", description = "Displays your current coordinates onscreen", category = Category.HUD)
public class Coordinates extends HudModuleBackground implements IRegistrable {
  @Toggle(label = "Show Labels")
  public boolean showLabels = true;
  
  @Toggle(label = "Display Cardinal Direction")
  public boolean displayDirection = true;
  
  @Toggle(label = "Display Axis Direction")
  public boolean displayAxisDirection = true;
  
  @Toggle(label = "Show Biome")
  public boolean showBiome = true;
  
  @Toggle(label = "Dynamic Biome Color")
  public boolean dynamicBiomeColor = true;
  
  @Toggle(label = "Middle Click Looking-at Coords")
  public boolean middleClickLookingAt = false;
  
  @Keybind(label = "Shout Coordinates")
  public KeyBinding shoutKey = new KeyBinding("crystalclient.key.shout_current_location", 0, "Crystal Client");
  
  @Toggle(label = "Use /ff")
  public boolean useFactionChat = false;
  
  @Selector(label = "Mode", values = {"Horizontal", "Vertical"})
  public String mode = "Vertical";
  
  @Colour(label = "Label Color")
  public ColorObject labelColor = new ColorObject(255, 255, 255, 255);
  
  @Colour(label = "Value Color")
  public ColorObject valueColor = new ColorObject(240, 240, 240, 255);
  
  @Colour(label = "Direction Color")
  public ColorObject directionColor = new ColorObject(240, 240, 240, 255);
  
  private static final String[] directions = new String[] { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
  
  private static final String[] directionX = new String[] { "", "(+)", "(+)", "(+)", "", "(-)", "(-)", "(-)" };
  
  private static final String[] directionZ = new String[] { "(-)", "(-)", "", "(+)", "(+)", "(+)", "", "(-)" };
  
  private final FontRenderer fr;
  
  public Coordinates() {
    this.enabled = true;
    this.hasInfoHud = true;
    this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 5.0F, 5.0F);
    this.width = 125;
    this.height = 45;
    this.fr = this.mc.fontRendererObj;
  }
  
  public void configPostInit() {
    super.configPostInit();
    setOptionVisibility("Show Biome", f -> this.mode.equals("Vertical"));
    setOptionVisibility("Dynamic Biome Color", f -> (this.mode.equals("Vertical") && this.showBiome));
    setOptionVisibility("Display Cardinal Direction", f -> this.mode.equals("Vertical"));
  }
  
  public String getDisplayText() {
    return null;
  }
  
  public Tuple<String, String> getInfoHud() {
    BlockPos pos = new BlockPos((this.mc.getRenderViewEntity()).posX, (this.mc.getRenderViewEntity().getEntityBoundingBox()).minY, (this.mc.getRenderViewEntity()).posZ);
    int px = MathHelper.floor_double(pos.getX());
    int py = MathHelper.floor_double(pos.getY());
    int pz = MathHelper.floor_double(pos.getZ());
    return new Tuple("XYZ", String.format("%d, %d, %d", new Object[] { Integer.valueOf(px), Integer.valueOf(py), Integer.valueOf(pz) }));
  }
  
  public void draw() {
    if (this.mc.theWorld == null || this.mc.thePlayer == null)
      return; 
    int renderX = getRenderX();
    int renderY = getRenderY();
    int margin = 4;
    BlockPos pos = new BlockPos((this.mc.getRenderViewEntity()).posX, (this.mc.getRenderViewEntity().getEntityBoundingBox()).minY, (this.mc.getRenderViewEntity()).posZ);
    int px = MathHelper.floor_double(pos.getX());
    int py = MathHelper.floor_double(pos.getY());
    int pz = MathHelper.floor_double(pos.getZ());
    int direction = getDirection(this.mc.thePlayer.rotationYaw);
    BiomeGenBase biome = this.mc.theWorld.getBiomeGenForCoords(pos);
    int x = renderX + 4;
    int y = renderY + 4;
    if (this.drawBackground)
      RenderUtils.drawRect(renderX, renderY, (renderX + this.width), (renderY + this.height), this.backgroundColor); 
    if (this.mode.equalsIgnoreCase("Vertical")) {
      RenderUtils.drawString(Integer.toString(px), RenderUtils.drawString(this.showLabels ? "X: " : "", x, y, this.labelColor), y, this.valueColor);
      if (this.displayAxisDirection)
        RenderUtils.drawString(directionX[direction].replaceAll("[()]", ""), renderX + this.width - 4 - this.fr.getStringWidth("-"), y, this.directionColor); 
      y += this.fr.FONT_HEIGHT;
      RenderUtils.drawString(Integer.toString(py), RenderUtils.drawString(this.showLabels ? "Y: " : "", x, y, this.labelColor), y, this.valueColor);
      if (this.displayDirection)
        RenderUtils.drawString(directions[direction], renderX + this.width - 4 - this.fr.getStringWidth(directions[direction]), y, this.directionColor); 
      y += this.fr.FONT_HEIGHT;
      RenderUtils.drawString(Integer.toString(pz), RenderUtils.drawString(this.showLabels ? "Z: " : "", x, y, this.labelColor), y, this.valueColor);
      if (this.displayAxisDirection)
        RenderUtils.drawString(directionZ[direction].replaceAll("[()]", ""), renderX + this.width - 4 - this.fr.getStringWidth("-"), y, this.directionColor); 
      y += this.fr.FONT_HEIGHT;
      if (this.showBiome) {
        if (this.dynamicBiomeColor) {
          RenderUtils.drawString(biome.biomeName, RenderUtils.drawString(this.showLabels ? "Biome: " : "", x, y, this.labelColor), y, biome.color);
        } else {
          RenderUtils.drawString(biome.biomeName, RenderUtils.drawString(this.showLabels ? "Biome: " : "", x, y, this.labelColor), y, this.valueColor);
        } 
        y += this.fr.FONT_HEIGHT;
      } 
      this.width = 125;
      this.height = y + 4 - renderY - 1;
    } else {
      if (!this.drawBackground)
        x = RenderUtils.drawString("[", x, y, this.textColor); 
      if (this.showLabels)
        x = RenderUtils.drawString("X: ", x, y, this.labelColor); 
      x = RenderUtils.drawString(px + "", x, y, this.valueColor);
      if (this.displayAxisDirection && !directionX[direction].isEmpty())
        x = RenderUtils.drawString(" " + directionX[direction], x, y, this.directionColor); 
      x = RenderUtils.drawString(", ", x, y, this.valueColor);
      if (this.showLabels)
        x = RenderUtils.drawString("Y: ", x, y, this.labelColor); 
      x = RenderUtils.drawString(py + "", x, y, this.valueColor);
      x = RenderUtils.drawString(", ", x, y, this.valueColor);
      if (this.showLabels)
        x = RenderUtils.drawString("Z: ", x, y, this.labelColor); 
      x = RenderUtils.drawString(pz + "", x, y, this.valueColor);
      if (this.displayAxisDirection && !directionZ[direction].isEmpty())
        x = RenderUtils.drawString(" " + directionZ[direction], x, y, this.directionColor); 
      if (!this.drawBackground)
        x = RenderUtils.drawString("]", x, y, this.textColor); 
      this.height = this.fr.FONT_HEIGHT + 8;
      this.width = x - renderX + 4;
    } 
  }
  
  private int getDirection(float yaw) {
    double point = MathHelper.wrapAngleTo180_float(yaw) + 180.0D;
    point += 22.5D;
    point %= 360.0D;
    point /= 45.0D;
    return MathHelper.floor_double(point);
  }
  
  public void registerEvents() {
    EventBus.register(this, InputEvent.Mouse.class, ev -> {
          if (ev.button == 2 && ev.buttonState && this.middleClickLookingAt && this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            BlockPos pos = this.mc.objectMouseOver.getBlockPos();
            this.mc.thePlayer.sendChatMessage(String.format((this.useFactionChat ? "/ff " : "") + "[Looking At] x:%d y:%d z:%d", new Object[] { Integer.valueOf(pos.getX()), Integer.valueOf(pos.getY()), Integer.valueOf(pos.getZ()) }));
          } 
        });
    EventBus.register(this, InputEvent.Key.class, ev -> {
          if (this.mc.thePlayer != null && this.shoutKey.isPressed()) {
            int px = MathHelper.floor_double(this.mc.thePlayer.posX);
            int py = MathHelper.floor_double(this.mc.thePlayer.posY);
            int pz = MathHelper.floor_double(this.mc.thePlayer.posZ);
            this.mc.thePlayer.sendChatMessage(String.format("%s[Coordinates] x:%d, y:%d, z:%d", new Object[] { this.useFactionChat ? "/ff " : "", Integer.valueOf(px), Integer.valueOf(py), Integer.valueOf(pz) }));
          } 
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\Coordinates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */