package application.math;

public class Number {
  public static int getRandom(int min, int max) {
    return (int)(Math.random() * (max - min) + min);
  }

  public static double map(double value, double min1, double max1, double min2, double max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  public static boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static boolean isPrimeNumber(final long number) {
    if (number == 2L) return true;
    if (number <= 1L || number % 2L == 0) return false;

    final long half = number / 2L;
    for (long i = 3L; i < half; i += 2L)
      if (number % i == 0)
        return false;

    return true;
  }

  public static int getClosestPrimeNumber(int i) {
    if (i <= 1) return 1;
    if (Number.isPrimeNumber(i)) return i;
    return getClosestPrimeNumber(i - 1);
  }

  public static double constrain(double n, double min, double max) {
    if (n <= min) return min;
    return Math.min(n, max);
  }

  public static int constrain(int n, int min, int max) {
    if (n <= min) return min;
    return Math.min(n, max);
  }

  public static float constrain(float n, float min, float max) {
    if (n <= min) return min;
    return Math.min(n, max);
  }

  public static long constrain(long n, long min, long max) {
    if (n <= min) return min;
    return Math.min(n, max);
  }
}