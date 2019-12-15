package advent;

public class MathUtility {
  public static long lcm(int... values) {
    if (values.length < 2) {
      throw new RuntimeException("Cannot take the LCM of less than 2 values");
    }
    long currentLcm = simpleLcm(values[0], values[1]);
    for (int i = 2; i < values.length; i++) {
      currentLcm = simpleLcm(currentLcm, values[i]);
    }
    return currentLcm;
  }

  private static long simpleLcm(long a, long b) {
    return Math.abs(a * b) / gcd(a, b);
  }

  public static int gcd(int a, int b) {
    return (int) gcd((long) a, b);
  }

  public static long gcd(long a, long b) {
    while (b != 0) {
      long temp = b;
      b = a % b;
      a = temp;
    }
    return Math.abs(a);
  }
}
