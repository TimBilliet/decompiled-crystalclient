package co.crystaldev.client.util.task;

import co.crystaldev.client.Client;
import co.crystaldev.client.util.enums.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotTask implements Runnable {
  private static final Logger logger = LogManager.getLogger("Async Screenshots");
  
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
  
  private static File currentScreenshot;
  
  private static BufferedImage currentImage;
  
  private final File gameDirectory;
  
  private final int width;
  
  private final int height;
  
  private final int[] pixelValues;
  
  private final Framebuffer buffer;
  
  public static File getCurrentScreenshot() {
    return currentScreenshot;
  }
  
  public static BufferedImage getCurrentImage() {
    return currentImage;
  }
  
  public ScreenshotTask(File gameDirectory, int width, int height, int[] pixelValues, Framebuffer buffer) {
    this.gameDirectory = gameDirectory;
    this.width = width;
    this.height = height;
    this.pixelValues = pixelValues;
    this.buffer = buffer;
  }
  
  public void run() {
    processPixelValues(this.pixelValues, this.width, this.height);
    currentScreenshot = getTimestampedPNGFileForDirectory(this.gameDirectory);
    try {
      if (OpenGlHelper.isFramebufferEnabled()) {
        currentImage = new BufferedImage(this.buffer.framebufferWidth, this.buffer.framebufferHeight, 1);
        for (int h = this.buffer.framebufferTextureHeight - this.buffer.framebufferHeight, heightSize = h; h < this.buffer.framebufferTextureHeight; h++) {
          for (int w = 0; w < this.buffer.framebufferWidth; w++)
            currentImage.setRGB(w, h - heightSize, this.pixelValues[h * this.buffer.framebufferTextureWidth + w]); 
        } 
      } else {
        currentImage = new BufferedImage(this.width, this.height, 1);
        currentImage.setRGB(0, 0, this.width, this.height, this.pixelValues, 0, this.width);
      } 
      ImageIO.write(currentImage, "png", currentScreenshot);
      printMessages();
    } catch (Exception ex) {
      logger.warn("Couldn't save screenshot", ex);
      Client.sendErrorMessage("Couldn't save screenshot: " + ex.getMessage(), true);
    } 
  }
  
  private void printMessages() throws IOException {
    ChatComponentText c1 = new ChatComponentText(ChatColor.translate('&', Client.getPrefix() + " Screenshot saved successfully as &n" + currentScreenshot.getName()));
    ChatComponentText c2 = new ChatComponentText(ChatColor.translate('&', "&a&l[OPEN] "));
    c2.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, currentScreenshot.getCanonicalPath()));
    c2.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent)new ChatComponentText("Open screenshot.")));
    ChatComponentText c3 = new ChatComponentText(ChatColor.translate('&', "&e&l[FOLDER] "));
    c3.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, this.gameDirectory.getCanonicalPath()));
    c3.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent)new ChatComponentText("Open screenshot folder.")));
    ChatComponentText c4 = new ChatComponentText(ChatColor.translate('&', "&9&l[COPY] "));
    c4.getChatStyle().setChatClickEvent(new ScreenshotClickEvent(ScreenshotClickEvent.Action.COPY));
    c4.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent)new ChatComponentText("Copy screenshot to clipboard.")));
    ChatComponentText c5 = new ChatComponentText(ChatColor.translate('&', "&c&l[DELETE]"));
    c5.getChatStyle().setChatClickEvent(new ScreenshotClickEvent(ScreenshotClickEvent.Action.DELETE));
    c5.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent)new ChatComponentText("Deletes the screenshot.")));
    IChatComponent all = (new ChatComponentText("")).appendSibling((IChatComponent)c2).appendSibling((IChatComponent)c3).appendSibling((IChatComponent)c4).appendSibling((IChatComponent)c5);
    (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage((IChatComponent)c1);
    (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(all);
  }
  
  private void processPixelValues(int[] pixels, int displayWidth, int displayHeight) {
    int[] xValues = new int[displayWidth];
    for (int yValues = displayHeight / 2, val = 0; val < yValues; val++) {
      System.arraycopy(pixels, val * displayWidth, xValues, 0, displayWidth);
      System.arraycopy(pixels, (displayHeight - 1 - val) * displayWidth, pixels, val * displayWidth, displayWidth);
      System.arraycopy(xValues, 0, pixels, (displayHeight - 1 - val) * displayWidth, displayWidth);
    } 
  }
  
  private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
    String s = dateFormat.format(new Date());
    int i = 1;
    while (true) {
      File file1 = new File(gameDirectory, s + ((i == 1) ? "" : ("_" + i)) + ".png");
      if (!file1.exists())
        return file1; 
      i++;
    } 
  }
  
  public static class ScreenshotClickEvent extends ClickEvent {
    private final Action screenshotAction;
    
    public Action getScreenshotAction() {
      return this.screenshotAction;
    }
    
    public ScreenshotClickEvent(Action action) {
      super(null, null);
      this.screenshotAction = action;
    }
    
    public enum Action {
      DELETE, COPY;
    }
  }
  
  public enum Action {
    DELETE, COPY;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\task\ScreenshotTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */