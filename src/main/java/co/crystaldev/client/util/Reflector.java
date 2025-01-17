package co.crystaldev.client.util;

import co.crystaldev.client.Client;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.settings.GameSettings;
import co.crystaldev.client.util.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflector {
    private static final boolean OPTIFINE_LOADED = Client.isOptiFineLoaded();

    private static final boolean REPLAYMOD_LOADED = (getClass("com.replaymod.replay.ReplayModReplay") != null);

    public static Class<?> net_minecraft_src_Config = getClass("net.minecraft.src.Config");

    public static Class<?> net_optifine_shaders_ShadersTex = getClass("net.optifine.shaders.ShadersTex");

    public static Class<?> net_optifine_util_RenderChunkUtils = getClass("net.optifine.util.RenderChunkUtils");

    public static Class<?> com_replaymod_replay_ReplayModReplay = getClass("com.replaymod.replay.ReplayModReplay");

    public static Class<?> com_replaymod_replay_gui_screen_GuiReplayViewer = getClass("com.replaymod.replay.gui.screen.GuiReplayViewer");

    private static final Method RenderChunkUtils$getRelativeBufferSize = getMethod(net_optifine_util_RenderChunkUtils, new String[]{"getRelativeBufferSize"}, RenderChunk.class);

    public static double RenderChunkUtils$getRelativeBufferSize(RenderChunk instance) {
        if (RenderChunkUtils$getRelativeBufferSize == null)
            return 0.0D;
        try {
            return (Double) RenderChunkUtils$getRelativeBufferSize.invoke(instance, new Object[0]);
        } catch (Exception ex) {
            return 0.0D;
        }
    }

    private static final Method RenderChunk$isChunkRegionEmpty = getMethod(RenderChunk.class, new String[]{"isChunkRegionEmpty"});

    public static boolean RenderChunk$isChunkRegionEmpty(RenderChunk instance) {
        if (RenderChunk$isChunkRegionEmpty == null)
            return false;
        try {
            return (Boolean) RenderChunk$isChunkRegionEmpty.invoke(instance, new Object[0]);
        } catch (Exception ex) {
            return false;
        }
    }

    private static final Method Config$isShaders = getMethod(net_minecraft_src_Config, new String[]{"isShaders"});

    public static boolean Config$isShaders() {
        if (Config$isShaders == null)
            return false;
        try {

            return (Boolean) Config$isShaders.invoke(null, new Object[0]);
        } catch (Exception ex) {
            return false;
        }
    }

    private static final Method Config$getUpdatesPerFrame = getMethod(net_minecraft_src_Config, new String[]{"getUpdatesPerFrame"});

    public static int Config$getUpdatesPerFrame() {
        if (Config$getUpdatesPerFrame == null)
            return 1;
        try {
            return (Integer) Config$getUpdatesPerFrame.invoke(null, new Object[0]);
        } catch (Exception ex) {
            return 1;
        }
    }

    private static final Method Config$isCustomColors = getMethod(net_minecraft_src_Config, new String[]{"isCustomColors"});

    public static boolean Config$isCustomColors() {
        if (Config$isCustomColors == null)
            return false;
        try {
            return (Boolean) Config$isCustomColors.invoke(null, new Object[0]);
        } catch (Exception ex) {
            return false;
        }
    }

    private static final Method ShadersTex$bindTexture = getMethod(net_optifine_shaders_ShadersTex, new String[]{"bindTexture"}, ITextureObject.class);

    public static void ShadersTex$bindTexture(ITextureObject texture) {
        if (ShadersTex$bindTexture == null)
            return;
        try {
            ShadersTex$bindTexture.invoke(null, texture);
        } catch (Exception exception) {
        }
    }

    private static final Field GameSettings$ofFastRender = getField(GameSettings.class, "ofFastRender");

    public static boolean GameSettings$ofFastRender(GameSettings gs) {
        if (!OPTIFINE_LOADED || GameSettings$ofFastRender == null)
            return false;
        try {
            return GameSettings$ofFastRender.getBoolean(gs);
        } catch (Exception ex) {
            return false;
        }
    }

    public static void GameSettings$setOfFastRender(GameSettings gs, boolean newValue) {
        if (OPTIFINE_LOADED && GameSettings$ofFastRender != null)
            try {
                GameSettings$ofFastRender.setBoolean(gs, newValue);
            } catch (Exception exception) {
            }
    }

    public static void openReplayGui() {
        if (!REPLAYMOD_LOADED)
            return;
        try {
            Constructor<?> constructor = com_replaymod_replay_gui_screen_GuiReplayViewer.getConstructor(com_replaymod_replay_ReplayModReplay);
            Object instance = constructor.newInstance(com_replaymod_replay_ReplayModReplay.getField("instance").get(null));
            Method method = instance.getClass().getMethod("display");
            method.invoke(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isOptiFineLoaded() {
        return OPTIFINE_LOADED;
    }

    public static boolean isReplaymodLoaded() {
        return REPLAYMOD_LOADED;
    }

    public static Method getMethod(Class<?> clazzIn, String[] nameAliases, Class... methodTypes) {
        if (clazzIn == null)
            return null;
        try {
            return ReflectionHelper.findMethod((Class<? super Object>) clazzIn, nameAliases, methodTypes);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Field getField(Class<?> clazzIn, String... nameAliases) {
        try {
            return ReflectionHelper.findField(clazzIn, nameAliases);
        } catch (ReflectionHelper.UnableToFindFieldException ex) {
            return null;
        }
    }

    public static Class getClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\Reflector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */