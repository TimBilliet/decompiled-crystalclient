package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.cosmetic.type.Emoticon;
import co.crystaldev.client.cosmetic.type.cloak.AnimatedCloak;
import co.crystaldev.client.cosmetic.type.cloak.Cloak;
import co.crystaldev.client.cosmetic.type.wings.Wings;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;

public class CosmeticEntry {
    @SerializedName("name")
    private String name;

    public String toString() {
        return "CosmeticEntry(name=" + getName() + ", displayName=" + getDisplayName() + ", type=" + getType() + ", animated=" + isAnimated() + ", hiddenUnlessOwned=" + isHiddenUnlessOwned() + ", sha1=" + getSha1() + ", size=" + getSize() + ", path=" + getPath() + ", originalWidth=" + getOriginalWidth() + ", originalHeight=" + getOriginalHeight() + ", location=" + getLocation() + ", resourceLocation=" + getResourceLocation() + ", stream=" + getStream() + ")";
    }

    public String getName() {
        return this.name;
    }

    @SerializedName("display_name")
    private String displayName = null;

    @SerializedName("type")
    private CosmeticType type;

    public String getDisplayName() {
        return this.displayName;
    }

    public CosmeticType getType() {
        return this.type;
    }

    @SerializedName("animated")
    private boolean animated = false;

    public boolean isAnimated() {
        return this.animated;
    }

    @SerializedName("hidden_unless_owned")
    private boolean hiddenUnlessOwned = false;

    @SerializedName("sha1")
    private String sha1;

    @SerializedName("size")
    private int size;

    @SerializedName("path")
    private String path;

    public boolean isHiddenUnlessOwned() {
        return this.hiddenUnlessOwned;
    }

    public String getSha1() {
        return this.sha1;
    }

    public int getSize() {
        return this.size;
    }

    public String getPath() {
        return this.path;
    }

    @SerializedName("width")
    private int originalWidth = -1;

    public int getOriginalWidth() {
        return this.originalWidth;
    }

    @SerializedName("height")
    private int originalHeight = -1;

    private transient File location;

    private transient ResourceLocation resourceLocation;

    private transient InputStream stream;

    public int getOriginalHeight() {
        return this.originalHeight;
    }

    public File getLocation() {
        return this.location;
    }

    public void setLocation(File location) {
        this.location = location;
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public InputStream getStream() {
        return this.stream;
    }

    public void close() {
        if (this.stream != null)
            try {
                this.stream.close();
                this.stream = null;
            } catch (IOException ex) {
                Reference.LOGGER.error("Unable to close input stream", ex);
            }
    }

    public InputStream getInputStream(File file) {
        close();
        try {
            return this.stream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    public boolean validate(File file) {
        if (file == null || getInputStream(file) == null)
            return false;
        try {
            int size = this.stream.available();
            String sha1 = DigestUtils.sha1Hex(this.stream);
            close();
            return (this.sha1.equals(sha1) && size == this.size);
        } catch (IOException ex) {
            return false;
        }
    }

    public CosmeticEntry withName(String name) {
        this.name = name;
        return this;
    }

    public CosmeticEntry withType(CosmeticType type) {
        this.type = type;
        return this;
    }

    public CosmeticEntry withDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public CosmeticEntry setHiddenIfUnowned(boolean hidden) {
        this.hiddenUnlessOwned = hidden;
        return this;
    }

    public CosmeticEntry setAnimated(boolean animated) {
        this.animated = animated;
        return this;
    }

    public CosmeticEntry withResourceLocation(ResourceLocation location) {
        this.resourceLocation = location;
        return this;
    }

    public CosmeticEntry withFileLocation(File location) {
        this.location = location;
        return this;
    }

    public Cosmetic build() {
        if (this.animated) {
            if (this.type == CosmeticType.CLOAK)
                return new AnimatedCloak(this);
        } else {
            switch (this.type) {
                case CLOAK:
                    return new Cloak(this);
                case WINGS:
                    return new Wings(this);
                case EMOTE:
                    return new Emoticon(this);
                case COLOR:
                    return new Color(this);
            }
        }
        return null;
    }
}