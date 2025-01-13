package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJAction;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Animation {
    public String name;

    public BOBJLoader.BOBJData data;

    public List<AnimationMesh> meshes;

    public Minecraft mc;

    public Animation(String name, BOBJLoader.BOBJData data) {
        this.name = name;
        this.data = data;
        this.mc = Minecraft.getMinecraft();
        this.meshes = new ArrayList<>();
    }

    public void reload(BOBJLoader.BOBJData data) {
        this.data = data;
        delete();
        init();
    }

    public ActionPlayback createAction(ActionPlayback old, ActionConfig config, boolean looping) {
        return createAction(old, config, looping, 1);
    }

    public ActionPlayback createAction(ActionPlayback old, ActionConfig config, boolean looping, int priority) {
        BOBJAction action = (BOBJAction) this.data.actions.get(config.name);
        if (action == null)
            return null;
        if (old != null && old.action == action) {
            old.config = config;
            old.setSpeed(1.0F);
            return old;
        }
        ActionPlayback playback = new ActionPlayback(action, config, looping, priority);
        if (action.name.contains("ragdoll"))
            playback.customArmature = (BOBJArmature) this.data.armatures.get("ArmatureRagdoll");
        return playback;
    }

    public void init() {
        Map<String, BOBJLoader.CompiledData> compiled = BOBJLoader.loadMeshes(this.data);
        for (Map.Entry<String, BOBJLoader.CompiledData> entry : compiled.entrySet()) {
            String name = entry.getKey();
            BOBJLoader.CompiledData data = entry.getValue();
            AnimationMesh mesh = new AnimationMesh(this, entry.getKey(), data);
            mesh.texture = RLUtils.create("s&b", this.name + "/textures/" + name + "/default.png");
            this.meshes.add(mesh);
        }
        this.data.dispose();
    }

    public void delete() {
        for (AnimationMesh mesh : this.meshes)
            mesh.delete();
        this.meshes.clear();
    }

    public void render(Map<String, AnimationMeshConfig> configs) {
        for (AnimationMesh mesh : this.meshes)
            mesh.render(this.mc, (configs == null) ? null : configs.get(mesh.name));
        GL15.glBindBuffer(34962, 0);
        GL15.glBindBuffer(34963, 0);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\Animation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */