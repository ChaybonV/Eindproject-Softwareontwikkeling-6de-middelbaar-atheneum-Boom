package application.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import application.math.Coordinate;
import application.math.Number;
import application.math.Vector;
import application.Game;
import application.config.ConfigHandler;
import application.items.*;
import application.items.weapons.Weapon;
import application.items.weapons.shooters.magazines.HeavyMagazine;
import application.items.weapons.shooters.magazines.LightMagazine;
import application.items.weapons.shooters.magazines.Magazine;
import application.items.weapons.shooters.magazines.MediumMagazine;
import application.javafx.Draw;

import java.util.List;

public class Monster extends Entity {
  private Coordinate goTo;
  private Entity currentEnemy;

  public Monster(int maxHp, Coordinate pos, double mass) {
    super(maxHp, pos, mass);

    final double hpPercentageOfMax = (double) getHp() / ConfigHandler.getInt("entities.Bot.MAX_HP");

    int randomAmountOfItems;
    int maxArmorProtection;
    int maxBackpackWeight;
    if (hpPercentageOfMax > .70) {
      setHand(WeaponFactory.AssaultRifleFactory.generateRandom());

      maxBackpackWeight = Number.getRandom(20, 70);
      setBackPack(new Backpack(maxBackpackWeight, Number.getRandom(2, 10)));

      randomAmountOfItems = Number.getRandom(4, 12);
      maxArmorProtection = ConfigHandler.getInt("items.Armor.PROTECTION_MAX");
    } else if (hpPercentageOfMax > .33) {
      setHand(WeaponFactory.SubmachineGunFactory.generateRandom());

      maxBackpackWeight = Number.getRandom(10, 50);
      setBackPack(new Backpack(maxBackpackWeight, Number.getRandom(2, 10)));

      randomAmountOfItems = Number.getRandom(2, 6);
      maxArmorProtection = (int)(ConfigHandler.getInt("items.Armor.PROTECTION_MAX") * .6);
    } else {
      setHand(WeaponFactory.PistolFactory.generateRandom());

      maxBackpackWeight = Number.getRandom(5, 30);
      setBackPack(new Backpack(maxBackpackWeight, Number.getRandom(2, 10)));

      randomAmountOfItems = Number.getRandom(0, 4);
      maxArmorProtection = 20;
    }
    final Backpack backpack = getBackpack();
    for (int i = 0; i < randomAmountOfItems; i++) {
      try {
        backpack.addItemToBackpack(ItemFactory.generateRandom(Number.getRandom(2, 10), Number.getRandom(ConfigHandler.getInt("entities.Bot.ARMOR_SPAWN_MIN"), maxArmorProtection), maxBackpackWeight));
      } catch (Backpack.MaxMassExceeded ignore) {}
    }
  }

  public static Coordinate generateRandomCoordinateAround(Coordinate pos) {
    final double devi = ConfigHandler.getInt("entities.Bot.GO_TO_DEVIATION") * 1.4;
    final double xMax = Game.getInstance().getPlayField().getWidth() - devi;
    final double yMax = Game.getInstance().getPlayField().getHeight() - devi;

    final Coordinate coordinate = new Coordinate(
      Number.getRandom(
        (int) Number.constrain(pos.getX() - ConfigHandler.getInt("entities.Bot.RANDOM_LOCATION_MAX"), devi, xMax),
        (int) Number.constrain(pos.getX() + ConfigHandler.getInt("entities.Bot.RANDOM_LOCATION_MAX"), devi, xMax)),
      Number.getRandom(
        (int) Number.constrain(pos.getY() - ConfigHandler.getInt("entities.Bot.RANDOM_LOCATION_MAX"), devi, yMax),
        (int) Number.constrain(pos.getY() + ConfigHandler.getInt("entities.Bot.RANDOM_LOCATION_MAX"), devi, yMax))
    );

    if (coordinate.getDistanceBetween(pos) < ConfigHandler.getInt("entities.Bot.GO_TO_DEVIATION"))
      return generateRandomCoordinateAround(pos);

    return coordinate;
  }

  public static Coordinate intersectionOfLines(Coordinate p1, Coordinate p2, Coordinate p3, Coordinate p4) {
    final double a1 = (p1.getY() - p2.getY()) / (p1.getX() - p2.getX());
    final double a2 = (p3.getY() - p4.getY()) / (p3.getX() - p4.getX());

    //parallel
    if (a1 == a2) return null;

    final double b1 = p1.getY() - a1 * p1.getX();
    final double b2 = p3.getY() - a2 * p3.getX();

    if (Double.isInfinite(a1)) return new Coordinate(p1.getX(), a2 * p1.getX() + b2);
    if (Double.isInfinite(a2)) return new Coordinate(p3.getX(), a1 * p3.getX() + b1);

    final double x = (b2 - b1) / (a1 - a2);
    final double y = a1 * x + b1;

    return new Coordinate(x, y);
  }

  @Override
  public void draw(GraphicsContext gc) {
    gc.setFill(Color.SANDYBROWN);
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);

    final double d = getDiameter();
    Draw.fillCircle(gc, getPos(), d);

    Draw.hpBar(gc, new Coordinate(getPos().getX(), getPos().getY() - d / 2D - ConfigHandler.getInt("entities.Bot.HP_BAR_HEIGHT")), getHpPercentage(), d, ConfigHandler.getInt("entities.Bot.HP_BAR_HEIGHT"));

  }

  public double getDiameter() {
    return Number.constrain(getMass() / 2D, ConfigHandler.getInt("entities.Bot.DRAWING_WIDTH_MIN"), ConfigHandler.getInt("entities.Bot.DRAWING_WIDTH_MAX"));
  }

  public double getRadius() {
    return getDiameter() / 2D;
  }

  @Override
  public void update() {
    setCurrentEnemy(getClosestEnemy());
    if (getGoTo() == null || getGoTo().getDistanceBetween(getPos()) <= ConfigHandler.getInt("entities.Bot.GO_TO_DEVIATION"))
      setGoTo(Monster.generateRandomCoordinateAround(getPos()));

    useAttack();

    final Vector goToVector = new Vector(getPos(), getGoTo());
    goToVector.setMag(ConfigHandler.getDouble("entities.Bot.MOVEMENT_SPEED"));

    applyForce(goToVector);

    super.update();
  }

  @Override
  public void die() {
    getBackpack().drop(getPos());
    getBackpack().drop();
    getHand().drop();
  }

  @Override
  public Magazine getNewMagazine(MagazineChecker magazineChecker) {
    Magazine[] magazines = new Magazine[] {
      new LightMagazine(), new MediumMagazine(), new HeavyMagazine()
    };
    for (Magazine magazine: magazines) {
      if (magazineChecker.canHaveMagazine(magazine))
        return magazine;
    }
    throw new InternalError("The bot is not able to make a new Magazine that meets the given requirements");
  }

  @Override
  public void useAttack() {
    final Entity entity = getCurrentEnemy();
    if (entity == null) return;

    final int ATTACK_GO_TO_SQUARE_WIDTH = ConfigHandler.getInt("entities.Bot.ATTACK_GO_TO_SQUARE_WIDTH");
    final Coordinate leftTop = new Coordinate(entity.getPos());
    leftTop.add(new Vector(-ATTACK_GO_TO_SQUARE_WIDTH, -ATTACK_GO_TO_SQUARE_WIDTH));

    final Coordinate leftBottom = new Coordinate(entity.getPos());
    leftBottom.add(new Vector(-ATTACK_GO_TO_SQUARE_WIDTH, +ATTACK_GO_TO_SQUARE_WIDTH));

    final Coordinate rightTop = new Coordinate(entity.getPos());
    rightTop.add(new Vector(+ATTACK_GO_TO_SQUARE_WIDTH, -ATTACK_GO_TO_SQUARE_WIDTH));

    final Coordinate rightBottom = new Coordinate(entity.getPos());
    rightBottom.add(new Vector(+ATTACK_GO_TO_SQUARE_WIDTH, +ATTACK_GO_TO_SQUARE_WIDTH));

    final Coordinate[] intersections = new Coordinate[] {
    	intersectionOfLines(entity.getPos(), getPos(), leftBottom, leftTop),
        intersectionOfLines(entity.getPos(), getPos(), leftTop, rightTop),
        intersectionOfLines(entity.getPos(), getPos(), rightTop, rightBottom),
        intersectionOfLines(entity.getPos(), getPos(), rightBottom, leftBottom),
    };

    Coordinate bestCoordinateEnemy = null;
    for (Coordinate next: intersections) {
      if (bestCoordinateEnemy == null) {
        bestCoordinateEnemy = next;
        continue;
      }
      if (next == null) continue;
      if (bestCoordinateEnemy.getDistanceBetween(getPos()) > next.getDistanceBetween(getPos())) {
        bestCoordinateEnemy = next;
      }

    }
    setGoTo(bestCoordinateEnemy);

    shootTo(entity.getPos());
  }

  //  private void handlingDamage(Entity entity, int damage) {
  //	    if (entity instanceof Hero) {
  //	      if (entity.getActualProtection() - damage > 10) {
  //	        entity.getAnchors().get(Anchor.BODY).setActualProtection((entity.getActualProtection() - 10) - damage);
  //	        this.setInCombat(false);
  //	        System.out.println("Remaining HP: " + entity.getActualHitpoints());
  //	      } else if (entity.getActualProtection() - damage == 10 || (entity.getActualProtection() - damage < 10 && entity.getActualProtection() - damage > 0)) {
  //	        ((Hero) entity).dropItem(Anchor.BODY, entity.getAnchors().get(Anchor.BODY));
  //	        this.setInCombat(false);
  //	        System.out.println("Remaining HP: " + entity.getActualHitpoints());
  //	      } else {
  //	        int damageAfter = damage - entity.getActualProtection();
  //	        if (entity.getActualHitpoints() - damageAfter>= 1) {
  //	          entity.setActualHitpoints(entity.getActualHitpoints() - damageAfter);
  //	          this.setInCombat(false);
  //	          System.out.println("Remaining HP: " + entity.getActualHitpoints());
  //	        } else {
  //	          deathblow(entity);
  //	        }
  //	      }
  //	    } 
  //	    else {
  //	      if (entity.getActualProtection() - damage >= 1) {
  //	        entity.setActualProtection(entity.getActualProtection() - damage);
  //	        this.setInCombat(false);
  //	        System.out.println("Remaining HP: " + entity.getActualHitpoints());
  //	      }
  //	      else {
  //	        int damageAfter = damage - entity.getActualProtection();
  //	        if (entity.getActualHitpoints() - damageAfter >= 1) {
  //	          entity.setActualHitpoints(entity.getActualHitpoints() - damageAfter);
  //	          this.setInCombat(false);
  //	          System.out.println("Remaining HP: " + entity.getActualHitpoints());
  //	        } 
  //	        else {
  //	          deathblow(entity);
  //	        }
  //	      }
  //	    }
  //	  }

  //  public void hit(Entity entity) {
  //	    int randomNumber = (int)(Math.random()*101);
  //	    int actualHitpoints = this.getActualHitpoints();
  //	    this.setInCombat(true);
  //
  //	    if (entity.getActualHitpoints() > 0) {
  //	      if (calculateHit(actualHitpoints, randomNumber)) {
  //	        if (randomNumber >= entity.getActualProtection()) {
  //	          System.out.println("Hit.");
  //	          handlingDamage(entity, calculateDamage());
  //	        } else {
  //	          System.out.println("Miss.");
  //	        }
  //	      } else {
  //	        if (actualHitpoints >= entity.getActualProtection()) {
  //	          System.out.println("Hit.");
  //	          handlingDamage(entity, calculateDamage());
  //	        } else {
  //	          System.out.println("Miss.");
  //	        }
  //	      }
  //	    } else {
  //	      System.out.println("Cannot hit an opponent whose HP equals 0.");
  //	    }
  //	  }

  //  public void collectTreasure(Entity entity) {
  //	    Anchor[] anchorValues = Anchor.values();
  //	    boolean scene = false;
  //
  //	    int amountOfOpponentAnchors = 0;
  //	    if (entity instanceof Hero) {
  //	      Hero hero = (Hero) entity;
  //	      for (int i = 0; i < anchorValues.length; i++) {
  //	        if (hero.getAnchors().containsKey(anchorValues[i])) {
  //	          amountOfOpponentAnchors++;
  //	        }
  //	      }
  //	    } else if (entity instanceof Monster) {
  //	      Monster opponentMonster = (Monster) entity;
  //	      amountOfOpponentAnchors = opponentMonster.getMaxAnchors();
  //	    }
  //
  //	    int amountOfMonsterAnchors = this.getMaxAnchors();
  //	    long totalWeight = getTotalWeight();
  //	    if (Math.random() < 0.3) {
  //	      System.out.println("Monster took one extra item from the opponent.");
  //	      for (int i = 0; i < anchorValues.length; i++) {
  //	        for (int z = 0; z < anchorValues.length; z++) {
  //	          if (!this.getAnchors().containsKey(anchorValues[z]) && entity.getAnchors().containsKey(anchorValues[i])) {
  //	            double addWeight = entity.getAnchors().get(anchorValues[i]).getWeight();
  //	            if (totalWeight + addWeight <= this.getCapacity()) {
  //	              if (this.getAnchors().size() < amountOfMonsterAnchors) {
  //	                this.getAnchors().put(anchorValues[z], entity.getAnchors().get(anchorValues[i]));
  //	                this.getAnchors().get(anchorValues[z]).setHolder(this);
  //	                entity.getAnchors().remove(anchorValues[i]);
  //	                scene = true;
  //	                return;
  //	              }
  //	            } else {
  //	              System.out.println("Cannot collect item, capacity exceeded.");
  //	              return;
  //	            }
  //	          }
  //	        }
  //	      }
  //	    }
  //	    if (Math.random() < 0.5) {
  //	      System.out.println("Monster traded some items with its own collection.");
  //	      int maxTrade = (int)(Math.random()*2)+1;
  //	      int tradeCounter = 0;
  //	      for (int i = 0; i < anchorValues.length; i++) {
  //	        for (int z = 0; z < anchorValues.length; z++) {
  //	          if (!this.getAnchors().containsKey(anchorValues[z]) && entity.getAnchors().containsKey(anchorValues[i])) {
  //	            double addWeight = entity.getAnchors().get(anchorValues[i]).getWeight();
  //	            if (totalWeight + addWeight <= this.getCapacity()) {
  //	              this.getAnchors().put(anchorValues[z], entity.getAnchors().get(anchorValues[i]));
  //	              this.getAnchors().get(anchorValues[z]).setHolder(this);
  //	              entity.getAnchors().remove(anchorValues[i]);
  //	              scene = true;
  //	              tradeCounter++;
  //	              if (maxTrade == tradeCounter) {
  //	                return;
  //	              }
  //	            } else {
  //	              System.out.println("Cannot collect item, capacity exceeded.");
  //	              return;
  //	            }
  //	          }
  //	        }
  //	      }
  //	    }
  //	    if (Math.random() < 0.2) {
  //	      System.out.println("Monster exchanged all weapons with the opponent.");
  //	      if (amountOfOpponentAnchors >= 1 && amountOfMonsterAnchors >= 1) {
  //	        for (int i = 0; i < anchorValues.length; i++) {
  //	          for (int z = 0; z < anchorValues.length; z++) {
  //	            if (this.getAnchors().containsKey(anchorValues[i]) && entity.getAnchors().containsKey(anchorValues[z])) {
  //	              double addWeight = entity.getAnchors().get(anchorValues[z]).getWeight();
  //	              if (totalWeight + addWeight <= this.getCapacity()) {
  //	                this.getAnchors().put(anchorValues[i], entity.getAnchors().get(anchorValues[z]));
  //	                this.getAnchors().get(anchorValues[i]).setHolder(this);
  //	                entity.getAnchors().remove(anchorValues[z]);
  //	                scene = true;
  //	              } else {
  //	                System.out.println("Cannot collect item, capacity exceeded.");
  //	                return;
  //	              }
  //	            }
  //	          }
  //	        }
  //	      }
  //	    }
  //	    if (scene == false) {
  //	      System.out.println("No treasure collected.");
  //	    }
  //	  }

  @Override
  public double getbulletStartLocationRadius() {
    return getRadius() + ConfigHandler.getInt("entities.Bot.BULLET_LOCATION_EXTRA_RADIUS");
  }

  public void shootTo(Coordinate coordinate) {
    if (coordinate == null) return;

    final Item hand = getHand();

    if (hand instanceof Weapon)((Weapon) hand).attack(coordinate);
  }

  public Entity getClosestEnemy() {
    final Hero player = Game.getInstance().getPlayer();
    final List < Entity > entities = Game.getInstance().getEntities();
    final Entity closestEntity = entities.stream().filter(entity -> entity != this && entity.getPos().getDistanceBetween(getPos()) <= ConfigHandler.getInt("entities.Bot.VISION_LENGTH")).min((o1, o2) -> (int) o1.getPos().getDistanceBetween(o2.getPos())).orElse(null);

    if (player.getPos().getDistanceBetween(getPos()) <= ConfigHandler.getInt("entities.Bot.VISION_LENGTH")) {
      return closestEntity == null || player.getPos().getDistanceBetween(getPos()) < closestEntity.getPos().getDistanceBetween(getPos()) ? player : closestEntity;
    }
    return closestEntity;
  }

  public Coordinate getGoTo() {
    return goTo;
  }

  public void setGoTo(Coordinate goTo) {
    this.goTo = goTo;
  }

  public Entity getCurrentEnemy() {
    return currentEnemy;
  }

  public void setCurrentEnemy(Entity currentEnemy) {
    this.currentEnemy = currentEnemy;
  }
}