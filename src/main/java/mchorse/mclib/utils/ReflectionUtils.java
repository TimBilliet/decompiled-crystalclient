package mchorse.mclib.utils;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class ReflectionUtils {
    public static Field TEXTURE_MAP;

    public static Map<ResourceLocation, ITextureObject> getTextures(TextureManager manager) {
        if (TEXTURE_MAP == null)
            setupTextureMapField(manager);
        try {
            return (Map<ResourceLocation, ITextureObject>) TEXTURE_MAP.get(manager);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setupTextureMapField(TextureManager manager) {
        for (Field field : manager.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    Object value = field.get(manager);
                    if (value instanceof Map && ((Map) value).keySet().iterator().next() instanceof ResourceLocation) {
                        TEXTURE_MAP = field;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\ReflectionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */