package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RLUtils {
    private static final List<IResourceTransformer> transformers = new ArrayList<>();

    private static final ResourceLocation pixel = new ResourceLocation("emoticons", "textures/pixel.png");

    public static IResource getStreamForMultiskin(MultiResourceLocation multi) throws IOException {
        if (multi.children.isEmpty())
            throw new IOException("Multi-skin is empty!");
        try {
            MultiskinThread.add(multi);
            return Minecraft.getMinecraft().getResourceManager().getResource(pixel);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static void register(IResourceTransformer transformer) {
        transformers.add(transformer);
    }

    public static ResourceLocation create(String path) {
        for (IResourceTransformer transformer : transformers)
            path = transformer.transform(path);
        return new ResourceLocation(path);
    }

    public static ResourceLocation create(String domain, String path) {
        for (IResourceTransformer transformer : transformers) {
            String newDomain = transformer.transformDomain(domain, path);
            String newPath = transformer.transformPath(domain, path);
            domain = newDomain;
            path = newPath;
        }
        return new ResourceLocation(domain, path);
    }

    public static ResourceLocation create(NBTBase base) {
        ResourceLocation location = MultiResourceLocation.from(base);
        if (location != null)
            return location;
        if (base instanceof NBTTagString)
            return create(((NBTTagString) base).getString());
        return null;
    }

    public static ResourceLocation create(JsonElement element) {
        ResourceLocation location = MultiResourceLocation.from(element);
        if (location != null)
            return location;
        if (element.isJsonPrimitive())
            return create(element.getAsString());
        return null;
    }

    public static NBTBase writeNbt(ResourceLocation location) {
        if (location instanceof IWritableLocation)
            return ((IWritableLocation) location).writeNbt();
        if (location != null)
            return (NBTBase) new NBTTagString(location.toString());
        return null;
    }

    public static JsonElement writeJson(ResourceLocation location) {
        if (location instanceof IWritableLocation)
            return ((IWritableLocation) location).writeJson();
        if (location != null)
            return (JsonElement) new JsonPrimitive(location.toString());
        return (JsonElement) JsonNull.INSTANCE;
    }

    public static ResourceLocation clone(ResourceLocation location) {
        if (location instanceof IWritableLocation)
            return ((IWritableLocation) location).clone();
        if (location != null)
            return create(location.toString());
        return null;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\RLUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */