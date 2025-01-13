package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Reference;
import co.crystaldev.client.SplashScreen;
import co.crystaldev.client.util.FileUtils;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CosmeticDownloader {
    private static final CosmeticDownloader INSTANCE = new CosmeticDownloader();

    private final Queue<CosmeticEntry> queued = new ArrayDeque<>();

    private final List<CosmeticEntry> downloaded = new ArrayList<>();

    private int length;

    public List<CosmeticEntry> getDownloaded() {
        return this.downloaded;
    }

    public boolean hasNext() {
        return (this.queued.peek() != null);
    }

    public void downloadNext() {
        CosmeticEntry next = this.queued.poll();
        File location = (next == null) ? null : new File(CosmeticManager.getCosmeticsDirectory(), next.getPath());
        if (next == null)
            return;
        if (!location.exists() || !next.validate(location)) {
            try {
                SplashScreen.setProgress(SplashScreen.getProgress(), "Downloading Cosmetic - " + next.getName());
                FileUtils.copyURLToFile(new URL("https://libraries.crystalclient.net/cosmetics/" + next.getPath()), location, 10000, 15000);
                if (next.validate(location))
                    this.downloaded.add(next);
            } catch (IOException ex) {
                Reference.LOGGER.error("Unable to download cosmetic '" + next.getName() + "'", ex);
            }
        } else {
            SplashScreen.setProgress(SplashScreen.getProgress(), "Validating Cosmetic - " + next.getName());
            this.downloaded.add(next);
        }
        next.setResourceLocation(new ResourceLocation("crystalclient", next.getPath()));
        next.setLocation(location);
    }

    public static CosmeticDownloader getInstance() {
        return INSTANCE;
    }

    static {
        if (!CosmeticManager.getCosmeticsDirectory().exists())
            CosmeticManager.getCosmeticsDirectory().mkdirs();
        try {
            HttpsURLConnection conn = (HttpsURLConnection) (new URL("https://libraries.crystalclient.net/cosmetics/cosmetics.json")).openConnection();
            conn.setRequestProperty("User-Agent", "Minecraft/Crystal Client-v1.1.16-projectassfucker");
            conn.setRequestMethod("GET");
            List<CosmeticEntry> entries = Reference.GSON.fromJson(String.join("\n", IOUtils.readLines(conn.getInputStream())), (new TypeToken<ArrayList<CosmeticEntry>>() {

            }).getType());
            INSTANCE.queued.addAll(entries);
            INSTANCE.length = INSTANCE.queued.size();
        } catch (IOException ex) {
            Reference.LOGGER.error("Unable to reach cosmetics endpoint", ex);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\CosmeticDownloader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */