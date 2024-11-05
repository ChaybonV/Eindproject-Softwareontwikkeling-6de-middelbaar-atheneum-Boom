package application.entities;

import application.math.Coordinate;
import application.math.Number;
import application.Game;
import application.Playfield;
import application.config.ConfigHandler;

public class MonsterFactory {
  public static Monster generateRandom() {
    final int hp = randomHp();
    return new Monster(hp, MonsterFactory.generateRandomCoordinateOnPlayfield(Game.getInstance().getPlayField()), MonsterFactory.hpToMass(hp));
  }

  public static int randomHp() {
    return Number.getRandom(ConfigHandler.getInt("entities.Bot.MIN_HP"), ConfigHandler.getInt("entities.Bot.MAX_HP"));
  }

  public static double hpToMass(int maxHp) {
    return maxHp * 1.2;
  }

  public static Coordinate generateRandomCoordinateOnPlayfield(Playfield playfield) {
    return new Coordinate(Number.getRandom(0, playfield.getWidth()), Number.getRandom(0, playfield.getHeight()));
  }
}