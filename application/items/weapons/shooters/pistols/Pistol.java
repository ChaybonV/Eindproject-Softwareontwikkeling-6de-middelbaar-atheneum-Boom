package application.items.weapons.shooters.pistols;

import application.items.weapons.shooters.BulletGenerator;
import application.items.weapons.shooters.Shooter;
import application.items.weapons.shooters.magazines.LightMagazine;
import application.items.weapons.shooters.magazines.Magazine;

public abstract class Pistol extends Shooter < LightMagazine > {
  public Pistol(BulletGenerator bulletGenerator, double weight) {
    super(bulletGenerator, weight);
  }

  @Override
  public boolean canHaveMagazine(Magazine magazine) {
    return magazine instanceof LightMagazine;
  }
}