package wdl;

import net.minecraft.client.resources.I18n;

public abstract class MessageTypeCategory {
    public final String internalName;

    public MessageTypeCategory(String internalName) {
        this.internalName = internalName;
    }

    public abstract String getDisplayName();

    public String toString() {
        return "MessageTypeCategory [internalName=" + this.internalName + ", displayName=" +
                getDisplayName() + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + ((this.internalName == null) ? 0 : this.internalName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageTypeCategory other = (MessageTypeCategory) obj;
        if (this.internalName == null)
            return (other.internalName == null);
        return this.internalName.equals(other.internalName);
    }

    public static class I18nableMessageTypeCategory extends MessageTypeCategory {
        public final String i18nKey;

        public I18nableMessageTypeCategory(String internalName, String i18nKey) {
            super(internalName);
            this.i18nKey = i18nKey;
        }

        public String getDisplayName() {
            return I18n.format(this.i18nKey, new Object[0]);
        }
    }

    static final MessageTypeCategory CORE_RECOMMENDED = new I18nableMessageTypeCategory("CORE_RECOMMENDED", "wdl.messages.category.core_recommended");

    static final MessageTypeCategory CORE_DEBUG = new I18nableMessageTypeCategory("CORE_DEBUG", "wdl.messages.category.core_debug");
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\MessageTypeCategory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */