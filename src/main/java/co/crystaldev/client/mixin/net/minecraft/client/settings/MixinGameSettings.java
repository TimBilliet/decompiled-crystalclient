package co.crystaldev.client.mixin.net.minecraft.client.settings;

import co.crystaldev.client.duck.GameSettingsExt;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Mixin({GameSettings.class})
public abstract class MixinGameSettings implements GameSettingsExt {
  @Shadow
  protected Minecraft mc;

  @Unique
  private boolean needsResourceRefresh;

  @Redirect(method = {"<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V"}, at = @At(value = "NEW", target = "java.io.File", ordinal = 0))
  private File construct(File file, String child) {
    return new File(file, "options_crystal.txt");
  }

  /**
   * @author Tim
   */
  @Overwrite(aliases = {"isKeyDown"})
  public static boolean isKeyDown(KeyBinding key) {
    int keyCode = key.getKeyCode();
    if (keyCode != 0 && keyCode < 256)
      return (keyCode < 0) ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
    return false;
  }

  public void onSettingsGuiClosed() {
    if (this.needsResourceRefresh) {
      this.mc.scheduleResourcesRefresh();
      this.needsResourceRefresh = false;
    }
  }

  @Redirect(method = {"setOptionFloatValue"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;scheduleResourcesRefresh()Lcom/google/common/util/concurrent/ListenableFuture;"))
  private ListenableFuture<Object> scheduleResourceRefresh(Minecraft instance) {
    this.needsResourceRefresh = true;
    return null;
  }
}
