package co.crystaldev.client.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.List;

public class CrystalClientTweaker implements ITweaker {
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.init();
        MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();
        Mixins.addConfiguration("mixins.crystalclient.json");
        if (env.getObfuscationContext() == null)
            env.setObfuscationContext("name");
        env.setSide(MixinEnvironment.Side.CLIENT);
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            try {
                File file = new File(location.toURI());
                if (file.isFile())
                    CoreModManager.getReparseableCoremods().remove(file.getName());
            } catch (URISyntaxException uRISyntaxException) {
            }
        } else {
            Logger logger = LogManager.getLogger("Offline Crystal Client CoreMod");
            logger.warn("No CodeSource, if this is not a development environment we might run into problems!");
            logger.warn(getClass().getProtectionDomain());
        }
    }

    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    public String[] getLaunchArguments() {
        return new String[0];
    }
}