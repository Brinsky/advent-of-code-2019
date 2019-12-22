package advent;

import advent.Day03.Point;
import advent.Day11.Direction;

import java.io.IOException;
import java.util.*;

public class Day15 {

  public static final Point ORIGIN = new Point(0, 0);

  public static void main(String[] args) throws IOException {
    long[] program = ParseUtility.extractLongs(FileUtility.fileToString("input/15.txt"));

    ShipComputer droid = new ShipComputer(program);

    Map<Point, Tile> world = new HashMap<>();
    world.put(ORIGIN, Tile.EMPTY);
    explore(droid, ORIGIN, world);
    Point oxygenSensor =
        world.keySet().stream().filter(p -> world.get(p) == Tile.OXYGEN_SENSOR).findFirst().get();

    // Part one
    FileUtility.printAndOutput(stepsToAllPoints(world, ORIGIN).get(oxygenSensor), "output/15a.txt");

    // Part two
    FileUtility.printAndOutput(
        stepsToAllPoints(world, oxygenSensor).values().stream()
            .mapToInt(Integer::valueOf)
            .max()
            .getAsInt(),
        "output/15b.txt");
  }

  private static Map<Point, Integer> stepsToAllPoints(Map<Point, Tile> world, Point start) {
    Queue<Point> queue = new ArrayDeque<>();
    Map<Point, Integer> steps = new HashMap<>();

    queue.add(start);
    steps.put(start, 0);
    while (!queue.isEmpty()) {
      Point current = queue.remove();
      int currentSteps = steps.get(current);

      for (Direction d : Direction.values()) {
        Point targetPoint = current.plus(d.vector);
        if (!steps.containsKey(targetPoint) && world.get(targetPoint) != Tile.WALL) {
          steps.put(targetPoint, currentSteps + 1);
          queue.add(targetPoint);
        }
      }
    }

    return steps;
  }

  private static void explore(ShipComputer droid, Point point, Map<Point, Tile> world) {
    for (Direction d : Direction.values()) {
      Point targetPoint = point.plus(d.vector);
      if (world.containsKey(targetPoint)) {
        continue;
      }
      Tile tile = moveDroid(droid, d);
      updateWorld(world, targetPoint, tile);
      if (tile != Tile.WALL) {
        explore(droid, targetPoint, world);
        // Undo the droid's last step
        moveDroid(droid, d.opposite());
      }
    }
  }

  private static void updateWorld(Map<Point, Tile> world, Point point, Tile tile) {
    if (world.containsKey(point) && world.get(point) != tile) {
      throw new RuntimeException("Tile mismatch at " + point);
    } else {
      world.put(point, tile);
    }
  }

  private static Tile moveDroid(ShipComputer droid, Direction d) {
    droid.addInput(getDirectionCode(d));
    droid.execute();
    return Tile.fromCode((int) droid.removeOutput());
  }

  private static int getDirectionCode(Direction d) {
    switch (d) {
      case NORTH:
        return 1;
      case SOUTH:
        return 2;
      case WEST:
        return 3;
      case EAST:
        return 4;
      default:
        throw new RuntimeException("Unknown direction " + d.name());
    }
  }

  private enum Tile {
    WALL('#'),
    EMPTY('.'),
    OXYGEN_SENSOR('O');

    public final char icon;

    Tile(char icon) {
      this.icon = icon;
    }

    public static Tile fromCode(int code) {
      switch (code) {
        case 0:
          return WALL;
        case 1:
          return EMPTY;
        case 2:
          return OXYGEN_SENSOR;
        default:
          throw new RuntimeException("Unrecognized tile code: " + code);
      }
    }
  }
}
