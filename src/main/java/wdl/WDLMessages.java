package wdl;

import co.crystaldev.client.Client;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.api.IWDLMessageType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class WDLMessages {
    private static final Logger logger = LogManager.getLogger();

    private static class MessageRegistration {
        public final String name;

        public final IWDLMessageType type;

        public final MessageTypeCategory category;

        public MessageRegistration(String name, IWDLMessageType type, MessageTypeCategory category) {
            this.name = name;
            this.type = type;
            this.category = category;
        }

        public String toString() {
            return "MessageRegistration [name=" + this.name + ", type=" + this.type + ", category=" + this.category + "]";
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
            result = 31 * result + ((this.category == null) ? 0 : this.category.hashCode());
            result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof MessageRegistration))
                return false;
            MessageRegistration other = (MessageRegistration) obj;
            if (this.name == null) {
                if (other.name != null)
                    return false;
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            if (this.category == null) {
                if (other.category != null)
                    return false;
            } else if (!this.category.equals(other.category)) {
                return false;
            }
            if (this.type == null)
                return (other.type == null);
            return this.type.equals(other.type);
        }
    }

    public static boolean enableAllMessages = true;

    private static final List<MessageRegistration> registrations = new ArrayList<>();

    private static MessageRegistration getRegistration(String name) {
        for (MessageRegistration r : registrations) {
            if (r.name.equals(name))
                return r;
        }
        return null;
    }

    private static MessageRegistration getRegistration(IWDLMessageType type) {
        for (MessageRegistration r : registrations) {
            if (r.type.equals(type))
                return r;
        }
        return null;
    }

    public static void registerMessage(String name, IWDLMessageType type, MessageTypeCategory category) {
        registrations.add(new MessageRegistration(name, type, category));
        WDL.defaultProps.setProperty("Messages." + name,
                Boolean.toString(type.isEnabledByDefault()));
        WDL.defaultProps.setProperty("MessageGroup." + category.internalName, "true");
    }

    public static boolean isEnabled(IWDLMessageType type) {
        if (type == null)
            return false;
        if (!enableAllMessages)
            return false;
        MessageRegistration r = getRegistration(type);
        if (r == null)
            return false;
        if (!isGroupEnabled(r.category))
            return false;
        if (!WDL.baseProps.containsKey("Messages." + r.name))
            if (WDL.baseProps.containsKey("Debug." + r.name)) {
                WDL.baseProps.put("Messages." + r.name, WDL.baseProps
                        .remove("Debug." + r.name));
            } else {
                WDL.baseProps.setProperty("Messages." + r.name,
                        Boolean.toString(r.type.isEnabledByDefault()));
            }
        return WDL.baseProps.getProperty("Messages." + r.name).equals("true");
    }

    public static void toggleEnabled(IWDLMessageType type) {
        MessageRegistration r = getRegistration(type);
        if (r != null)
            WDL.baseProps.setProperty("Messages." + r.name,
                    Boolean.toString(!isEnabled(type)));
    }

    public static boolean isGroupEnabled(MessageTypeCategory group) {
        if (!enableAllMessages)
            return false;
        return WDL.baseProps.getProperty("MessageGroup." + group.internalName, "true")
                .equals("true");
    }

    public static void toggleGroupEnabled(MessageTypeCategory group) {
        WDL.baseProps.setProperty("MessageGroup." + group.internalName,
                Boolean.toString(!isGroupEnabled(group)));
    }

    public static ListMultimap<MessageTypeCategory, IWDLMessageType> getTypes() {
        LinkedListMultimap linkedListMultimap = LinkedListMultimap.create();
        for (MessageRegistration r : registrations)
            linkedListMultimap.put(r.category, r.type);
        return (ListMultimap<MessageTypeCategory, IWDLMessageType>) ImmutableListMultimap.copyOf((Multimap) linkedListMultimap);
    }

    public static void resetEnabledToDefaults() {
        WDL.baseProps.setProperty("Messages.enableAll", "true");
        enableAllMessages = WDL.globalProps.getProperty("Messages.enableAll", "true").equals("true");
        for (MessageRegistration r : registrations) {
            WDL.baseProps.setProperty("MessageGroup." + r.category.internalName, WDL.globalProps

                    .getProperty("MessageGroup." + r.category.internalName, "true"));
            WDL.baseProps.setProperty("Messages." + r.name, WDL.globalProps

                    .getProperty("Messages." + r.name));
        }
    }

    public static void onNewServer() {
        if (!WDL.baseProps.containsKey("Messages.enableAll"))
            if (WDL.baseProps.containsKey("Debug.globalDebugEnabled")) {
                WDL.baseProps.put("Messages.enableAll", WDL.baseProps
                        .remove("Debug.globalDebugEnabled"));
            } else {
                WDL.baseProps.setProperty("Messages.enableAll", WDL.globalProps
                        .getProperty("Messages.enableAll", "true"));
            }
        enableAllMessages = WDL.baseProps.getProperty("Messages.enableAll").equals("true");
    }

    public static void chatMessage(IWDLMessageType type, String message) {
        chatMessage(type, (IChatComponent) new ChatComponentText(message));
    }

    public static void chatMessageTranslated(IWDLMessageType type, String translationKey, Object... args) {
        List<Throwable> exceptionsToPrint = new ArrayList<>();
        int i;
        for (i = 0; i < args.length; i++) {
            if (args[i] instanceof Entity) {
                IChatComponent chatComponentText;
                Entity e = (Entity) args[i];
                String entityType = EntityUtils.getEntityType(e);
                HoverEvent event = null;
                String customName = null;
                try {
                    event = e.getDisplayName().getChatStyle().getChatHoverEvent();
                    if (e.hasCustomName())
                        customName = e.getCustomNameTag();
                } catch (Exception exception) {
                }
                if (customName != null) {
                    chatComponentText = new ChatComponentTranslation("wdl.messages.entityTypeAndCustomName", entityType, customName);
                } else {
                    chatComponentText = new ChatComponentText(entityType);
                }
                chatComponentText.setChatStyle(chatComponentText.getChatStyle()
                        .setChatHoverEvent(event));
                args[i] = chatComponentText;
            } else if (args[i] instanceof Throwable) {
                Throwable t = (Throwable) args[i];
                ChatComponentText chatComponentText = new ChatComponentText(t.toString());
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                exceptionAsString = exceptionAsString.replace("\r", "").replace("\t", "    ");
                HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent) new ChatComponentText(exceptionAsString));
                chatComponentText.setChatStyle(chatComponentText.getChatStyle()
                        .setChatHoverEvent(event));
                logger.warn(t);
                args[i] = chatComponentText;
                exceptionsToPrint.add(t);
            }
        }
        chatMessage(type, (IChatComponent) new ChatComponentTranslation(translationKey, args));
        for (i = 0; i < exceptionsToPrint.size(); i++)
            logger.warn("Exception #" + (i + 1) + ": ", exceptionsToPrint.get(i));
    }

    public static void chatMessage(IWDLMessageType type, IChatComponent message) {
        if (isEnabled(type)) {
            Client.sendMessage(message.getUnformattedText(), true);
        } else {
            logger.info(message.getUnformattedText());
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WDLMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */