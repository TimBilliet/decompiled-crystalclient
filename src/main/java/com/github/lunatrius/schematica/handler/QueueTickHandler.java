package com.github.lunatrius.schematica.handler;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.chunk.SchematicContainer;
import com.github.lunatrius.schematica.world.schematic.SchematicFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.ArrayDeque;
import java.util.Queue;

public class QueueTickHandler {
  public static final QueueTickHandler INSTANCE = new QueueTickHandler();
  
  private final Queue<SchematicContainer> queue = new ArrayDeque<>();
  
  @SubscribeEvent
  public void onClientTick(ClientTickEvent.Pre event) {
    try {
      EntityPlayerSP player = (Minecraft.getMinecraft()).thePlayer;
      if (player != null && player.sendQueue != null)
        processQueue(); 
    } catch (Exception e) {
      Reference.logger.error("Something went wrong...", e);
    } 
  }
  
  private void processQueue() {
    if (this.queue.size() == 0)
      return; 
    SchematicContainer container = this.queue.poll();
    if (container == null)
      return; 
    if (container.hasNext()) {
      if (container.isFirst()) {
        ChatComponentTranslation chatComponent = new ChatComponentTranslation(I18n.format("schematica.command.save.started", new Object[0]), new Object[] { Integer.valueOf(container.chunkCount), container.file.getName() });
        container.player.addChatMessage((IChatComponent)chatComponent);
      } 
      container.next();
    } 
    if (container.hasNext()) {
      this.queue.offer(container);
    } else {
      SchematicFormat.writeToFileAndNotify(container.file, container.schematic, container.player);
    } 
  }
  
  public void queueSchematic(SchematicContainer container) {
    this.queue.offer(container);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\QueueTickHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */