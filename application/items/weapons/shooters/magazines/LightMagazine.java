package application.items.weapons.shooters.magazines;

import application.config.ConfigHandler;

public class LightMagazine extends Magazine {
  public static String NAME = "Light Magazine";

  @Override
  public int getMax() {
    return ConfigHandler.getInt("items.weapons.shooters.magazines.LightMagazine.MAX");
  }

  @Override
  public String getName() {
    return NAME;
  }
}