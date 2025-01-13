package co.crystaldev.client.mixin.accessor.net.minecraft.network.play.server;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({S02PacketChat.class})
public interface MixinS02PacketChat {
    @Accessor("chatComponent")
    void setChatComponent(IChatComponent paramIChatComponent);

    @Accessor("type")
    void setType(byte paramByte);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\network\play\server\MixinS02PacketChat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */