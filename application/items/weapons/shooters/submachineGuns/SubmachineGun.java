package application.items.weapons.shooters.submachineGuns;

import application.items.weapons.shooters.BulletGenerator;
import application.items.weapons.shooters.Shooter;
import application.items.weapons.shooters.magazines.Magazine;
import application.items.weapons.shooters.magazines.MediumMagazine;

public abstract class SubmachineGun extends Shooter < MediumMagazine > {
  public SubmachineGun(BulletGenerator bulletGenerator, double weight) {
    super(bulletGenerator, weight);
  }

  @Override
  public boolean canHaveMagazine(Magazine magazine) {
    return magazine instanceof MediumMagazine;
  }
}