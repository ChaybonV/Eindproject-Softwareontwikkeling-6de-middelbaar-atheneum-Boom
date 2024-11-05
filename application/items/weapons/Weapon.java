package application.items.weapons;

import application.math.Coordinate;
import application.items.Item;

public abstract class Weapon extends Item {
  public Weapon(double weight) {
    super(weight);
  }

  public abstract String getName();

  public abstract void attack(Coordinate to);

  @Override
  public String getExtra() {
    return null;
  }
}