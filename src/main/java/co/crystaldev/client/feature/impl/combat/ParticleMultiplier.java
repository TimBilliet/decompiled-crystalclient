package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.entity.EntityAttackEvent;
import co.crystaldev.client.event.impl.entity.EntityCriticalStrikeEvent;
import co.crystaldev.client.event.impl.entity.EntityEnchantCriticalStrikeEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.PageBreak;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import net.minecraft.util.EnumParticleTypes;

@ModuleInfo(name = "Particle Multiplier", description = "Increases the amount of hit (regular and critical) particles", category = Category.COMBAT)
public class ParticleMultiplier extends Module implements IRegistrable {
  @Slider(label = "Multiplier", minimum = 1.0D, maximum = 10.0D, standard = 2.0D, integers = true)
  public int multiplier = 2;
  
  @PageBreak(label = "Effects")
  @Toggle(label = "Show Criticals")
  public boolean showCriticals = true;
  
  @Toggle(label = "Affect Criticals")
  public boolean affectCriticals = true;
  
  @Toggle(label = "Show Sharpness")
  public boolean showSharpness = false;
  
  @Toggle(label = "Affect Sharpness")
  public boolean affectSharpness = true;
  
  public ParticleMultiplier() {
    this.enabled = false;
  }
  
  public void registerEvents() {
    EventBus.register(this, EntityAttackEvent.Pre.class, ev -> {
          if (ev.getTarget() instanceof net.minecraft.entity.EntityLivingBase && this.showSharpness && ev.getDistance() <= 3.0D)
            for (int i = 0; i < this.multiplier; i++)
              this.mc.effectRenderer.emitParticleAtEntity(ev.getTarget(), EnumParticleTypes.CRIT_MAGIC);  
        });
    EventBus.register(this, EntityCriticalStrikeEvent.class, ev -> {
          if (ev.getEntity() instanceof net.minecraft.entity.EntityLivingBase)
            if (!this.showCriticals) {
              ev.setCancelled(true);
            } else if (this.affectCriticals) {
              for (int i = 0; i < this.multiplier; i++)
                this.mc.effectRenderer.emitParticleAtEntity(ev.getEntity(), EnumParticleTypes.CRIT); 
            }  
        });
    EventBus.register(this, EntityEnchantCriticalStrikeEvent.class, ev -> {
          if (ev.getEntity() instanceof net.minecraft.entity.EntityLivingBase && this.affectSharpness)
            for (int i = 0; i < this.multiplier; i++)
              this.mc.effectRenderer.emitParticleAtEntity(ev.getEntity(), EnumParticleTypes.CRIT_MAGIC);  
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\combat\ParticleMultiplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */