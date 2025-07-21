package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;


@ModuleInfo(name = "Spotify Stats", description = "Show the spotify song that's currently playing", category = Category.HUD)

public class SpotifyStats extends HudModuleBackground {

    @Toggle(label = "Show Album Cover")
    public boolean showCover = true;

    @Toggle(label = "Show Progress")
    public boolean showProgress = true;

    public SpotifyStats(){

    }


    @Override
    public String getDisplayText() {
        return "t";
    }
}
