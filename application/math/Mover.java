package application.math;

import application.objects.Drawable;
import application.objects.Updatable;

import java.text.MessageFormat;

abstract public class Mover implements Drawable, Updatable {
  private Coordinate pos;
  private Vector vel;
  private Vector acc;
  private double mass;

  public Mover(Coordinate pos, double mass) {
    this(pos, new Vector(), mass);
  }

  public Mover(Coordinate pos, Vector vel, double mass) {
    this.pos = pos;
    this.vel = vel;
    this.mass = mass;
    this.acc = new Vector();
  }

  public void update() {
    getVel().add(getAcc());
    getPos().add(getVel());
    getAcc().zero();
  }

  public void applyForce(Vector force) {
    getAcc().add(Vector.div(force, getMass()));
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = getPos().hashCode();
    result = 31 * result + getVel().hashCode();
    result = 31 * result + getAcc().hashCode();
    temp = Double.doubleToLongBits(getMass());
    result = 31 * result + (int)(temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Mover)) return false;

    Mover mover = (Mover) o;

    if (Double.compare(mover.getMass(), getMass()) != 0) return false;
    if (!getPos().equals(mover.getPos())) return false;
    if (!getVel().equals(mover.getVel())) return false;
    return getAcc().equals(mover.getAcc());
  }

  @Override
  public String toString() {
    return MessageFormat.format("Mover'{'pos={0}, vel={1}, acc={2}, mass={3}'}'", pos.toString(), vel.toString(), acc.toString(), mass);
  }

  public Coordinate getPos() {
    return pos;
  }

  public void setPos(Coordinate pos) {
    this.pos = pos;
  }

  public Vector getVel() {
    return vel;
  }

  public void setVel(Vector vel) {
    this.vel = vel;
  }

  public Vector getAcc() {
    return acc;
  }

  public void setAcc(Vector acc) {
    this.acc = acc;
  }

  public double getMass() {
    return mass;
  }

  public void setMass(double mass) {
    this.mass = mass;
  }
}