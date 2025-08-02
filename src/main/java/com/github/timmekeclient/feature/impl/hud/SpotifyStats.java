package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.util.RenderUtils;
import com.github.timmekeclient.util.enums.AnchorRegion;
import com.github.timmekeclient.util.objects.ModulePosition;
import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyAPIFactory;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.Track;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

@ModuleInfo(name = "Spotify Stats", description = "Show the spotify song that's currently playing", category = Category.HUD)

public class SpotifyStats extends HudModuleBackground implements SpotifyListener {

    @Toggle(label = "Show Album Cover")
    public boolean showCover = true;

    @Toggle(label = "Show Progress")
    public boolean showProgress = true;

    private SpotifyAPI spotifyAPI;
    private Track currentTrack;
    private int length;
    private int progress;
    private ResourceLocation cover;
    private boolean isPlaying = true;

    public SpotifyStats() {
        enabled = false;
        hasInfoHud = false;
        height = 47;
        width = 180;
        position = new ModulePosition(AnchorRegion.TOP_CENTER, 0.0F, 5.0F);
        spotifyAPI = SpotifyAPIFactory.createInitialized();
        spotifyAPI.registerListener(this);
    }

    @Override
    public void enable() {
        super.enable();
        System.out.println("on enable spotifystats");
        spotifyAPI = SpotifyAPIFactory.createInitialized();
        spotifyAPI.registerListener(this);
    }

    @Override
    public void disable() {
        super.disable();
        if (spotifyAPI != null)
            spotifyAPI.stop();
    }

    @Override
    public String getDisplayText() {
        return "";
    }

    public void draw() {
        if (currentTrack != null) {
            int x = getRenderX();
            int y = getRenderY();
            if (drawBackground)
                drawBackground(x, y, x + width, y + height);
            int iconSize = 45;
            y+=4;
            RenderUtils.drawString(currentTrack.getName(), x + iconSize, y, textColor);
            y+=10;
            RenderUtils.drawString(currentTrack.getArtist(), x + iconSize, y, textColor);
            y+=10;
            if(isPlaying)
                RenderUtils.drawString("Playing...", x + iconSize, y, textColor);
            else
                RenderUtils.drawString("Paused...", x + iconSize, y, textColor);
            y+=13;
            RenderUtils.drawString(String.valueOf(progress), x + 10, y, textColor);
            RenderUtils.drawString(String.valueOf(length), x + 50, y, textColor);
            if (showCover && cover != null) {
                GlStateManager.enableBlend();
                GlStateManager.resetColor();
                this.mc.getTextureManager().bindTexture(cover);
                Gui.drawModalRectWithCustomSizedTexture(x - 20, y, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
                GlStateManager.disableBlend();
            }
        }

    }

    private void convertCoverImage() {
        mc.addScheduledTask(() -> {
            BufferedImage coverBI = currentTrack.getCoverArt();
            if (coverBI != null) {
                int newHeight = coverBI.getHeight() - 65;
                coverBI = coverBI.getSubimage(0,0,coverBI.getWidth(), newHeight);
                DynamicTexture dynamicTexture = new DynamicTexture(coverBI);
                mc.getTextureManager().loadTexture(new ResourceLocation("timmekeclient", "spotify_track"), dynamicTexture);
                cover = new ResourceLocation("timmekeclient", "spotify_track");

            } else {
                System.out.println("cover is null");
            }
        });
    }

    private void renderProgressBar(){

    }

    @Override
    public void onConnect() {
        Reference.LOGGER.info("Connected to Spotify");
    }

    @Override
    public void onTrackChanged(Track track) {
        currentTrack = track;
        System.out.println("track changed");
        convertCoverImage();

    }

    @Override
    public void onPositionChanged(int position) {
        System.out.println(position);
        length = currentTrack.getLength()/1000;
        progress = position/1000;
    }

    @Override
    public void onPlayBackChanged(boolean isPlaying) {
        System.out.println("playback changed");
        this.isPlaying = isPlaying;
        System.out.println(currentTrack.getLength());

        if (spotifyAPI.hasTrack()) {
            currentTrack = spotifyAPI.getTrack();
            convertCoverImage();
            progress = spotifyAPI.getPosition()/1000;
        }
    }

    @Override
    public void onSync() {

    }

    @Override
    public void onDisconnect(Exception exception) {
//        currentTrack = null;
    }
}
