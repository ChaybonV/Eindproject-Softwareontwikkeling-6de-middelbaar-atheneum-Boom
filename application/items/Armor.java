package application.items;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import application.math.Number;
import application.config.ConfigHandler;
import application.javafx.Draw;

import java.text.MessageFormat;

public class Armor extends Item {

  private int protection;

  public Armor(int protection, double weight) {
    super(weight);
    this.protection = protection;
  }

  public static boolean isValidProtection(int protection) {
    return protection >= 0 && protection <= ConfigHandler.getInt("items.Armor.PROTECTION_MAX");
  }

  @Override
  public void draw(GraphicsContext gc) {
    gc.setFill(Color.BLUE);
    gc.setLineWidth(4);
    gc.setStroke(Color.BLACK);
    Draw.fillCircle(gc, getPos(), ConfigHandler.getDouble("items.Item.WIDTH"));
  }

  public int getProtection() {
    return protection;
  }

  public void setProtection(int protection) throws IllegalArgumentException {
    if (!isValidProtection(protection))
      throw new IllegalArgumentException(MessageFormat.format("The given protection '{'{0}'}' is not valid", protection));
    this.protection = protection;
  }

  public int reduceProtection(int i) throws IllegalArgumentException {
    final int pro = getProtection() - i;

    setProtection(Number.constrain(pro, 0, Integer.MAX_VALUE));

    if (getProtection() == 0)
      return Math.abs(pro);

    return 0;
  }

  public double getProtectionPercentage() {
    return (double) getProtection() / (double) ConfigHandler.getInt("items.Armor.PROTECTION_MAX");
  }

  @Override
  public String toString() {
    return "%s (%d/%d)".formatted(super.toString(), getProtection(), ConfigHandler.getInt("items.Armor.PROTECTION_MAX"));
  }

  @Override
  public String getExtra() {
    return String.valueOf(getProtection());
  }
}