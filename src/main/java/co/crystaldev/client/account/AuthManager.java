package co.crystaldev.client.account;

import co.crystaldev.client.Reference;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthManager {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean login(AccountData data) throws IOException {
        URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + data.getAccessToken());
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        if (connection.getResponseCode() > 399) {
            AltManager.getInstance().removeAccount(data);
            return false;
        }
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        JsonObject obj = Reference.GSON.fromJson(response.toString(), JsonObject.class);
        AltManager.getInstance().addAccount(new AccountData(data.getAccessToken(), obj.get("name").getAsString(), obj.get("id").getAsString()));
        return true;
    }
}