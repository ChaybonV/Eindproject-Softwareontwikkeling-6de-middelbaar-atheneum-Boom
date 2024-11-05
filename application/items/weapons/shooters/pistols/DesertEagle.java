package application.items.weapons.shooters.pistols;

import application.config.ConfigHandler;
import application.items.weapons.shooters.BulletGenerator;

public class DesertEagle extends Pistol {
  public static final String NAME = "Desert Eagle";

  public DesertEagle() {
    super(new BulletGenerator(
        ConfigHandler.getDouble("items.weapons.shooters.pistols.DesertEagle.BULLET_VELOCITY"),
        ConfigHandler.getInt("items.weapons.shooters.pistols.DesertEagle.FIRE_RATE"),
        ConfigHandler.getInt("items.weapons.shooters.pistols.DesertEagle.DAMAGE")),
    	ConfigHandler.getDouble("items.weapons.shooters.pistols.DesertEagle.WEIGHT")
    );
  }

  @Override
  public String getName() {
    return NAME;
  }
}