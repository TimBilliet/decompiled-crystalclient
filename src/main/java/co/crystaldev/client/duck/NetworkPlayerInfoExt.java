package co.crystaldev.client.duck;

public interface NetworkPlayerInfoExt {
    void setCrystalOnlineStatus(boolean paramBoolean);
    void setOrbitOnlineStatus(boolean paramBoolean);

    boolean isOnCrystalClient();
    boolean isOnOrbitClient();
}
