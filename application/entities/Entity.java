package application.entities;

import application.math.Number;
import application.math.*;
import application.Anchor;
import application.Game;
import application.GameLoop;
import application.config.ConfigHandler;
import application.items.Armor;
import application.items.Backpack;
import application.items.Item;
import application.items.weapons.Weapon;
import application.items.weapons.shooters.Bullet;
import application.items.weapons.shooters.magazines.Magazine;
import application.javafx.Controller;

import java.util.EnumMap;

abstract public class Entity extends Mover {
  private String name;
  private final int maxHp;
  private final EnumMap < Anchor, Item > anchorPoints;
  private Angle angle;
  private int hp;
  private double actualStrength;
  private double capacity;

  public Entity(int maxHp, Coordinate pos, double mass) {
    super(pos, mass);
    if (maxHp <= 0) {
      throw new IllegalArgumentException("MaxHp can not be 0 or negative");
    }
    this.maxHp = maxHp;
    this.hp = this.maxHp;
    this.anchorPoints = new EnumMap < > (Anchor.class);
    final Backpack backpack = new Backpack(ConfigHandler.getDouble("entities.Entity.DEFAULT_BACKPACK_MASS_MAX"), ConfigHandler.getDouble("entities.Entity.DEFAULT_BACKPACK_MASS"));
    backpack.setHolder(this);
    this.anchorPoints.put(Anchor.BACK, backpack);
    this.angle = new Angle();
  }

  @Override
  public void update() {
    if (!(getAnchorPoints().get(Anchor.BACK) instanceof Backpack)) {
      final Backpack backpack = new Backpack(ConfigHandler.getDouble("entities.Entity.DEFAULT_BACKPACK_MASS_MAX"), ConfigHandler.getDouble("entities.Entity.DEFAULT_BACKPACK_MASS"));
      getAnchorPoints().put(Anchor.BACK, backpack);
    }

    Game.getInstance().getPlayField().stayInBoundaries(getPos());

    applyForce(Game.getFriction(this));

    super.update();
  }

  @Override
  public void applyForce(Vector force) {
    final GameLoop gameLoop = Controller.getInstance().getGameLoop();
    //        System.out.println(gameLoop.getFrameTime() + " / " + gameLoop.getFrameTimeMultiplier());
    force.mult(gameLoop.getFrameTimeSeconds());
    super.applyForce(force);
  }

  @Override
  public double getMass() {
    double mass = super.getMass();
    mass += getAnchorPoints().values().stream().mapToDouble(Item::getMass).sum();
    return mass;
  }

  public double getActualStrength() {
    return actualStrength;
  }
  public void setActualStrength(double actualStrength) {
    this.actualStrength = actualStrength;
  }

  public double getCapacity() {
    return capacity;
  }
  public void setCapacity(double capacity) {
    this.capacity = capacity;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    String regex = "^[A-Z][a-zA-Z\\s]*[a-zA-Z\\s]*[']?[a-zA-Z\\s]*[']?[a-zA-Z\\s]*$";
    int countCappingSigns = name.length() - name.replace("'", "").length();

    if (name.matches(regex)) {
      this.name = name;
    }
    if (name.isEmpty()) {
      throw new NullPointerException("There is no name");
    }
    if (!name.substring(0, 1).toUpperCase().equals(name.substring(0, 1))) {
      throw new IllegalArgumentException("The name is not starting with a capital letter.");
    }
    if (name.matches(".*\\d.*")) {
      throw new IllegalArgumentException("The name must not contain numbers");
    }
    if (countCappingSigns > 2) {
      throw new IllegalArgumentException("The name may contain up to two capping signs");
    }
  }

  public void hit(Bullet bullet) {
    bullet.stop();
    int damage = bullet.getDamage();
    final Item bodyItem = getAnchorPoints().get(Anchor.BODY);
    if (bodyItem instanceof Armor) {
      Armor armor = (Armor) bodyItem;
      damage = armor.reduceProtection(damage);
      if (damage > 0) {
        armor.setHolder(null);
        this.getAnchorPoints().remove(Anchor.BODY);
      }
    }
    reduceHp(damage);
  }

  //    public void hit(Entity entity) {
  //        int chance = (int)(Math.random() * 100);
  //        if (chance >= this.getActualProtection()) {
  //          super.setInCombat(true);
  //          int damage = 0;
  //          int strength = (int) this.getActualStrength();
  //          if (anchors.get(Anchor.LEFTHAND) instanceof Weapon && anchors.get(Anchor.RIGHTHAND) instanceof Weapon)
  //            strength += (anchors.get(Anchor.LEFTHAND).getDamage() + anchors.get(Anchor.RIGHTHAND).getDamage());
  //          else if (anchors.get(Anchor.LEFTHAND) instanceof Weapon)
  //            strength += anchors.get(Anchor.LEFTHAND).getDamage();
  //          else if (anchors.get(Anchor.RIGHTHAND) instanceof Weapon)
  //            strength += anchors.get(Anchor.RIGHTHAND).getDamage();
  //          	damage += (Math.round(strength - 10) / 2);
  //          if (entity instanceof Hero) {
  //            Hero hero2 = (Hero) entity;
  //            int armorProtection = 0;
  //            int hero2Protection = hero2.getActualProtection();
  //            int hitpoints = hero2.getActualHitpoints();
  //            if (hero2.anchors.containsKey(Anchor.BODY) && hero2.anchors.get(Anchor.BODY) instanceof Armor) {
  //              armorProtection = hero2.anchors.get(Anchor.BODY).getActualProtection();
  //              int armorDamage = damage - armorProtection;
  //              if (armorDamage > 1) {
  //                if (armorDamage - hero2Protection > 1) {
  //                  int actualDamage = armorDamage - hero2Protection;
  //                  if (hitpoints - actualDamage > 1) {
  //                    hero2.setActualHitpoints(hero2.getActualHitpoints() - actualDamage);
  //                    hero2.anchors.remove(Anchor.BODY);
  //                    super.setInCombat(false);
  //                  } else {
  //                    this.deathblow(entity);
  //                  }
  //                } else {
  //                  hero2.anchors.remove(Anchor.BODY);
  //                  super.setInCombat(false);
  //                }
  //              } else {
  //                hero2.anchors.get(Anchor.BODY).setActualProtection(armorProtection - damage);
  //                super.setInCombat(false);
  //              }
  //            } else {
  //              if (damage - hero2Protection > 1) {
  //                int actualDamage = damage - hero2Protection;
  //                if (actualDamage - hitpoints > 1) {
  //                  hero2.setActualHitpoints(hero2.getActualHitpoints() - actualDamage);
  //                  super.setInCombat(false);
  //                } else {
  //                  this.deathblow(entity);
  //                }
  //              }
  //            }
  //          }
  //        }
  //      }

  //    public void collectTreasure(Entity entity) {
  //        Anchor[] anchors = Anchor.values();
  //        Monster monster = (Monster) entity;
  //        int amountOfMonsterAnchors = monster.getMaxAnchors();
  //        int amountOfHeroAnchors = 0;
  //        for (int i = 0; i < anchors.length; i++) {
  //          if (this.anchors.containsKey(anchors[i])) {
  //            amountOfHeroAnchors++;
  //          }
  //        }
  //        if (amountOfHeroAnchors != 4 && amountOfMonsterAnchors > 0) {
  //          for (int i = 0; i < anchors.length; i++) {
  //            for (int z = 0; z < anchors.length; z++) {
  //              if (!this.anchors.containsKey(anchors[z]) && monster.getAnchors().containsKey(anchors[i])) {
  //                double newTotalWeight = getTotalWeightAnchors() + monster.getAnchors().get(anchors[i]).getWeight();
  //                if (newTotalWeight <= super.getCapacity()) {
  //                  this.anchors.put(anchors[z], monster.getAnchors().get(anchors[i]));
  //                  this.anchors.get(anchors[z]).setHolder(this);
  //                  entity.anchors.remove(anchors[i]);
  //                } else {
  //                  System.out.println("No treasure collected. Weight limit exceeded.");
  //                }
  //              }
  //            }
  //          }
  //        } else {
  //          System.out.println("No treasure collected.");
  //        }
  //      }
  
  public abstract void die();

  public void addItemToBackpack(Item item) throws NoBackpack {
    final Item backpack = getAnchorPoints().get(Anchor.BACK);

    if (!(backpack instanceof Backpack))
      throw new NoBackpack();

    ((Backpack) backpack).addItemToBackpack(item);
  }

  public Magazine getNewMagazine(MagazineChecker magazineChecker) {
    for (Item item: getBackpack().getItems()) {
      if (!(item instanceof Magazine)) continue;

      final Magazine magazine = (Magazine) item;

      if (magazineChecker.canHaveMagazine(magazine) && !magazine.isEmpty()) {
        getBackpack().removeItem(magazine);
        return magazine;
      }
    }
    return null;
  }

  public void reduceHp(int hp) {
    if (hp > 0)
      setHp(getHp() - hp);
  }

  public Item setBackPack(Backpack backpack) {
    backpack.setHolder(this);
    return getAnchorPoints().put(Anchor.BACK, backpack);
  }

  public Backpack getBackpack() {
    final Item item = getAnchorPoints().get(Anchor.BACK);
    return item instanceof Backpack ? (Backpack) item : null;
  }

  public Item getHand() {
    return getAnchorPoints().get(Anchor.HAND);
  }

  public Item setHand(Weapon weapon) {
    weapon.setHolder(this);
    return getAnchorPoints().put(Anchor.HAND, weapon);
  }

  public Armor getArmor() {
    final Item item = getAnchorPoints().get(Anchor.BODY);
    if (item instanceof Armor)
      return (Armor) item;
    return null;
  }

  public Item setArmor(Armor armor) {
    armor.setHolder(this);
    return getAnchorPoints().put(Anchor.BODY, armor);
  }

  public int getMaxHp() {
    return maxHp;
  }

  public int getHp() {
    return hp;
  }

  public void setHp(int hp) {
    this.hp = Number.constrain(hp, 0, getMaxHp());
  }

  public EnumMap < Anchor, Item > getAnchorPoints() {
    return anchorPoints;
  }

  public void heal(int hp) {
    if (hp > 0)
      this.setHp(getHp() + hp);
  }

  //    public void heal() {
  //        double differenceInHP = super.getMaxHitpoints() - super.getActualHitpoints();
  //        double random = (int)(Math.random() * 101);
  //        double percentageInHP = (differenceInHP / 100) * random;
  //        int totalAfterHeal = Math.round((int) percentageInHP + super.getActualHitpoints());
  //        super.setActualHitpoints(totalAfterHeal);
  //      }

  public boolean isDead() {
    return getHp() <= 0;
  }

  public double getHpPercentage() {
    return (double) getHp() / (double) getMaxHp();
  }

  public abstract void useAttack();

  public Angle getAngle() {
    return angle;
  }

  public void setAngle(Angle angle) {
    this.angle = angle;
  }

  public abstract double getbulletStartLocationRadius();

  public interface MagazineChecker {
    boolean canHaveMagazine(Magazine magazine);
  }

  public static class NoBackpack extends Exception {}
}