package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.ChatReceivedEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Selector;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.gui.MixinGuiChat;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(name = "Chat Settings", description = "A collection of features relating to the chat box", category = Category.ALL)
public class ChatSettings extends Module implements IRegistrable {
    @Toggle(label = "Clear Chat")
    public boolean clear = false;

    @Toggle(label = "Compact Chat")
    public boolean compact = false;

    @Toggle(label = "Filter Empty Messages")
    public boolean filterEmpty = true;

    @Toggle(label = "Chat Algebra")
    public boolean algebra = false;

    @Toggle(label = "Keep Chat History Across Servers")
    public boolean crossServer = true;

    @Toggle(label = "Timestamps")
    public boolean timestamps = false;

    @Toggle(label = "Smooth Chat")
    public boolean smoothChat = true;

    @Selector(label = "Timestamp Format", values = {"hh:mm a", "hh:mm:ss a", "HH:mm", "HH:mm:ss"})
    public String format = "hh:mm a";

    private static final Pattern MONETARY_VALUE_PATTERN = Pattern.compile("([$€£])[ ]*([1-9][0-9]*(([, ])[0-9]{3})*|0)(\\.[0-9]+)?[ ]*([BbMmKk])?");

    private static final Pattern COORDINATES_PATTERN = Pattern.compile(String.format("(?<x>[-0-9]+)[^-0-9\\_\\[\\]<>;()./\\{}?'\"%s]{1,7}(?<y>[-0-9]+)(?:[^-0-9\\_\\[\\]<>;()./\\{}?'\"%s]{1,7}(?<z>[-0-9]+))?", new Object[]{Character.valueOf('\u00A7'), Character.valueOf('\u00A7')}));

    private static ChatSettings INSTANCE;

    private final LinkedList<ChatEntry> entries = new LinkedList<>();

    private String lastText;

    private int line;

    public ChatSettings() {
        INSTANCE = this;
        this.enabled = true;
    }

    public void onChatReceived(ChatReceivedEvent event) {
        if (!event.isCancelled() && event.getType() != 2) {
            String message = event.message.getUnformattedText();
            if (this.filterEmpty && message.replace(" ", "").trim().isEmpty())
                event.setCancelled(true);
            if (event.message.getUnformattedTextForChat().equals(I18n.format("build.tooHigh", "256"))) {
                event.setType((byte) 2);
                return;
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("[" + this.format + "]"));
            if (!event.isCancelled() && this.compact) {
                GuiNewChat chat = this.mc.ingameGUI.getChatGUI();
                ChatEntry print = null;
                for (ChatEntry entry : this.entries) {
                    if (entry.text.equalsIgnoreCase(message) || (entry.noSpace.length() == 0 && message.replace(" ", "").length() == 0)) {
                        chat.deleteChatLine(entry.id);
                        entry.amount++;
                        event.message.appendText(EnumChatFormatting.GRAY + " (" + entry.amount + ")");
                        print = entry;
                        break;
                    }
                }
                if (print == null) {
                    ChatEntry entry = new ChatEntry((message.replace(" ", "").length() == 0) ? "" : message, 1, this.line);
                    this.entries.add(entry);
                    print = entry;
                    this.entries.remove(print);
                    this.entries.add(print);
                }
                if (this.timestamps) {
                    ChatComponentText timestampFormatted = new ChatComponentText(EnumChatFormatting.GRAY + "[" + timestamp + "] ");
                    timestampFormatted.appendSibling(event.message);
                    event.message = (IChatComponent) timestampFormatted;
                }
                this.line++;
                if (!event.isCancelled())
                    chat.printChatMessageWithOptionalDeletion(event.message, print.id);
                if (this.line > 256)
                    this.line = 0;
                event.setCancelled(true);
            }
            if (!event.isCancelled() && this.timestamps) {
                ChatComponentText timestampFormatted = new ChatComponentText(EnumChatFormatting.GRAY + "[" + timestamp + "] ");
                timestampFormatted.appendSibling(event.message);
                event.message = (IChatComponent) timestampFormatted;
            }
        }
    }

    private void handleChatComponent(String globalMessage, IChatComponent component, Matcher matcher) {
        String x = matcher.group("x"), y = matcher.group("y"), z = matcher.group("z");
        if (!NumberUtils.isNumber(x) || !NumberUtils.isNumber(y) || (z != null && !NumberUtils.isNumber(z)))
            return;
        if (component.getChatStyle().getChatHoverEvent() == null) {
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent) new ChatComponentText(Client.getPrefix() + " Click to create a new waypoint."));
            component.getChatStyle().setChatHoverEvent(hoverEvent);
            component.getSiblings().forEach(s -> s.getChatStyle().setChatHoverEvent(hoverEvent));
        }
        String sender = "Server Location";
        for (NetworkPlayerInfo info : this.mc.getNetHandler().getPlayerInfoMap()) {
            String name = info.getGameProfile().getName();
            if (globalMessage.contains(name)) {
                sender = name + "'s Location";
                break;
            }
        }
        String cmd = String.format("/waypoints tempcreate %s %s %s %s", x, (z == null) ? String.valueOf((int) this.mc.thePlayer.posY + 2) : y, (z == null) ? y : z, sender);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd);
        component.getChatStyle().setChatClickEvent(clickEvent);
        component.getSiblings().forEach(s -> s.getChatStyle().setChatClickEvent(clickEvent));
    }

    public void onClientTick(ClientTickEvent.Post event) {
        if (this.algebra && this.mc.currentScreen instanceof GuiChat) {
            //TODO fix, exp.evaluate fails
      GuiChat chatBar = (GuiChat)this.mc.currentScreen;
      String text = ((MixinGuiChat)chatBar).getInputField().getText();
      char[] operators = { '+', '-', '*', '/', '^', '=', '(' };
      for (char operator : operators) {
          System.out.println(text + " lasttext: " + lastText);
        if (!text.equals(this.lastText) && text.contains(String.valueOf(operator))) {
          double result;
          this.lastText = text;
          try {
            Expression exp = (new ExpressionBuilder(text)).build();
            result = exp.evaluate();
          } catch (Throwable ignored) {
              System.out.println("throwable");
            return;
          }
          Client.sendMessage("&7» " + result, false);
          break;
        }
      }
        }
    }

    public static ChatSettings getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, this::onClientTick);
        EventBus.register(this, ChatReceivedEvent.class, this::onChatReceived);
    }

    static class ChatEntry {
        String text;

        int amount;

        int id;

        String noSpace;

        public ChatEntry(String text, int amount, int id) {
            this.text = text;
            this.amount = amount;
            this.id = id;
            this.noSpace = text.replace(" ", "");
        }
    }
}