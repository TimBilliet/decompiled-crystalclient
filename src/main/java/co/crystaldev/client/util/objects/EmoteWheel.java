package co.crystaldev.client.util.objects;

import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.settings.ClientOptions;
import com.google.gson.annotations.SerializedName;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;

import java.util.LinkedList;

public class EmoteWheel {
    @SerializedName("emotes")
    private final LinkedList<String> emotes = new LinkedList<>();

    public LinkedList<String> getEmotes() {
        return this.emotes;
    }

    public void addEmote(String emote) {
        if (hasSelected(emote)) {
            this.emotes.remove(emote);
            save();
            return;
        }
        if (this.emotes.size() >= 6) {
            this.emotes.remove(this.emotes.getFirst());
            save();
        }
        this.emotes.add(emote);
        save();
    }

    public Emote getEmote(int index) {
        if (index >= 0 && this.emotes.size() > index)
            return Emotes.get(this.emotes.get(index));
        return null;
    }

    public boolean hasSelected(String emote) {
        return this.emotes.contains(emote);
    }

    public boolean hasSelected(Emote emote) {
        return hasSelected(emote.name);
    }

    private void save() {
        (ClientOptions.getInstance()).emoteCache = Reference.GSON.toJson(this);
    }
}