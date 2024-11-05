package application.items.weapons.shooters.submachineGuns;

import application.config.ConfigHandler;
import application.items.weapons.shooters.BulletGenerator;

public class Uzi extends SubmachineGun {
  public static final String NAME = "Uzi";

  public Uzi() {
    super(new BulletGenerator(
        ConfigHandler.getDouble("items.weapons.shooters.submachineGuns.Uzi.BULLET_VELOCITY"),
        ConfigHandler.getInt("items.weapons.shooters.submachineGuns.Uzi.FIRE_RATE"),
        ConfigHandler.getInt("items.weapons.shooters.submachineGuns.Uzi.DAMAGE")),
    	ConfigHandler.getDouble("items.weapons.shooters.submachineGuns.Uzi.WEIGHT")
    );
  }

  @Override
  public String getName() {
    return NAME;
  }
}