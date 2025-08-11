package com.github.timmekeclient.account;

import com.mojang.authlib.exceptions.AuthenticationException;
import org.apache.commons.lang3.tuple.Pair;

import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.util.UUID;

public class AuthManager {

    public static boolean login(AccountData data) throws IOException {
        try {
            refreshToken(data);
            AltManager.getInstance().addAccount(new AccountData(data.getMcAccessToken(), data.getRefreshToken(), data.getName(), data.getUuidString()));
        } catch (Exception e) {
            AltManager.getInstance().removeAccount(data);
            return false;
        }
        return true;
    }

    public static void refreshToken(AccountData data) throws AuthenticationException, IOException, FailedLoginException {
        try {
            MicrosoftAuthManager.checkGameOwnerShip(data.getMcAccessToken());
            Pair<UUID, String> profile = MicrosoftAuthManager.getProfile(data.getMcAccessToken());
            data.setUuid(profile.getLeft());
            data.setName(profile.getRight());
        } catch (Exception e) {
            try {
                Pair<String, String> authRefreshTokens = MicrosoftAuthManager.refreshToken(data.getRefreshToken());
                String refreshToken = authRefreshTokens.getRight();
                String xblToken = MicrosoftAuthManager.acquireXBLToken(authRefreshTokens.getLeft());
                Pair<String, String> xstsToken = MicrosoftAuthManager.acquireXstsToken(xblToken);
                String accessToken = MicrosoftAuthManager.acquireMinecraftToken(xstsToken.getRight(), xstsToken.getLeft());
                MicrosoftAuthManager.checkGameOwnerShip(accessToken);
                Pair<UUID, String> profile = MicrosoftAuthManager.getProfile(accessToken);
                data.setUuid(profile.getLeft());
                data.setName(profile.getRight());
                data.setMcAccessToken(accessToken);
                data.setRefreshToken(refreshToken);
            } catch (Exception ex) {
                ex.addSuppressed(e);
                throw ex;
            }
        }
    }
}