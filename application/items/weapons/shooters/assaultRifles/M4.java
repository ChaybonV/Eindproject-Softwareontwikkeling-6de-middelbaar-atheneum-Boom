package application.items.weapons.shooters.assaultRifles;

import application.config.ConfigHandler;
import application.items.weapons.shooters.BulletGenerator;

public class M4 extends AssaultRifle {
  public static final String NAME = "M4";

  public M4() {
    super(new BulletGenerator(
        ConfigHandler.getDouble("items.weapons.shooters.assaultRifles.M4.BULLET_VELOCITY"),
        ConfigHandler.getInt("items.weapons.shooters.assaultRifles.M4.FIRE_RATE"),
        ConfigHandler.getInt("items.weapons.shooters.assaultRifles.M4.DAMAGE")),
    	ConfigHandler.getDouble("items.weapons.shooters.assaultRifles.M4.WEIGHT")
    );
  }

  @Override
  public String getName() {
    return NAME;
  }
}