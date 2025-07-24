package com.github.timmekeclient.feature.impl.combat;

import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
@ModuleInfo(name = "Effect Alert", description = "Display a text on screen when a certain effect runs out", category = Category.COMBAT)

public class EffectAlert extends Module implements IRegistrable {
    @Override
    public void registerEvents() {

    }
}
