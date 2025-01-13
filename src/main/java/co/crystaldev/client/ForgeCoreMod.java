package co.crystaldev.client;

import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
public class ForgeCoreMod implements IFMLLoadingPlugin {
    public ForgeCoreMod() {
        MixinBootstrap.init();
        MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();
        Mixins.addConfiguration("mixins.crystalclient.json");
        env.setSide(MixinEnvironment.Side.CLIENT);
        Logger logger1 = LogManager.getLogger("Timmeke_ Crystal Client CoreMod");
        logger1.warn("Adding mixin config");
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
            Logger logger = LogManager.getLogger("Timmeke_ Crystal Client CoreMod");
            logger.warn("No CodeSource, if this is not a development environment we might run into problems!");
            logger.warn(getClass().getProtectionDomain());
        }
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
