package application.items.weapons.shooters.assaultRifles;

import application.config.ConfigHandler;
import application.items.weapons.shooters.BulletGenerator;

public class Ak47 extends AssaultRifle {
  public static final String NAME = "AK-47";

  public Ak47() {
    super(new BulletGenerator(
        ConfigHandler.getDouble("items.weapons.shooters.assaultRifles.Ak47.BULLET_VELOCITY"),
        ConfigHandler.getInt("items.weapons.shooters.assaultRifles.Ak47.FIRE_RATE"),
        ConfigHandler.getInt("items.weapons.shooters.assaultRifles.Ak47.DAMAGE")),
    	ConfigHandler.getDouble("items.weapons.shooters.assaultRifles.Ak47.WEIGHT"));
  }

  @Override
  public String getName() {
    return NAME;
  }
}