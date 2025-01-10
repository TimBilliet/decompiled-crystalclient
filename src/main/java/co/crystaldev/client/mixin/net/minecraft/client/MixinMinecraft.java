package co.crystaldev.client.mixin.net.minecraft.client;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.SplashScreen;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.event.Event;
import co.crystaldev.client.event.impl.entity.EntityAttackEvent;
import co.crystaldev.client.event.impl.init.ShutdownEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.event.impl.render.RenderTickEvent;
import co.crystaldev.client.event.impl.render.WindowResizeEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.ext.GuiIngameCrystal;
//import co.crystaldev.client.feature.impl.all.ChatSettings;
import co.crystaldev.client.feature.impl.all.ChatSettings;
import co.crystaldev.client.feature.impl.combat.OldAnimations;
import co.crystaldev.client.feature.impl.combat.OldHits;
import co.crystaldev.client.feature.impl.mechanic.PerspectiveMod;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.override.ScreenMainMenu;
import co.crystaldev.client.handler.OverlayHandler;
import co.crystaldev.client.patcher.hook.MinecraftHook;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({Minecraft.class})
public abstract class MixinMinecraft {
  @Shadow
  @Final
  private List<IResourcePack> defaultResourcePacks;
  
  @Shadow
  private boolean enableGLErrorChecking;
  
  @Shadow
  public EntityRenderer entityRenderer;
  
  @Shadow
  private IReloadableResourceManager mcResourceManager;
  
  @Shadow
  @Final
  public static boolean isRunningOnMac;
  
  @Shadow
  private boolean fullscreen;
  
  @Shadow
  public EffectRenderer effectRenderer;
  
  @Shadow
  public PlayerControllerMP playerController;
  
  @Shadow
  public MovingObjectPosition objectMouseOver;
  
  @Shadow
  private int leftClickCounter;
  
  @Shadow
  public EntityPlayerSP thePlayer;
  
  @Shadow
  public WorldClient theWorld;
  
  @Shadow
  public GuiScreen currentScreen;
  
  @Shadow
  public Entity pointedEntity;
  
  @Shadow
  public GameSettings gameSettings;
  
  @Shadow
  public GuiIngame ingameGUI;
  
  @Shadow
  private SoundHandler mcSoundHandler;
  
  @Shadow
  private Timer timer;
  
  @Shadow
  public boolean skipRenderWorld;
  
  @Inject(method = {"startGame"}, at = {@At("TAIL")})
  private void startGame(CallbackInfo ci) {
    new Client();
  }
  
  @Inject(method = {"startGame"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER)})
  private void addCosmeticResourcePack(CallbackInfo ci) {
    this.defaultResourcePacks.add(CosmeticManager.getResourcePack());
  }
  
  @Inject(method = {"startGame"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER)})
  private void replaceGuiIngame(CallbackInfo ci) {
    this.ingameGUI = (GuiIngame)new GuiIngameCrystal((Minecraft)(Object)this);
  }

  @Inject(method = {"startGame"}, at = {@At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", shift = At.Shift.BEFORE)})
  private void logClientVersion(CallbackInfo ci) {
    Reference.LOGGER.info("");
    Reference.LOGGER.info("  {} v{}-{}/{}", "Crystal Client", "1.1.16-projectassfucker", "37aa61d", "offline");
    Reference.LOGGER.info("  https://discord.gg/mmVWkk93E9");
    Reference.LOGGER.info("");
  }

  @Redirect(method = {"createDisplay"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", ordinal = 0))
  private void createDisplay(String newTitle) {
    Display.setTitle("Loading...");
  }

  @Inject(method = {"loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;renderViewEntity:Lnet/minecraft/entity/Entity;", ordinal = 0, shift = At.Shift.AFTER)})
  private void loadWorld0(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
    this.pointedEntity = null;
  }

  @Redirect(method = {"launchIntegratedServer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
  private void launchIntegratedServer(Minecraft minecraft, GuiScreen guiScreenIn) {
    minecraft.displayGuiScreen((GuiScreen)new GuiScreenWorking());
  }

  @Redirect(method = {"run", "freeMemory", "shutdownMinecraftApplet", "launchIntegratedServer", "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V", remap = false))
  private void ignoreSystemGcCalls() {}

  @Inject(method = {"runTick"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 3, shift = At.Shift.AFTER)})
  private void runTick(CallbackInfo ci) {
    if ((PerspectiveMod.getInstance()).perspectiveToggled) {
      PerspectiveMod.getInstance().resetPerspective();
    } else {
      this.gameSettings.thirdPersonView = 0;
    }
  }

  @Inject(method = {"toggleFullscreen"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false)})
  private void resolveScreenState(CallbackInfo ci) {
    if (!this.fullscreen && Util.getOSType() == Util.EnumOS.WINDOWS) {
      Display.setResizable(false);
      Display.setResizable(true);
    }
  }

  @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V"))
  private void keepShadersOnPerspectiveChange(EntityRenderer entityRenderer, Entity entityIn) {
    if (!(ClientOptions.getInstance()).keepShadersOnPerspectiveChange)
      entityRenderer.loadEntityShader(entityIn);
  }

  @Redirect(method = {"dispatchKeypresses"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventCharacter()C", remap = false))
  private char resolveForeignKeyboards() {
    return (char)(Keyboard.getEventCharacter() + 256);
  }

  @Inject(method = {"setIngameFocus"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V")})
  private void makeKeysReRegister(CallbackInfo ci) {
    if ((ClientOptions.getInstance()).modernKeybindHandling && !isRunningOnMac)
      MinecraftHook.updateKeyBindState();
  }

  @Inject(method = {"toggleFullscreen"}, at = {@At("HEAD")}, cancellable = true)
  private void windowedFullscreen(CallbackInfo ci) {
    if (MinecraftHook.fullscreen())
      ci.cancel();
  }

  @Inject(method = {"getLimitFramerate"}, at = {@At("HEAD")}, cancellable = true)
  private void modifyFpsLimit(CallbackInfoReturnable<Integer> cir) {
    if (this.theWorld == null && this.currentScreen != null)
      return;
    if (!Display.isActive() && (ClientOptions.getInstance()).unfocusedFps)
      cir.setReturnValue((ClientOptions.getInstance()).unfocusedFpsAmount);
  }

  @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;refreshResources()V", ordinal = 0))
  private void separateSoundReloading(Minecraft minecraft) {
    this.mcSoundHandler.onResourceManagerReload((IResourceManager)this.mcResourceManager);
  }

  @Inject(method = {"loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at = {@At("HEAD")})
  private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
    if (worldClientIn != this.theWorld)
      this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
  }
  
  @Inject(method = {"startGame"}, at = {@At("TAIL")})
  private void disableGlErrorChecking(CallbackInfo ci) {
    this.enableGLErrorChecking = false;
  }

  @Redirect(method = {"runGameLoop"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
  private void skipTwitch1(IStream instance) {}

  @Redirect(method = {"runGameLoop"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
  private void skipTwitch2(IStream instance) {}

  @Redirect(method = {"loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
  private void skipExplicitGc() {}

  /**
   * @author
   */
  @Overwrite(aliases = {"getLimitFramerate"})
  public int getLimitFramerate() {
    return this.gameSettings.limitFramerate;
  }

  @Inject(method = {"drawSplashScreen"}, cancellable = true, at = {@At("HEAD")})
  private void drawSplashScreen(TextureManager manager, CallbackInfo ci) {
    SplashScreen.renderSplash(manager);
    ci.cancel();
  }

  @Inject(method = {"clickMouse"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;leftClickCounter:I", opcode = 181, shift = At.Shift.AFTER)})
  public void setLeftClickCounter(CallbackInfo ci) {
    if ((OldHits.getInstance()).enabled)
      this.leftClickCounter = 0;
  }

  @Inject(method = {"runTick"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 8, shift = At.Shift.BEFORE)})
  public void injectHandleInput(CallbackInfo ci) {
    if (OverlayHandler.getInstance().hasOverlay())
      OverlayHandler.getInstance().getCurrentOverlay().handleInput();
  }

  @Redirect(method = {"runTick"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 8))
  public GuiScreen handleInput(Minecraft instance) {
    if (OverlayHandler.getInstance().hasOverlay())
      return null;
    return instance.currentScreen;
  }

  @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setIngameFocus()V"))
  public void cancelSetIngameFocus(Minecraft mc) {
    if (!OverlayHandler.getInstance().hasOverlay())
      mc.setIngameFocus();
  }

  @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButtonState()Z", shift = At.Shift.BEFORE, ordinal = 2)})
  public void injectMouseInput(CallbackInfo ci) {
    if (OverlayHandler.getInstance().hasOverlay())
      OverlayHandler.getInstance().getCurrentOverlay().handleMouseInput();
  }

  @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V", shift = At.Shift.BY, by = 2)})
  public void injectKeyboardInput(CallbackInfo ci) {
    if (OverlayHandler.getInstance().hasOverlay())
      OverlayHandler.getInstance().getCurrentOverlay().handleKeyboardInput();
  }

  @ModifyArg(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;sendClickBlockToController(Z)V"))
  public boolean cancelSendClickBlockToController(boolean flag) {
    return (!OverlayHandler.getInstance().hasOverlay() && flag);
  }
  
  /**
   * @author
   */
  @Overwrite
  private void sendClickBlockToController(boolean leftClick) {
    if (!leftClick)
      this.leftClickCounter = 0;
    if (this.leftClickCounter <= 0)
      if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
        BlockPos blockpos = this.objectMouseOver.getBlockPos();
        if ((OldAnimations.getInstance()).enabled && this.playerController.gameIsSurvivalOrAdventure() &&
          (OldAnimations.getInstance()).punchDuringUsage &&
                          this.gameSettings.keyBindAttack.isKeyDown() && (this.gameSettings.keyBindUseItem.isKeyDown() || this.thePlayer.isUsingItem()) && this.thePlayer.getHeldItem() != null && (this.thePlayer.getHeldItem()).stackSize > 0) {
//          this.gameSettings.keyBindAttack.getIsKeyPressed() && (this.gameSettings.keyBindUseItem.getIsKeyPressed() || this.thePlayer.isUsingItem()) && this.thePlayer.getHeldItem() != null && (this.thePlayer.getHeldItem()).stackSize > 0) {
          ItemStack stack = this.thePlayer.getHeldItem();
          int id;
          if (stack == null || (id = Item.getIdFromItem(stack.getItem())) == 332 || id == 381 || id == 368)
            return;
          if (this.playerController.sendUseItem((EntityPlayer)this.thePlayer, (World)this.theWorld, this.thePlayer.getHeldItem())) {
            this.playerController.resetBlockRemoving();
            return;
          }
        }
        if (this.thePlayer.isUsingItem())
          return;
        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
          this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
          this.thePlayer.swingItem();
        }
      } else {
        this.playerController.resetBlockRemoving();
      }
  }

  @Inject(method = {"clickMouse"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V", shift = At.Shift.BEFORE)})
  public void onEntityAttackPre(CallbackInfo ci) {
    Entity entityHit = this.objectMouseOver.entityHit;
    Event event = (new EntityAttackEvent.Pre((Entity)this.thePlayer, entityHit, this.objectMouseOver.hitVec.distanceTo(new Vec3(entityHit.posX, entityHit.posY, entityHit.posZ)))).call();
    if (event.isCancelled())
      ci.cancel(); 
  }
  
  @Inject(method = {"clickMouse"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER)})
  public void onEntityAttackPost(CallbackInfo ci) {
    Entity entityHit = this.objectMouseOver.entityHit;
    (new EntityAttackEvent.Post((Entity)this.thePlayer, entityHit, this.objectMouseOver.hitVec.distanceTo(new Vec3(entityHit.posX, entityHit.posY, entityHit.posZ)))).call();
  }
  
  @Inject(method = {"shutdownMinecraftApplet"}, at = {@At("HEAD")})
  private void shutdownMinecraftApplet(CallbackInfo ci) {
    (new ShutdownEvent()).call();
  }
  
  @Inject(method = {"dispatchKeypresses"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", shift = At.Shift.BEFORE)})
  private void dispatchKeypresses(CallbackInfo ci) {
    InputEvent.Key event = new InputEvent.Key(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.isKeyDown(Keyboard.getEventKey()));
    event.call();
    if (event.isCancelled())
      ci.cancel(); 
  }
  
  @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z", ordinal = 0))
  private boolean Mouse$next() {
    boolean flag = Mouse.next();
    if (flag) {
      InputEvent.Mouse event = new InputEvent.Mouse();
      event.call();
      return !event.isCancelled();
    } 
    return false;
  }
  
  @Inject(method = {"displayGuiScreen"}, cancellable = true, at = {@At("HEAD")})
  public void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
    loadGui(guiScreenIn, ci);
  }
  private void loadGui(GuiScreen guiScreenIn, CallbackInfo ci) {

//    ScreenMainMenu screenMainMenu = null;
//    GuiGameOver guiGameOver = null;
    ci.cancel();
    if (guiScreenIn instanceof net.minecraft.client.gui.GuiMainMenu)
      guiScreenIn = new ScreenMainMenu();
    if (guiScreenIn == null && this.theWorld == null) {
      guiScreenIn = new ScreenMainMenu();
    } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
      guiScreenIn = new GuiGameOver();
    }
    GuiOpenEvent ev = new GuiOpenEvent(guiScreenIn);
    GuiScreen old = this.currentScreen;
    if (MinecraftForge.EVENT_BUS.post(ev))
      return;
    GuiScreen guiScreen1 = ev.gui;
    GuiScreenEvent.Pre event = new GuiScreenEvent.Pre(guiScreen1, old);
    event.call();
    if (event.isCancelled())
      return;
    guiScreen1 = event.gui;
    if (old != null)
      old.onGuiClosed();
    if (guiScreen1 instanceof Screen) {
      ((Screen) guiScreen1).exited = false;
      if (old != null)
        ((Screen) guiScreen1)._keyPressed = true;
      if (!((Screen) guiScreen1).constructed) {
        ((Screen) guiScreen1).init();
        ((Screen) guiScreen1).constructed = true;
      }
    }
    if (guiScreen1 instanceof ScreenMainMenu) {
      this.gameSettings.showDebugInfo = false;
      if (ChatSettings.getInstance() != null && (!(ChatSettings.getInstance()).enabled || !(ChatSettings.getInstance()).crossServer))
        this.ingameGUI.getChatGUI().clearChatMessages();
    }
    this.currentScreen = guiScreen1;
    if (guiScreen1 != null) {
      setIngameNotInFocus();
      ScaledResolution scaledresolution = new ScaledResolution((Minecraft) (Object) this);
      int i = scaledresolution.getScaledWidth();
      int j = scaledresolution.getScaledHeight();
      guiScreen1.setWorldAndResolution((Minecraft) (Object) this, i, j);
      this.skipRenderWorld = false;
    } else {
      this.mcSoundHandler.resumeSounds();
      setIngameFocus();
    }
    (new GuiScreenEvent.Post(this.currentScreen)).call();
  }
  @Inject(method = {"resize"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;displayHeight:I", shift = At.Shift.AFTER)})
  private void resize(int width, int height, CallbackInfo ci) {
    ScaledResolution sr = new ScaledResolution((Minecraft)(Object)this);
    (new WindowResizeEvent(sr.getScaledWidth(), sr.getScaledHeight(), sr)).call();
  }
  
  @Inject(method = {"loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at = {@At("HEAD")})
  private void loadWorld(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
    if (this.theWorld != null) {
      if (this.theWorld instanceof com.github.lunatrius.schematica.client.world.SchematicWorld) {
        (new WorldEvent.SchematicUnload(this.theWorld)).call();
      } else {
      (new WorldEvent.Unload(this.theWorld)).call();
       }
    }
  }
  
  @Inject(method = {"runGameLoop"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.BEFORE)})
  public void onRenderTickPre(CallbackInfo ci) {
    (new RenderTickEvent.Pre(this.timer.renderPartialTicks)).call();
  }
  
  @Inject(method = {"runGameLoop"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.AFTER)})
  public void onRenderTickPost(CallbackInfo ci) {
    (new RenderTickEvent.Post(this.timer.renderPartialTicks)).call();
  }
  
  @Inject(method = {"runTick"}, at = {@At("HEAD")})
  public void onClientTickPre(CallbackInfo ci) {
    (new ClientTickEvent.Pre()).call();
  }
  
  @Inject(method = {"runTick"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;systemTime:J", ordinal = 1, shift = At.Shift.BEFORE)})
  public void onClientTickPost(CallbackInfo ci) {
    (new ClientTickEvent.Post()).call();
  }
  
  @Shadow
  public abstract void setIngameFocus();
  
  @Shadow
  public abstract void setIngameNotInFocus();
}