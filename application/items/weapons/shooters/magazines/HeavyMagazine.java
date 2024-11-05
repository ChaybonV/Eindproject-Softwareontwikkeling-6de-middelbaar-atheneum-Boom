package application.items.weapons.shooters.magazines;

import application.config.ConfigHandler;

public class HeavyMagazine extends Magazine {
  public static String NAME = "Heavy Magazine";

  @Override
  public int getMax() {
    return ConfigHandler.getInt("items.weapons.shooters.magazines.HeavyMagazine.MAX");
  }

  @Override
  public String getName() {
    return NAME;
  }
}