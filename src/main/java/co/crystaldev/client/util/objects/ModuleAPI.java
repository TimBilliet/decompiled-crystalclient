package co.crystaldev.client.util.objects;

import com.google.gson.annotations.SerializedName;

public class ModuleAPI {
    @SerializedName("PATCHCRUMBS")
    public boolean PATCHCRUMBS = true;

    @SerializedName("BREADCRUMBS")
    public boolean BREADCRUMBS = true;

    @SerializedName("EXPLOSION_BOXES")
    public boolean EXPLOSION_BOXES = true;

    @SerializedName("ADJUST_HELPER")
    public boolean ADJUST_HELPER = true;

    @SerializedName("CANNON_VIEW")
    public boolean CANNON_VIEW = false;

    @SerializedName("OLD_HITS")
    public boolean OLD_HITS = true;
}