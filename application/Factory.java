package application;

import com.sun.jdi.InternalException;
import application.math.Number;

import java.util.Arrays;

public abstract class Factory {

  public static < V > V getRandomValueOf(V[] values) {
    return values[Number.getRandom(0, values.length)];
  }

  public static < V extends Factory.WeightHolder > V getRandomByWeightValueOf(V[] values) {
    int total = Number.getRandom(0, Arrays.stream(values).mapToInt(Factory.WeightHolder::getWeight).sum());

    for (V value: values) {
      total -= value.getWeight();
      if (total <= 0)
        return value;
    }
    throw new InternalException();
  }

  public interface WeightHolder {
    int getWeight();
  }
}