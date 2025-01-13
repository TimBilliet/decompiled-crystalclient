package co.crystaldev.client.patcher.enhancement.text;

import co.crystaldev.client.patcher.enhancement.Enhancement;
import co.crystaldev.client.patcher.enhancement.hash.StringHash;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minecraft.client.renderer.GLAllocation;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class EnhancedFontRenderer implements Enhancement {
    private static final List<EnhancedFontRenderer> instances = new ArrayList<>();

    private final List<StringHash> obfuscated = new ArrayList<>();

    private final Map<String, Integer> stringWidthCache = new HashMap<>();

    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();

    private final Cache<StringHash, CachedString> stringCache;

    public EnhancedFontRenderer() {
        this

                .stringCache = Caffeine.newBuilder().removalListener((key, value, cause) -> {
            if (value == null)
                return;
            this.glRemoval.add(Integer.valueOf(((CachedString) value).getListId()));
        }).executor(POOL).maximumSize(5000L).build();
        instances.add(this);
    }

    public static List<EnhancedFontRenderer> getInstances() {
        return instances;
    }

    public String getName() {
        return "Enhanced Font Renderer";
    }

    public void tick() {
        this.stringCache.invalidateAll(this.obfuscated);
        this.obfuscated.clear();
    }

    public int getGlList() {
        Integer poll = this.glRemoval.poll();
        return (poll == null) ? GLAllocation.generateDisplayLists(1) : poll.intValue();
    }

    public CachedString get(StringHash key) {
        return (CachedString) this.stringCache.getIfPresent(key);
    }

    public void cache(StringHash key, CachedString value) {
        this.stringCache.put(key, value);
    }

    public Map<String, Integer> getStringWidthCache() {
        return this.stringWidthCache;
    }

    public void invalidateAll() {
        this.stringCache.invalidateAll();
    }

    public List<StringHash> getObfuscated() {
        return this.obfuscated;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\text\EnhancedFontRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */