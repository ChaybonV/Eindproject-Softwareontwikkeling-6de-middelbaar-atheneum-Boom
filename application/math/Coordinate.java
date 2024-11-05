package application.math;

public class Coordinate {
  private double x;
  private double y;

  public Coordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Coordinate(Coordinate coordinate) {
    this(coordinate.getX(), coordinate.getY());
  }

  public double getDistanceBetween(Coordinate pos) {
    return Math.hypot(this.getX() - pos.getX(), this.getY() - pos.getY());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coordinate that = (Coordinate) o;
    return Double.compare(that.getX(), getX()) == 0 &&
      Double.compare(that.getY(), getY()) == 0;
  }

  public void add(Vector v) {
    setX(getX() + v.getX());
    setY(getY() + v.getY());
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }
}