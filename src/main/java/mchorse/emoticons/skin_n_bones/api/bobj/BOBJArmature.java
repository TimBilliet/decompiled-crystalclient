package mchorse.emoticons.skin_n_bones.api.bobj;

import co.crystaldev.client.util.javax.Matrix4f;

import java.util.*;

public class BOBJArmature {
    public String name;

    public String action = "";

    public Map<String, BOBJBone> bones = new HashMap<>();

    public List<BOBJBone> orderedBones = new ArrayList<>();

    public List<BOBJBone> ikBones = new ArrayList<>();

    public Matrix4f[] matrices;

    private boolean initialized;

    public BOBJArmature(String name) {
        this.name = name;
    }

    public void addBone(BOBJBone bone) {
        this.bones.put(bone.name, bone);
        this.orderedBones.add(bone);
    }

    public void initArmature() {
        if (!this.initialized) {
            List<BOBJBone> ikBones = new ArrayList<>();
            for (BOBJBone bone : this.bones.values()) {
                if (bone.hasModifiers())
                    ikBones.add(bone);
                if (!bone.parent.isEmpty()) {
                    bone.parentBone = this.bones.get(bone.parent);
                    bone.relBoneMat.set(bone.parentBone.boneMat);
                    bone.relBoneMat.invert();
                    bone.relBoneMat.mul(bone.boneMat);
                    continue;
                }
                bone.relBoneMat.set(bone.boneMat);
            }
            if (!ikBones.isEmpty())
                this.ikBones = ikBones;
            Collections.sort(this.orderedBones, (o1, o2) -> o1.index - o2.index);
            this.matrices = new Matrix4f[this.orderedBones.size()];
            this.initialized = true;
        }
    }

    public void setupMatrices() {
        for (BOBJBone bone : this.orderedBones)
            this.matrices[bone.index] = bone.compute();
    }

    public void copyOrder(BOBJArmature armature) {
        for (BOBJBone bone : armature.orderedBones) {
            BOBJBone thisBone = this.bones.get(bone.name);
            if (thisBone != null)
                thisBone.index = bone.index;
        }
        Collections.sort(this.orderedBones, (o1, o2) -> o1.index - o2.index);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJArmature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */