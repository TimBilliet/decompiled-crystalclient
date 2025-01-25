package co.crystaldev.client.account;

import co.crystaldev.client.Reference;
import co.crystaldev.client.gui.screens.ScreenLogin;
import co.crystaldev.client.util.type.Tuple;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.exceptions.AuthenticationException;

import javax.security.auth.login.FailedLoginException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MicrosoftAuthManager {

    public static void login(String authCode) throws IOException, FailedLoginException, AuthenticationException {
        ScreenLogin.feedback = "Acquiring access token...";
        String liveAccessToken = acquireAccessToken(authCode);
        ScreenLogin.feedback = "Acquiring xbl token...";
        String xblToken = acquireXBLToken(liveAccessToken);
        ScreenLogin.feedback = "Acquiring xsts token...";
        Tuple<String, String> xsts = acquireXstsToken(xblToken);
        ScreenLogin.feedback = "Acquiring Minecraft token...";
        String minecraftAccessToken = acquireMinecraftToken(xsts.getItem2(), xsts.getItem1());
        ScreenLogin.feedback = "Checking for game ownership...";
        checkProfile(minecraftAccessToken);
        ScreenLogin.feedback = "Login complete!";
    }

    private static String acquireAccessToken(String authCode) throws IOException {
        Request pr = new Request("https://login.live.com/oauth20_token.srf");
        pr.header("Content-Type", "application/x-www-form-urlencoded");
        HashMap<Object, Object> req = new HashMap<>();
        req.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
        req.put("code", authCode);
        req.put("grant_type", "authorization_code");
        req.put("redirect_uri", "http://localhost:59125");
        req.put("scope", "XboxLive.signin XboxLive.offline_access");
        req.put("prompt", "select_account");
        pr.post(req);
        if (pr.response() >= 200 && pr.response() < 300) {
            JsonObject resp = Reference.GSON.fromJson(pr.body(), JsonObject.class);
            return resp.get("access_token").getAsString();
        } else {
            throw new IllegalArgumentException("acquireAccessToken response: " + pr.response());
        }
    }

    private static String acquireXBLToken(String accessToken) throws IOException {
        Request pr = new Request("https://user.auth.xboxlive.com/user/authenticate");
        pr.header("Content-Type", "application/json");
        pr.header("Accept", "application/json");
        JsonObject req = new JsonObject();
        JsonObject reqProps = new JsonObject();
        reqProps.addProperty("AuthMethod", "RPS");
        reqProps.addProperty("SiteName", "user.auth.xboxlive.com");
        reqProps.addProperty("RpsTicket", "d=" + accessToken);
        req.add("Properties", reqProps);
        req.addProperty("RelyingParty", "http://auth.xboxlive.com");
        req.addProperty("TokenType", "JWT");
        req.addProperty("prompt", "select_account");
        pr.post(req.toString());
        if (pr.response() >= 200 && pr.response() < 300) {
            return (Reference.GSON.fromJson(pr.body(), JsonObject.class)).get("Token").getAsString();
        } else {
            throw new IllegalArgumentException("acquireXBLToken response: " + pr.response());
        }
    }

    private static Tuple<String, String> acquireXstsToken(String xblToken) throws IOException, AuthenticationException {
        Request pr = new Request("https://xsts.auth.xboxlive.com/xsts/authorize");
        pr.header("Content-Type", "application/json");
        pr.header("Accept", "application/json");
        JsonObject req = new JsonObject();
        JsonObject reqProps = new JsonObject();
        JsonArray userTokens = new JsonArray();
        userTokens.add(new JsonPrimitive(xblToken));
        reqProps.add("UserTokens", userTokens);
        reqProps.addProperty("SandboxId", "RETAIL");
        req.add("Properties", reqProps);
        req.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        req.addProperty("TokenType", "JWT");
        req.addProperty("prompt", "select_account");
        pr.post(req.toString());
        if (pr.response() == 401) {
            throw new AuthenticationException("Error 401");
        } else if (pr.response() >= 200 && pr.response() < 300) {
            JsonObject resp = Reference.GSON.fromJson(pr.body(), JsonObject.class);
            return new Tuple<>(resp.get("Token").getAsString(), resp.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString());
        } else {
            throw new IllegalArgumentException("acquireXstsToken response: " + pr.response());
        }
    }

    private static String acquireMinecraftToken(String xblUhs, String xstsToken) throws IOException {
        Request pr = new Request("https://api.minecraftservices.com/authentication/login_with_xbox");
        pr.header("Content-Type", "application/json");
        pr.header("Accept", "application/json");
        JsonObject req = new JsonObject();
        req.addProperty("identityToken", "XBL3.0 x=" + xblUhs + ";" + xstsToken);
        pr.post(req.toString());
        if (pr.response() >= 200 && pr.response() < 300) {
            return (Reference.GSON.fromJson(pr.body(), JsonObject.class)).get("access_token").getAsString();
        } else {
            throw new IllegalArgumentException("acquireMinecraftToken response: " + pr.response());
        }
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
        JsonObject obj = Reference.GSON.fromJson(response.toString(), JsonObject.class);
        String name = obj.get("name").getAsString();
        String uuid = obj.get("id").getAsString();
        AltManager.getInstance().addAccount(new AccountData(mcAccessToken, name, uuid));
    }
}