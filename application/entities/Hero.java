package application.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import application.math.Angle;
import application.math.Coordinate;
import application.math.Number;
import application.math.Vector;
import application.Game;
import application.Viewbox;
import application.config.ConfigHandler;
import application.items.Backpack;
import application.items.Item;
import application.items.weapons.Weapon;
import application.javafx.Controller;
import application.javafx.Draw;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Hero extends Entity {
  private final Boost boost = new Boost();

  public Hero(int maxHp, Coordinate pos, double mass) {
    super(maxHp, pos, mass);
  }

  @Override
  public void draw(GraphicsContext gc) {

    gc.setLineWidth(1);
    gc.setStroke(new Color(0, 0, 0, 0.1));
    Draw.strokeCircle(gc, getPos(), ConfigHandler.getDouble("entities.Player.MAX_SURROUNDING_RADIUS") * 2);

    gc.setLineWidth(2);
    gc.setStroke(Color.BLACK);
    gc.setFill(Color.YELLOW);
    Draw.fillTriangle(gc, getPos(), getAngle(), getRadiusOfTriangle());

    gc.setLineWidth(8);
    final Vector vector = new Vector(Draw.relativeCoordinate(getPos()), Controller.getInstance().getCursor());
    vector.setMag(25);

    final Coordinate destination = new Coordinate(getPos());
    destination.add(vector);

    Draw.line(gc, getPos(), destination, false, false);

    gc.setLineWidth(2);
    gc.setStroke(new Color(0, 0, 0, 0.2));
    gc.setLineDashes(10);
    Draw.line(gc, getPos(), Controller.getInstance().getCursor(), false, true);
    gc.setLineDashes(0);
  }

  public double getRadiusOfTriangle() {
    return getMass() / 2D;
  }

  @Override
  public void update() {
    final List < KeyCode > keys = Controller.getInstance().getKeys();
    boolean boostKeyPressed = false;
    Vector vector = new Vector();
    for (KeyCode key: keys) {
      switch (key) {
      case Z, W -> {
        vector.add(new Vector(0, ConfigHandler.getDouble("entities.Entity.MOVEMENT_SPEED")));
        vector.setAngle(getAngle());
      }
      case S -> {
        vector.add(new Vector(0, ConfigHandler.getDouble("entities.Entity.MOVEMENT_SPEED")));
        vector.setAngle(new Angle(getAngle().getRadians() + Math.PI));
      }
      case A, Q -> getAngle().setDegrees(getAngle().getDegrees() - ConfigHandler.getDouble("entities.Entity.ROTATION_SPEED") * Controller.getInstance().getGameLoop().getFrameTimeSeconds());
      case D -> getAngle().setDegrees(getAngle().getDegrees() + ConfigHandler.getDouble("entities.Entity.ROTATION_SPEED") * Controller.getInstance().getGameLoop().getFrameTimeSeconds());
      case SPACE -> useAttack();
      case SHIFT -> boostKeyPressed = true;
      }
    }

    if (!vector.isZero())
      vector.setMag(ConfigHandler.getDouble("entities.Entity.MOVEMENT_SPEED"));

    if (boostKeyPressed) {
      try {
        getBoost().use(vector);
      } catch (Boost.BoostTooLowException e) {
        Controller.getInstance().showError(e.getMessage());
        getBoost().fill();
      }
    } else getBoost().fill();

    applyForce(vector);

    final Viewbox viewbox = Game.getInstance().getViewBox();
    viewbox.getPos().setX(getPos().getX() - viewbox.getWidth() / 2D);
    viewbox.getPos().setY(getPos().getY() - viewbox.getHeight() / 2D);

    super.update();
  }

  @Override
  public void die() {
    System.out.println("GAME OVER");
  }

  @Override
  public void useAttack() {
    if (getHand() instanceof Weapon) {
      final Weapon weapon = (Weapon) getHand();
      weapon.attack(Game.getInstance().getViewBox().relativeViewboxCoordinateToAbsolute(Controller.getInstance().getCursor()));
    }
  }

  @Override
  public double getbulletStartLocationRadius() {
    return getRadiusOfTriangle() + 10;
  }

  public void changeBackpack(Backpack backpack) {
    if (getBackpack() == backpack) return;

    final Backpack prevBackpack = (Backpack) setBackPack(backpack);

    final int size = prevBackpack.getItems().size();
    for (int i = 0; i < size; i++) {
      final Item item = prevBackpack.removeItem(0);
      if (item == backpack) continue;

      try {
        backpack.addItemToBackpack(item);
      } catch (Backpack.MaxMassExceeded ignored) {
        item.drop();
      }
    }

    try {
      backpack.addItemToBackpack(prevBackpack);
    } catch (Backpack.MaxMassExceeded ignored) {
      prevBackpack.drop();
    }
  }

  public void initializeCapacity() {
    if (super.getActualStrength() == 0) {
      super.setCapacity(0);
    }
    if (super.getActualStrength() >= 1.00 && super.getActualStrength() <= 10.00) {
      super.setCapacity(super.getActualStrength() * 10);
    }
    if (super.getActualStrength() >= 11.00 && super.getActualStrength() <= 20.00) {
      double listOfCapacities[] = {115,130,150,175,200,230,260,300,350,400};
      super.setCapacity(listOfCapacities[(int) super.getActualStrength() - 11]);
    }
    if (super.getActualStrength() >= 21) {
      super.setCapacity(4 * returnCapacity(super.getActualStrength() - 10));
    }
  }

  public double returnCapacity(double amount) {
    if (amount == 0) {
      return 0;
    }
    if (amount >= 1.00 && amount <= 10.00) {
      return amount * 10;
    }
    if (amount >= 11.00 && amount <= 20.00) {
      double listOfCapacities[] = {115,130,150,175,200,230,260,300,350,400};
      return listOfCapacities[(int) amount - 11];
    }
    return 4 * returnCapacity(amount - 10);
  }

  public List < Item > getAllSurroundingItems() {
    return Game.getInstance().getItems().stream().filter(item ->
      item.getPos() != null &&
      getPos().getX() - ConfigHandler.getDouble("entities.Player.MAX_SURROUNDING_RADIUS") < getPos().getX() &&
      getPos().getX() + ConfigHandler.getDouble("entities.Player.MAX_SURROUNDING_RADIUS") > getPos().getX() &&
      getPos().getY() - ConfigHandler.getDouble("entities.Player.MAX_SURROUNDING_RADIUS") < getPos().getY() &&
      getPos().getY() + ConfigHandler.getDouble("entities.Player.MAX_SURROUNDING_RADIUS") > getPos().getY() &&
      item.getPos().getDistanceBetween(getPos()) < ConfigHandler.getDouble("entities.Player.MAX_SURROUNDING_RADIUS")).collect(Collectors.toCollection(LinkedList::new));
  }

  public Boost getBoost() {
    return boost;
  }

  public static class Boost {
    public static final double DEFLATE_PER_SECOND = .2;
    public static final double FILLING_PER_SECOND = .1;
    public static double MOVEMENT_SPEED_BOOST_AMPLIFIER = 1.5;
    private double percentage;

    public Boost() {
      this(1);
    }

    public Boost(double percentage) {
      this.percentage = percentage;
    }

    public void use(Vector speed) throws BoostTooLowException {
      final double toBeDeflated = DEFLATE_PER_SECOND * Controller.getInstance().getGameLoop().getFrameTimeSeconds();

      if (getPercentage() - toBeDeflated < 0)
        throw new BoostTooLowException();

      speed.mult(MOVEMENT_SPEED_BOOST_AMPLIFIER);
      setPercentage(getPercentage() - toBeDeflated);
    }

    public void fill() {
      setPercentage(getPercentage() + FILLING_PER_SECOND * Controller.getInstance().getGameLoop().getFrameTimeSeconds());
    }

    public double getPercentage() {
      return percentage;
    }

    private void setPercentage(double percentage) {
      this.percentage = Number.constrain(percentage, 0, 1);
    }

    public static class BoostTooLowException extends RuntimeException {
      public BoostTooLowException() {
        super("Boost Too Low");
      }
    }
  }
}