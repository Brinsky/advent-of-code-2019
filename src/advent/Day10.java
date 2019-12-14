package advent;

import advent.Day03.Point;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day10 {

  public static final int BETTING_INDEX = 199;

  public static void main(String[] args) throws IOException {
    Set<Point> points = parsePoints(FileUtility.fileToString("input/10.txt"));

    // Part one
    Point origin = points.stream().max(Comparator.comparingInt(p -> numVisible(p, points))).get();
    FileUtility.printAndOutput(numVisible(origin, points), "output/10a.txt");

    // Part two
    points.remove(origin);
    List<Point> laserOrder =
        points.stream()
            .sorted(Comparator.comparing(p -> getLaserAngle(origin, p, points)))
            .collect(Collectors.toList());
    FileUtility.printAndOutput(coordinateHash(laserOrder.get(BETTING_INDEX)), "output/10b.txt");
  }

  private static int coordinateHash(Point p) {
    return p.x * 100 + p.y;
  }

  private static int numVisible(Point a, Set<Point> points) {
    int visibile = 0;

    for (Point b : points) {
      if (!b.equals(a) && numPointsBetween(a, b, points) == 0) {
        visibile++;
      }
    }

    return visibile;
  }

  private static int numPointsBetween(Point a, Point b, Set<Point> points) {
    Point dir = b.minus(a).simplify();
    int pointsBetween = 0;
    for (Point p = a.plus(dir); !p.equals(b); p = p.plus(dir)) {
      if (points.contains(p)) {
        pointsBetween++;
      }
    }
    return pointsBetween;
  }

  private static double getLaserAngle(Point origin, Point other, Set<Point> points) {
    // Add a full rotation for each point in between our two chosen points
    return getAngle(other.minus(origin)) + 2 * Math.PI * numPointsBetween(origin, other, points);
  }

  /**
   * Because the y-axis increases going "downwards", we invert it so that arc-tangent yields 0 for
   * the straight "upwards" vector. Hence, we have to correct all negative angles by adding 2 * PI.
   */
  private static double getAngle(Point vector) {
    double angle = Math.atan2(vector.x, -vector.y);
    return (angle < 0) ? angle + 2 * Math.PI : angle;
  }

  private static Set<Point> parsePoints(String s) {
    Character[][] map = ParseUtility.parseMatrix(s);
    Set<Point> points = new HashSet<>();

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[0].length; y++) {
        if (map[x][y] == '#') {
          points.add(new Point(x, y));
        }
      }
    }

    return points;
  }
}
