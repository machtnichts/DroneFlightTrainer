package sandbox;

public final class DSPFast  {

  private static final double PI_2 = 1.5707963F; // Math.PI / 2
  private static final double PI_4 = 0.7853982F; // Math.PI / 4
  private static final double PI_3_4 = 2.3561945F; // (Math.PI / 4) * 3
  private static final double MINUS_PI_2 = -1.5707963F; // Math.PI / -2

  public static double atan2(double y, double x) {
    double r;
    double abs_y = Double.longBitsToDouble(Double.doubleToRawLongBits(y)<<1>>>1);
    if (x == 0.0F) {
      if (y > 0.0F) {
        return PI_2;
      }
      if (y == 0.0F) {
        return 0.0f;
      }
      return MINUS_PI_2;
    } else if (x > 0) {
      r = (x - abs_y) / (x + abs_y);
      r = PI_4 - PI_4 * r;
    } else {
      r = (x + abs_y) / (abs_y - x);
      r = PI_3_4 - PI_4 * r;
    }
    return y < 0 ? -r : r;
  }
}