package application.items.weapons.shooters.submachineGuns;

import application.config.ConfigHandler;
import application.items.weapons.shooters.BulletGenerator;

public class P90 extends SubmachineGun {
  public static final String NAME = "P90";

  public P90() {
    super(new BulletGenerator(
        ConfigHandler.getDouble("items.weapons.shooters.submachineGuns.P90.BULLET_VELOCITY"),
        ConfigHandler.getInt("items.weapons.shooters.submachineGuns.P90.FIRE_RATE"),
        ConfigHandler.getInt("items.weapons.shooters.submachineGuns.P90.DAMAGE")),
    	ConfigHandler.getDouble("items.weapons.shooters.submachineGuns.P90.WEIGHT"));
  }

  @Override
  public String getName() {
    return NAME;
  }
}