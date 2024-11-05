package application.items.weapons.shooters.magazines;

import com.sun.jdi.InternalException;

import static application.Factory.getRandomValueOf;

public class MagazineFactory {
  public static Magazine generateRandom() {
    switch (getRandomEnumValue()) {
    case HEAVY: return new HeavyMagazine();
    case LIGHT: return new LightMagazine();
    case MEDIUM: return new MediumMagazine();
    default: throw new InternalException();
   }
}

  public static MagazineType getRandomEnumValue() {
    return getRandomValueOf(MagazineType.values());
  }
}