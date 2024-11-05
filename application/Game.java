package application;

import javafx.scene.canvas.GraphicsContext;
import application.math.Coordinate;
import application.math.Vector;
import application.objects.Drawable;
import application.objects.Updatable;
import application.config.ConfigHandler;
import application.entities.MonsterFactory;
import application.entities.Entity;
import application.entities.Hero;
import application.items.Item;
import application.items.weapons.shooters.Bullet;
import application.javafx.Controller;
import application.javafx.Draw;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Game implements Drawable, Updatable {
  private static Game game = new Game();
  public final List < Entity > entities;
  private final List < Item > items;
  private final Viewbox viewBox;
  private final List < Bullet > bullets;
  private Playfield playField;
  private Hero player;
  private boolean pause;

  private Game() {
    this.items = new LinkedList < > ();
    this.playField = new Playfield(ConfigHandler.getInt("Game.PLAYFIELD_WIDTH"), ConfigHandler.getInt("Game.PLAYFIELD_HEIGHT"));
    final Coordinate middle = new Coordinate(getPlayField().getWidth() / 2D, getPlayField().getHeight() / 2D);
    this.player = new Hero(ConfigHandler.getInt("Game.PLAYER_MAX_HP"), middle, ConfigHandler.getInt("Game.PLAYER_MASS"));
    this.viewBox = new Viewbox(new Coordinate(middle.getX(), middle.getY()));
    this.bullets = new LinkedList < > ();
    this.entities = new LinkedList < > ();
    this.pause = false;
  }

  public static void startNewGame() {
    Game.game = new Game();
  }

  public static Game getInstance() {
    return game;
  }

  public static Vector getFriction(Entity entity) {
    final Vector vector = entity.getVel().clone();
    vector.invert();
    vector.div(Controller.getInstance().getGameLoop().getFrameTimeSeconds());
    vector.mult(ConfigHandler.getDouble("Game.DEFAULT_FRICTION_MU"));
    return vector;
  }

  public void initialize() {
    this.playField.generateLoot();
  }

  @Override
  public void draw(GraphicsContext gc) {
    Draw.clear(gc);
    getPlayField().draw(gc);
    getItems().forEach(item -> {
      if (getViewBox().isVisible(item)) {
        item.draw(gc);
      }
    });
    getEntities().forEach(entity -> {
      if (getViewBox().isVisible(entity))
        entity.draw(gc);
    });
    getBullets().forEach(bullet -> {
      if (getViewBox().isVisible(bullet)) {
        bullet.draw(gc);
      }
    });
    getPlayer().draw(gc);
  }

  @Override
  public void update() {
    if (getPlayer().isDead())
      gameOver();
    spawnBotsIfNeeded();

    getPlayer().update();
    final Iterator < Entity > entityIterator = getEntities().iterator();

    while (entityIterator.hasNext()) {
      final Entity entity = entityIterator.next();
      if (entity.isDead()) {
        entity.die();
        entityIterator.remove();
        continue;
      }
      entity.update();
    }

    getBullets().removeIf(Bullet::isDelete);
    getBullets().forEach(Bullet::update);
  }

  private void gameOver() {
    Controller.getInstance().showError("GAME OVER");
    Controller.getInstance().pauseGame(true);
  }

  private void spawnBotsIfNeeded() {
    for (int i = getEntities().size(); i < ConfigHandler.getInt("Game.AMOUNT_OF_BOTS"); i++) {
      entities.add(MonsterFactory.generateRandom());
    }
  }

  public List < Item > getItems() {
    return items;
  }

  public Playfield getPlayField() {
    return playField;
  }

  public void setPlayField(Playfield playField) {
    this.playField = playField;
  }

  public Hero getPlayer() {
    return player;
  }

  public void setPlayer(Hero player) {
    this.player = player;
  }

  public Viewbox getViewBox() {
    return viewBox;
  }

  public List < Bullet > getBullets() {
    return bullets;
  }

  public List < Entity > getEntities() {
    return entities;
  }

  public boolean isPause() {
    return pause;
  }

  public void setPause(boolean pause) {
    this.pause = pause;
  }
}