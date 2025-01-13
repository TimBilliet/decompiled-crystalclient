package com.github.lunatrius.schematica;

import com.github.lunatrius.schematica.handler.client.InputHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.client.Minecraft;

public class Schematica {
    public static Schematica instance;

    public static ClientProxy proxy = new ClientProxy();

    public Schematica() {
        instance = this;
        new InputHandler();
        (Minecraft.getMinecraft()).gameSettings.loadOptions();
    }

    public void init() {
        proxy.init();
    }
}