package co.crystaldev.client.account;

import co.crystaldev.client.Reference;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.util.UUID;

public class MojangAuthManager {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final YggdrasilAuthenticationService yas = new YggdrasilAuthenticationService(mc.getProxy(), UUID.randomUUID().toString());

    private static final YggdrasilUserAuthentication yua = (YggdrasilUserAuthentication) yas.createUserAuthentication(Agent.MINECRAFT);

    private static final YggdrasilMinecraftSessionService ymss = (YggdrasilMinecraftSessionService) yas.createMinecraftSessionService();

    public static void login(String username, String password) {
        try {
            try {
                Field field = yas.getClass().getField("clientToken");
                field.setAccessible(true);
                field.set(yas, AltManager.getClientToken().toString());
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                Reference.LOGGER.error("Exception thrown while updating clientToken field");
            }
            yua.setUsername(username);
            yua.setPassword(password);
            yua.logIn();
            String name = yua.getSelectedProfile().getName();
            String id = UUIDTypeAdapter.fromUUID(yua.getSelectedProfile().getId());
            String token = yua.getAuthenticatedToken();
            yua.logOut();
            AltManager.getInstance().addAccount(new AccountData(token, name, id));
        } catch (AuthenticationException ex) {
            Reference.LOGGER.error("Error while logging in", (Throwable) ex);
        }
    }

    public static void loginNoCatch(String username, String password) throws AuthenticationException {
        yua.setUsername(username);
        yua.setPassword(password);
        yua.logIn();
        String name = yua.getSelectedProfile().getName();
        String id = UUIDTypeAdapter.fromUUID(yua.getSelectedProfile().getId());
        String token = yua.getAuthenticatedToken();
        AltManager.getInstance().addAccount(new AccountData(token, name, id));
        yua.logOut();
    }

    public static boolean validateSession() {
        try {
            String fakeServerId = UUID.randomUUID().toString();
            ymss.joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), fakeServerId);
            if (ymss.hasJoinedServer(mc.getSession().getProfile(), fakeServerId).isComplete())
                return true;
        } catch (AuthenticationException ex) {
            Reference.LOGGER.error("Session validation failed", (Throwable) ex);
            return false;
        }
        return false;
    }
}