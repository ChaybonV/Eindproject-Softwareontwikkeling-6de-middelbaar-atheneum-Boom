package application.items.weapons.shooters.pistols;

import application.config.ConfigHandler;
import application.items.weapons.shooters.BulletGenerator;

public class Glock extends Pistol {
  public static final String NAME = "Glock";

  public Glock() {
    super(new BulletGenerator(
        ConfigHandler.getDouble("items.weapons.shooters.pistols.Glock.BULLET_VELOCITY"),
        ConfigHandler.getInt("items.weapons.shooters.pistols.Glock.FIRE_RATE"),
        ConfigHandler.getInt("items.weapons.shooters.pistols.Glock.DAMAGE")),
    	ConfigHandler.getDouble("items.weapons.shooters.pistols.Glock.WEIGHT")
    );
  }

  @Override
  public String getName() {
    return NAME;
  }
}