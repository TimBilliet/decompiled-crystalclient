package co.crystaldev.client.cosmetic.base;

import co.crystaldev.client.cosmetic.CosmeticEntry;
import co.crystaldev.client.cosmetic.CosmeticType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Cosmetic {
    private final boolean hiddenUnlessOwned;

    private final String name;

    private final String displayName;

    private final ResourceLocation location;

    private CosmeticType type;

    private final boolean animated;

    public String toString() {
        return "Cosmetic(hiddenUnlessOwned=" + isHiddenUnlessOwned() + ", name=" + getName() + ", displayName=" + getDisplayName() + ", location=" + getLocation() + ", type=" + getType() + ", animated=" + isAnimated() + ", texture=" + getTexture() + ")";
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Cosmetic))
            return false;
        Cosmetic other = (Cosmetic) o;
        if (!other.canEqual(this))
            return false;
        if (isHiddenUnlessOwned() != other.isHiddenUnlessOwned())
            return false;
        if (isAnimated() != other.isAnimated())
            return false;
        Object this$name = getName(), other$name = other.getName();
        if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
            return false;
        Object this$displayName = getDisplayName(), other$displayName = other.getDisplayName();
        if ((this$displayName == null) ? (other$displayName != null) : !this$displayName.equals(other$displayName))
            return false;
        Object this$location = getLocation(), other$location = other.getLocation();
        if ((this$location == null) ? (other$location != null) : !this$location.equals(other$location))
            return false;
        Object this$type = getType(), other$type = other.getType();
        if ((this$type == null) ? (other$type != null) : !this$type.equals(other$type))
            return false;
        Object this$texture = getTexture(), other$texture = other.getTexture();
        return !((this$texture == null) ? (other$texture != null) : !this$texture.equals(other$texture));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Cosmetic;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (isHiddenUnlessOwned() ? 79 : 97);
        result = result * 59 + (isAnimated() ? 79 : 97);
        Object $name = getName();
        result = result * 59 + (($name == null) ? 43 : $name.hashCode());
        Object $displayName = getDisplayName();
        result = result * 59 + (($displayName == null) ? 43 : $displayName.hashCode());
        Object $location = getLocation();
        result = result * 59 + (($location == null) ? 43 : $location.hashCode());
        Object $type = getType();
        result = result * 59 + (($type == null) ? 43 : $type.hashCode());
        Object $texture = getTexture();
        return result * 59 + (($texture == null) ? 43 : $texture.hashCode());
    }

    public boolean isHiddenUnlessOwned() {
        return this.hiddenUnlessOwned;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public CosmeticType getType() {
        return this.type;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    protected ITextureObject texture = null;

    public ITextureObject getTexture() {
        return this.texture;
    }

    public Cosmetic(@Nullable CosmeticEntry entry) {
        if (entry == null) {
            this.name = "< null >";
            this.displayName = null;
            this.type = CosmeticType.UNKNOWN;
            this.hiddenUnlessOwned = true;
            this.location = null;
            this.animated = false;
        } else {
            this.name = entry.getName();
            this.displayName = entry.getDisplayName();
            this.type = entry.getType();
            this.hiddenUnlessOwned = entry.isHiddenUnlessOwned();
            this.animated = entry.isAnimated();
            this.location = (entry.getResourceLocation() == null) ? new ResourceLocation("crystalclient", "cosmetics/" + this.type.name().toLowerCase() + "/" + this.name) : entry.getResourceLocation();
        }
    }

    public boolean isUnknown() {
        return (this.type == CosmeticType.UNKNOWN || this.location == null);
    }

    public String getDisplayName() {
        return (this.displayName != null) ? this.displayName : getName();
    }

    public <T> T withType(CosmeticType type) {
        this.type = type;
        return (T) this;
    }

    public void bindTexture() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(getLocation());
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\base\Cosmetic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */