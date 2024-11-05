package application.items;

import application.Factory;
import application.config.ConfigHandler;

public enum ItemType implements Factory.WeightHolder {
	ARMOR("items.ItemType.ARMOR"),
    BACKPACK("items.ItemType.BACKPACK"),
    WEAPON("items.ItemType.WEAPON"),
    MAGAZINE("items.ItemType.MAGAZINE");

  public final String weightName;

  ItemType(String weightName) {
    this.weightName = weightName;
  }

  @Override
  public int getWeight() {
    return ConfigHandler.getInt(weightName);
  }
}