package application.math;

public class Angle {
  public static final boolean SIMPLIFY = false;
  private double rad;

  public Angle() {
    this(0);
  }

  public Angle(double rad) {
    this.rad = rad;
  }

  static public double degreesToRadians(double degrees) {
    return degrees / (180 / Math.PI);
  }

  static public double radiansToDegrees(double radians) {
    return radians * (180 / Math.PI);
  }

  static public double simplifyRadians(double radians) {
    final double DOUBLE_PI = Math.PI * 2D;

    radians %= DOUBLE_PI;
    return radians < 0 ? radians + DOUBLE_PI : radians;
  }

  static public double simplifyDegrees(double degrees) {
    degrees %= 360D;
    return degrees < 0 ? degrees + 360D : degrees;
  }

  @Override
  public boolean equals(Object o) {
    return this.getClass() == o.getClass() && equals(o, false);
  }

  public boolean equals(Object o, boolean simplify) {
    if (super.equals(o)) return true;
    if (o == null || this.getClass() != o.getClass()) return false;

    Angle angle = (Angle) o;

    if (simplify)
      return Double.compare(angle.getRadians(true), this.getRadians(true)) == 0;
    else
      return Double.compare(angle.getRadians(), this.getRadians()) == 0;
  }

  public boolean simplify() {
    final double prevRad = this.getRadians(false);
    final double newRad = Angle.simplifyRadians(prevRad);

    if (prevRad == newRad) return false;

    setRadians(newRad);
    return true;
  }

  public double getRadians() {
    return getRadians(Angle.SIMPLIFY);
  }

  public void setRadians(double rad) {
    this.rad = rad;
  }

  public double getRadians(boolean simplify) {
    return simplify ? Angle.simplifyRadians(rad) : rad;
  }

  public double getDegrees() {
    return getDegrees(Angle.SIMPLIFY);
  }

  public void setDegrees(double degrees) {
    this.setRadians(Angle.degreesToRadians(degrees));
  }

  public double getDegrees(boolean simplify) {
    return simplify ? Angle.simplifyDegrees(Angle.radiansToDegrees(this.getRadians())) : Angle.radiansToDegrees(this.getRadians());
  }
}