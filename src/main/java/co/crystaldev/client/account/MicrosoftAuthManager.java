package co.crystaldev.client.account;

import co.crystaldev.client.Reference;
import co.crystaldev.client.util.type.Tuple;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;

import javax.security.auth.login.FailedLoginException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MicrosoftAuthManager {
    public static void login(String authCode) {
        try {
            String liveAccessToken = acquireAccessToken(authCode);
            String xblToken = acquireXBLToken(liveAccessToken);
            Tuple<String, String> xsts = acquireXstsToken(xblToken);
            String minecraftAccessToken = acquireMinecraftToken((String) xsts.getItem1(), (String) xsts.getItem2());
            checkProfile(minecraftAccessToken);
        } catch (IOException | FailedLoginException iOException) {
        }
    }

    public static void loginNoCatch(String authCode) throws IOException, FailedLoginException {
        String liveAccessToken = acquireAccessToken(authCode);
        String xblToken = acquireXBLToken(liveAccessToken);
        Tuple<String, String> xsts = acquireXstsToken(xblToken);
        String minecraftAccessToken = acquireMinecraftToken((String) xsts.getItem1(), (String) xsts.getItem2());
        checkProfile(minecraftAccessToken);
    }

    private static String acquireAccessToken(String authCode) throws IOException {
        URL url = new URL("https://login.live.com/oauth20_token.srf");
        ImmutableMap immutableMap = ImmutableMap.builder().put("client_id", "00000000402b5328").put("code", authCode).put("grant_type", "authorization_code").put("redirect_uri", "https://login.live.com/oauth20_desktop.srf").put("scope", "service::user.auth.xboxlive.com::MBI_SSL").build();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(ofFormData((Map<Object, Object>) immutableMap).getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();
        connection.getOutputStream().close();
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(response.toString(), JsonObject.class);
        return obj.get("access_token").getAsString();
    }

    private static String acquireXBLToken(String accessToken) throws IOException {
        URL url = new URL("https://user.auth.xboxlive.com/user/authenticate");
        JsonObject data = new JsonObject();
        JsonObject props = new JsonObject();
        props.addProperty("AuthMethod", "RPS");
        props.addProperty("SiteName", "user.auth.xboxlive.com");
        props.addProperty("RpsTicket", accessToken);
        data.add("Properties", (JsonElement) props);
        data.addProperty("RelyingParty", "http://auth.xboxlive.com");
        data.addProperty("TokenType", "JWT");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();
        connection.getOutputStream().close();
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(response.toString(), JsonObject.class);
        return obj.get("Token").getAsString();
    }

    private static Tuple<String, String> acquireXstsToken(String xblToken) throws IOException, FailedLoginException {
        URL url = new URL("https://xsts.auth.xboxlive.com/xsts/authorize");
        JsonObject data = new JsonObject();
        JsonObject props = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add((JsonElement) new JsonPrimitive(xblToken));
        props.addProperty("SandboxId", "RETAIL");
        props.add("UserTokens", (JsonElement) arr);
        data.add("Properties", (JsonElement) props);
        data.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        data.addProperty("TokenType", "JWT");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(Reference.GSON.toJson((JsonElement) data).getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();
        connection.getOutputStream().close();
        try {
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            JsonObject obj = (JsonObject) Reference.GSON.fromJson(response.toString(), JsonObject.class);
            String xstsToken = obj.get("Token").getAsString();
            JsonObject claims = (JsonObject) obj.get("DisplayClaims");
            JsonArray xui = claims.getAsJsonArray("xui");
            String xblUhs = xui.get(0).getAsJsonObject().get("uhs").getAsString();
            return new Tuple(xblUhs, xstsToken);
        } catch (IOException ex) {
            InputStream is = connection.getErrorStream();
            if (is != null) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                JsonObject obj = (JsonObject) Reference.GSON.fromJson(response.toString(), JsonObject.class);
                if (obj.has("XErr")) {
                    long errorCode = obj.get("XErr").getAsLong();
                    if (errorCode == 2148916233L)
                        throw new FailedLoginException("This Microsoft account is not signed up with Xbox, please login to minecraft.net to continue.");
                    if (errorCode == 2148916238L &&
                            !obj.has("Redirect"))
                        throw new FailedLoginException("The Microsoft account holder is under 18, please add this account to a family to continue.");
                }
            }
            throw ex;
        }
    }

    private static String acquireMinecraftToken(String xblUhs, String xstsToken) throws IOException {
        URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
        JsonObject data = new JsonObject();
        data.addProperty("identityToken", "XBL3.0 x=" + xblUhs + ";" + xstsToken);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();
        connection.getOutputStream().close();
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(response.toString(), JsonObject.class);
        return obj.get("access_token").getAsString();
    }

    private static void checkProfile(String mcAccessToken) throws IOException, FailedLoginException {
        URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        if (connection.getResponseCode() == 404)
            throw new FailedLoginException("This Microsoft account does not own Minecraft.");
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(response.toString(), JsonObject.class);
        String name = obj.get("name").getAsString();
        String uuid = obj.get("id").getAsString();
        AltManager.getInstance().addAccount(new AccountData(mcAccessToken, name, uuid));
    }

    public static String ofFormData(Map<Object, Object> data) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0)
                builder.append("&");
            builder.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        return builder.toString();
    }

    private static final Minecraft mc = Minecraft.getMinecraft();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\account\MicrosoftAuthManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */