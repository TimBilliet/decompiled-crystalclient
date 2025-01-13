package mchorse.mclib.utils.resources;

import com.google.common.base.Objects;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MultiResourceLocation extends ResourceLocation implements IWritableLocation {
    public List<FilteredResourceLocation> children = new ArrayList<>();

    public static MultiResourceLocation from(NBTBase nbt) {
        NBTTagList list = (nbt instanceof NBTTagList) ? (NBTTagList) nbt : null;
        if (list == null || list.hasNoTags())
            return null;
        MultiResourceLocation multi = new MultiResourceLocation();
        try {
            multi.fromNbt(nbt);
            return multi;
        } catch (Exception exception) {
            return null;
        }
    }

    public static MultiResourceLocation from(JsonElement element) {
        JsonArray list = element.isJsonArray() ? (JsonArray) element : null;
        if (list == null || list.size() == 0)
            return null;
        MultiResourceLocation multi = new MultiResourceLocation();
        try {
            multi.fromJson(element);
            return multi;
        } catch (Exception exception) {
            return null;
        }
    }

    public MultiResourceLocation(String resourceName) {
        this();
        this.children.add(new FilteredResourceLocation(RLUtils.create(resourceName)));
    }

    public MultiResourceLocation(String resourceDomainIn, String resourcePathIn) {
        this();
        this.children.add(new FilteredResourceLocation(RLUtils.create(resourceDomainIn, resourcePathIn)));
    }

    public MultiResourceLocation() {
        super("it_would_be_very_ironic", "if_this_would_match_with_regular_rls");
    }

    public String getResourceDomain() {
        return this.children.isEmpty() ? "" : ((FilteredResourceLocation) this.children.get(0)).path.getResourceDomain();
    }

    public String getResourcePath() {
        return this.children.isEmpty() ? "" : ((FilteredResourceLocation) this.children.get(0)).path.getResourcePath();
    }

    public String toString() {
        return getResourceDomain() + ":" + getResourcePath();
    }

    public boolean equals(Object obj) {
        if (obj instanceof MultiResourceLocation) {
            MultiResourceLocation multi = (MultiResourceLocation) obj;
            return Objects.equal(this.children, multi.children);
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int hash = super.hashCode();
        for (int i = 0, c = this.children.size(); i < c; i++)
            hash = 31 * hash + ((FilteredResourceLocation) this.children.get(i)).hashCode();
        return hash;
    }

    public void fromNbt(NBTBase nbt) throws Exception {
        NBTTagList list = (NBTTagList) nbt;
        for (int i = 0; i < list.tagCount(); i++) {
            FilteredResourceLocation location = FilteredResourceLocation.from(list.get(i));
            if (location != null)
                this.children.add(location);
        }
    }

    public void fromJson(JsonElement element) throws Exception {
        JsonArray array = (JsonArray) element;
        for (int i = 0; i < array.size(); i++) {
            FilteredResourceLocation location = FilteredResourceLocation.from(array.get(i));
            if (location != null)
                this.children.add(location);
        }
    }

    public NBTBase writeNbt() {
        NBTTagList list = new NBTTagList();
        for (FilteredResourceLocation child : this.children) {
            NBTBase tag = child.writeNbt();
            if (tag != null)
                list.appendTag(tag);
        }
        return (NBTBase) list;
    }

    public JsonElement writeJson() {
        JsonArray array = new JsonArray();
        for (FilteredResourceLocation child : this.children) {
            JsonElement element = child.writeJson();
            if (element != null)
                array.add(element);
        }
        return (JsonElement) array;
    }

    public ResourceLocation clone() {
        MultiResourceLocation newMulti = new MultiResourceLocation();
        for (FilteredResourceLocation child : this.children)
            newMulti.children.add(child.copy());
        return newMulti;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\MultiResourceLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */