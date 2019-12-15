package advent;

import java.io.IOException;
import java.util.*;

public class Day03 {
  private static final Point ORIGIN = new Point(0, 0);

  public static void main(String[] args) throws IOException {
    String[] lineSpecs = FileUtility.fileToString("input/03.txt").split("\n");

    Map<Point, PointInfo> plane = new HashMap<>();
    List<IntersectionInfo> intersections = new ArrayList<>();

    plotLine(lineSpecs[0], 0, plane, intersections);
    plotLine(lineSpecs[1], 1, plane, intersections);

    // Part one
    FileUtility.printAndOutput(
        intersections.stream()
            .map(pi -> pi.point)
            .mapToInt(ORIGIN::manhattanDistance)
            .min()
            .getAsInt(),
        "output/03a.txt");

    // Part two
    FileUtility.printAndOutput(
        intersections.stream().mapToInt(pi -> pi.totalStepsElapsed).min().getAsInt(),
        "output/03b.txt");
  }

  private static void plotLine(
      String lineSpec,
      int lineId,
      Map<Point, PointInfo> plane,
      List<IntersectionInfo> intersections) {
    int currentX = 0, currentY = 0;
    int stepsElapsed = 0;

    for (String instruction : lineSpec.split(",")) {
      int delta = Integer.parseInt(instruction.substring(1));
      Point direction = getDirectionVector(instruction.charAt(0));

      for (int i = 0; i < delta; i++) {
        stepsElapsed++;
        currentX += direction.x;
        currentY += direction.y;
        Point current = new Point(currentX, currentY);

        if (plane.containsKey(current)) {
          PointInfo previous = plane.get(current);

          // If the line ID is the same, we want to do nothing (in order to preserve the "earlier"
          // point)
          if (previous.lineId != lineId) {
            intersections.add(new IntersectionInfo(current, stepsElapsed + previous.stepsElapsed));
          }
        } else {
          plane.put(current, new PointInfo(lineId, stepsElapsed));
        }
      }
    }
  }

  private static Point getDirectionVector(char direction) {
    switch (direction) {
      case 'R':
        return new Point(1, 0);
      case 'U':
        return new Point(0, 1);
      case 'L':
        return new Point(-1, 0);
      case 'D':
        return new Point(0, -1);
      default:
        throw new RuntimeException("Unexpected directional indicator:" + direction);
    }
  }

  private static class IntersectionInfo {
    public final Point point;
    public final int totalStepsElapsed;

    private IntersectionInfo(Point point, int totalStepsElapsed) {
      this.point = point;
      this.totalStepsElapsed = totalStepsElapsed;
    }
  }

  private static class PointInfo {
    public final int lineId;
    public final int stepsElapsed;

    public PointInfo(int lineId, int stepsElapsed) {
      this.lineId = lineId;
      this.stepsElapsed = stepsElapsed;
    }
  }

  public static class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    /** Returns a new point where the coordinates have been divided by their GCD */
    public Point simplify() {
      int gcd = MathUtility.gcd(x, y);
      return new Point(x / gcd, y / gcd);
    }

    public Point plus(Point p) {
      return new Point(x + p.x, y + p.y);
    }

    public Point minus(Point p) {
      return new Point(x - p.x, y - p.y);
    }

    @Override
    public String toString() {
      return String.format("(%d, %d)", x, y);
    }

    @Override
    public int hashCode() {
      return x * 7 + (y + 100) * 11;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof Point)) {
        return false;
      }

      Point p = (Point) other;
      return x == p.x && y == p.y;
    }

    public int manhattanDistance(Point p) {
      return Math.abs(x - p.x) + Math.abs(y - p.y);
    }
  }
}
