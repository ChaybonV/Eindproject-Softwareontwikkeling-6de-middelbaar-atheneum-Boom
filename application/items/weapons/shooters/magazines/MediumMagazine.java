package application.items.weapons.shooters.magazines;

import application.config.ConfigHandler;

public class MediumMagazine extends Magazine {
  public static String NAME = "Medium Magazine";

  @Override
  public int getMax() {
    return ConfigHandler.getInt("items.weapons.shooters.magazines.MediumMagazine.MAX");
  }

  @Override
  public String getName() {
    return NAME;
  }
}