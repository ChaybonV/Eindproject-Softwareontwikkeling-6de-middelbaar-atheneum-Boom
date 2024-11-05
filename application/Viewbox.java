package application;

import application.math.Coordinate;
import application.math.Mover;
import application.items.Item;
import application.javafx.Controller;

public class Viewbox {
  public final int DEVIATION = 100;
  private Coordinate pos;

  public Viewbox(Coordinate coordinate) {
    this.pos = coordinate;
  }

  public boolean isVisible(Mover mover) {
    return containsCoordinate(mover.getPos());
  }

  public boolean isVisible(Item item) {
    return item.canBeDrawn() && containsCoordinate(item.getPos());
  }

  public boolean containsCoordinate(Coordinate coordinate) {
    final double left = getPos().getX() - DEVIATION;
    final double right = getPos().getX() + getWidth() + DEVIATION;
    final double top = getPos().getY() - DEVIATION;
    final double bottom = getPos().getY() + getHeight() + DEVIATION;

    return left < coordinate.getX() && coordinate.getX() < right &&
      top < coordinate.getY() && coordinate.getY() < bottom;
  }

  public Coordinate relativeViewboxCoordinateToAbsolute(Coordinate c) {
    return new Coordinate(getPos().getX() + c.getX(), getPos().getY() + c.getY());
  }

  public int getWidth() {
    return (int) Controller.getInstance().getCanvas().getWidth();
  }

  public int getHeight() {
    return (int) Controller.getInstance().getCanvas().getHeight();
  }

  public Coordinate getPos() {
    return pos;
  }

  public void setPos(Coordinate pos) {
    this.pos = pos;
  }
}