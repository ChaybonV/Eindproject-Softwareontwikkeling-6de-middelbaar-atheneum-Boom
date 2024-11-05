package application.math;

public class Vector implements Cloneable {
  private double x;
  private double y;

  public Vector() {
    this(0, 0);
  }

  public Vector(double speedX, double speedY) {
    this.x = speedX;
    this.y = speedY;
  }

  public Vector(Coordinate from, Coordinate to) {
    this(to.getX() - from.getX(), to.getY() - from.getY());
  }

  public static Vector add(Vector vector1, Vector vector2) {
    return new Vector(vector1.getX() + vector2.getX(),
      vector1.getY() + vector2.getY());
  }

  public static Vector sub(Vector vector1, Vector vector2) {
    return new Vector(vector1.getX() - vector2.getX(),
      vector1.getY() - vector2.getY());
  }

  public static Vector mult(Vector vector, double scalar) {
    return new Vector(vector.getX() * scalar, vector.getY() * scalar);
  }

  public static Vector div(Vector vector, double scalar) {
    return Vector.mult(vector, 1 / scalar);
  }

  public Angle getAngle() {
    return new Angle(Math.atan2(getY(), getX()));
  }

  public void setAngle(Angle angle) {
    final double mag = getMag();
    final double rad = angle.getRadians();
    this.setX(Math.cos(rad) * mag);
    this.setY(Math.sin(rad) * mag);
  }

  public void rotate(Angle angle) {
    this.setAngle(new Angle(getAngle().getRadians() + angle.getRadians()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector vector = (Vector) o;
    return Double.compare(vector.getX(), getX()) == 0 &&
      Double.compare(vector.getY(), getY()) == 0;
  }

  @Override
  public Vector clone() {
    Vector clone = null;
    try {
      clone = (Vector) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    if (clone != null) {
      clone.setX(getX());
      clone.setY(getY());
    }
    return clone;
  }

  @Override
  public String toString() {
    return "Vector{" + "speedX=" + x + ", speedY=" + y + '}';
  }

  public Vector add(Vector vector) {
    this.setX(this.getX() + vector.getX());
    this.setY(this.getY() + vector.getY());
    return this;
  }

  public Vector sub(Vector vector) {
    this.setX(this.getX() - vector.getX());
    this.setY(this.getY() - vector.getY());
    return this;
  }

  public void mult(double scalar) {
    this.setX(this.getX() * scalar);
    this.setY(this.getY() * scalar);
  }

  public void div(double scalar) {
    mult(1 / scalar);
  }

  public void normalize() {
    final double mag = getMag();
    this.setX(this.getX() / mag);
    this.setY(this.getY() / mag);
  }

  public double getMag() {
    return Math.hypot(this.getX(), this.getY());
  }

  public void setMag(double len) {
    this.normalize();
    this.mult(len);
  }

  public void zero() {
    setX(0);
    setY(0);
  }

  public boolean isZero() {
    return getX() == 0 && getY() == 0;
  }

  public void limit(double max) {
    if (getMag() > max) setMag(max);
  }

  public void invert() {
    mult(-1);
  }

  public void invertX() {
    this.setX(-this.getX());
  }

  public void invertY() {
    this.setY(-this.getY());
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