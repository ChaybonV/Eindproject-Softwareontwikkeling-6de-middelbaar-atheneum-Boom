package application.items.weapons.shooters.assaultRifles;

import application.items.weapons.shooters.BulletGenerator;
import application.items.weapons.shooters.Shooter;
import application.items.weapons.shooters.magazines.HeavyMagazine;
import application.items.weapons.shooters.magazines.Magazine;

public abstract class AssaultRifle extends Shooter < HeavyMagazine > {
  public AssaultRifle(BulletGenerator bulletGenerator, double weight) {
    super(bulletGenerator, weight);
  }

  @Override
  public boolean canHaveMagazine(Magazine magazine) {
    return magazine instanceof HeavyMagazine;
  }
}