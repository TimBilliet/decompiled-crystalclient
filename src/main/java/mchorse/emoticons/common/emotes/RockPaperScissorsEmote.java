package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RockPaperScissorsEmote extends Emote {
    public String suffix = "";

    public RockPaperScissorsEmote(String name, int duration, boolean looping, String sound) {
        super(name, duration, looping, sound);
    }

    public RockPaperScissorsEmote(String name, int duration, boolean looping, String sound, String suffix) {
        super(name, duration, looping, sound);
        this.suffix = suffix;
    }

    public Emote getDynamicEmote() {
        int rand = this.rand.nextInt(30);
        String suffix = "";
        if (rand <= 10) {
            suffix = "rock";
        } else if (rand <= 20) {
            suffix = "paper";
        } else if (rand <= 30) {
            suffix = "scissors";
        }
        return getDynamicEmote(suffix);
    }

    public Emote getDynamicEmote(String suffix) {
        return new RockPaperScissorsEmote(this.name, this.duration, this.looping, this.sound, suffix);
    }

    public String getKey() {
        return this.name + (this.suffix.isEmpty() ? "" : (":" + this.suffix));
    }

    public void startAnimation(AnimatorEmoticonsController animator) {
        if (this.suffix.equals("rock")) {
            animator.itemSlot = new ItemStack(Blocks.stone, 1);
        } else if (this.suffix.equals("paper")) {
            animator.itemSlot = new ItemStack(Items.paper, 1);
        } else if (this.suffix.equals("scissors")) {
            animator.itemSlot = new ItemStack((Item) Items.shears, 1);
        }
        animator.itemSlotScale = 0.0F;
    }

    public void progressAnimation(EntityLivingBase entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {
        if (tick > 25 && tick < 55) {
            if (tick < 30) {
                animator.itemSlotScale = ((tick - 25) + partial) / 5.0F;
            } else if (tick >= 50) {
                animator.itemSlotScale = 1.0F - ((tick - 50) + partial) / 5.0F;
            } else {
                animator.itemSlotScale = 1.0F;
            }
        } else {
            animator.itemSlotScale = 0.0F;
        }
    }

    public void stopAnimation(AnimatorEmoticonsController animator) {
        animator.itemSlot = null;
        animator.itemSlotScale = 0.0F;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\RockPaperScissorsEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */