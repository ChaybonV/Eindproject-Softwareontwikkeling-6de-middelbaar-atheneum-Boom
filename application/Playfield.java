package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import application.math.Coordinate;
import application.math.Number;
import application.math.Vector;
import application.objects.Drawable;
import application.config.ConfigHandler;
import application.items.Item;
import application.items.ItemFactory;
import application.javafx.Draw;

public class Playfield implements Drawable {
  private final int width;
  private final int height;

  public Playfield(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void stayInBoundaries(Coordinate pos) {
    if (pos.getX() < 0)
      pos.add(new Vector(pos, new Coordinate(0, pos.getY())));
    else if (pos.getX() > getWidth())
      pos.add(new Vector(pos, new Coordinate(getWidth(), pos.getY())));

    if (pos.getY() < 0)
      pos.add(new Vector(pos, new Coordinate(pos.getX(), 0)));
    else if (pos.getY() > getHeight())
      pos.add(new Vector(pos, new Coordinate(pos.getX(), getHeight())));
  }

  public boolean isBetweenBorders(Coordinate pos) {
    return pos.getX() > 0 && pos.getX() < getWidth() && pos.getY() > 0 && pos.getY() < getHeight();
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  @Override
  public void draw(GraphicsContext gc) {
    gc.setStroke(new Color(0, 0, 0, .1));
    gc.setLineWidth(1);

    final Viewbox viewbox = Game.getInstance().getViewBox();
    for (int i = 0; i <= getWidth(); i += 40) {
      if (viewbox.getPos().getX() < i) {
        Coordinate top = new Coordinate(i, 0);
        Coordinate bottom = new Coordinate(i, getHeight());
        Draw.line(gc, top, bottom);
      }
    }
    for (int i = 0; i <= getHeight(); i += 40) {
      if (viewbox.getPos().getY() < i) {
        Coordinate left = new Coordinate(0, i);
        Coordinate right = new Coordinate(getWidth(), i);
        Draw.line(gc, left, right);
      }
    }
  }

  public void generateLoot() {
    final int SPAWN_DEVIATION_PX = ConfigHandler.getInt("PlayField.SPAWN_DEVIATION_PX");

    final int amountOfLootPlaces = (int)(getWidth() * getHeight() / Math.pow(40, 2) / ConfigHandler.getInt("PlayField.SPAWN_FREQUENCY"));
    for (int i = 0; i < amountOfLootPlaces; i++) {
      final Coordinate c = new Coordinate(Number.getRandom(0, getWidth()), Number.getRandom(0, getHeight()));

      final int random = Number.getRandom(2, 6);
      for (int j = 0; j < random; j++) {
        final Item item = ItemFactory.generateRandom(
          Number.getRandom(2, 10),
          Number.getRandom(30, 100) / random,
          Number.getRandom(20, 50) / random * 2);

        item.setPos(new Coordinate(
          Number.getRandom((int) c.getX() - SPAWN_DEVIATION_PX, (int) c.getX() + SPAWN_DEVIATION_PX),
          Number.getRandom((int) c.getY() - SPAWN_DEVIATION_PX, (int) c.getY() + SPAWN_DEVIATION_PX)
        ));

        stayInBoundaries(item.getPos());
        Game.getInstance().getItems().add(item);
      }
    }
  }
}