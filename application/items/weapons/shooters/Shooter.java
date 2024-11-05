package application.items.weapons.shooters;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import application.math.Coordinate;
import application.Game;
import application.config.ConfigHandler;
import application.items.weapons.Weapon;
import application.items.weapons.shooters.magazines.Magazine;
import application.javafx.Draw;

public abstract class Shooter<M extends Magazine> extends Weapon {
    private final BulletGenerator bulletGenerator;
    private M magazine;

    public Shooter(BulletGenerator bulletGenerator, double weight) {
        super(weight);
        this.bulletGenerator = bulletGenerator;
    }

    public abstract boolean canHaveMagazine(Magazine magazine);

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setLineWidth(4);
        gc.setStroke(Color.BLACK);
        Draw.fillCircle(gc, getPos(), ConfigHandler.getDouble("items.Item.WIDTH"));
    }

    @Override
    public void attack(Coordinate to) {
        findAndSetMagazine();
        try {
            final Bullet bullet = getBullet(to);
            if(bullet != null) {
                Game.getInstance().getBullets().add(bullet);
            }
        } catch(Magazine.EmptyMagazineException e) {
            findAndSetMagazine();
        }
    }

    @SuppressWarnings("unchecked")
    public void findAndSetMagazine() {
        if(getMagazine() == null || getMagazine().isEmpty()) {
            setMagazine((M) getHolder().getNewMagazine(this::canHaveMagazine));
        }
    }

    public Magazine getMagazine() {
        return magazine;
    }

    public void setMagazine(M magazine) {
        this.magazine = magazine;
    }

    public BulletGenerator getBulletGenerator() {
        return bulletGenerator;
    }

    public Bullet getBullet(Coordinate heading) throws Magazine.EmptyMagazineException {
        try {
            return getBulletGenerator().generate(this, heading);
        } catch(BulletGenerator.FiringTooFast ignored) {
            return null;
        }
    }
}
