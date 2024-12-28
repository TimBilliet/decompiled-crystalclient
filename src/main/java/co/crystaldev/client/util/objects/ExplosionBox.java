package co.crystaldev.client.util.objects;

import co.crystaldev.client.feature.impl.factions.ExplosionBoxes;

public class ExplosionBox {
  private final long initTime;

  public final Vec3d pos;

  public ExplosionBox(Vec3d pos) {
    this.pos = pos;
    this.initTime = System.currentTimeMillis();
  }

  public boolean expired() {
    return (this.initTime + (ExplosionBoxes.getInstance()).timeout * 1000L < System.currentTimeMillis());
  }
}