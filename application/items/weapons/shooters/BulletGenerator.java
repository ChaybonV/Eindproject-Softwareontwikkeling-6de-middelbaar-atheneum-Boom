package application.items.weapons.shooters;

import application.math.Coordinate;
import application.math.Vector;
import application.items.weapons.shooters.magazines.Magazine;

public class BulletGenerator {
  private final double vel;
  private final int fireRate;
  private final int damage;
  private long prevFire;

  public BulletGenerator(double vel, int fireRate, int damage) {
    this.vel = vel;
    this.fireRate = fireRate;
    this.damage = damage;
  }

  public Bullet generate(Shooter < ? > shooter, Coordinate headedTo) throws Magazine.EmptyMagazineException, FiringTooFast {
    if (shooter.getMagazine() == null || shooter.getMagazine().isEmpty()) {
    	throw new Magazine.EmptyMagazineException();
    }
    if (!canFire()) {
    	throw new FiringTooFast();
    }
    Vector vel = new Vector(shooter.getHolder().getPos(), headedTo);
    vel.setMag(getVel());

    shooter.getMagazine().fireBullet();
    updatePrevFire();

    final Coordinate startLocation = new Coordinate(shooter.getHolder().getPos());
    final Vector startLocationVector = vel.clone();
    startLocationVector.setMag(shooter.getHolder().getbulletStartLocationRadius());
    startLocation.add(startLocationVector);
    return new Bullet(getDamage(), startLocation, vel);
  }

  public int getDamage() {
    return damage;
  }

  public boolean canFire() {
    return getPrevFire() + getFireRate() <= System.currentTimeMillis();
  }

  public void updatePrevFire() {
    setPrevFire(System.currentTimeMillis());
  }

  public long getPrevFire() {
    return prevFire;
  }

  private void setPrevFire(long prevFire) {
    this.prevFire = prevFire;
  }

  public double getVel() {
    return vel;
  }

  public int getFireRate() {
    return fireRate;
  }

  public static class FiringTooFast extends RuntimeException {}
}