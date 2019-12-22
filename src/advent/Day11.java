package advent;

import advent.Day03.Point;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Day11 {

  private static final int BLACK = 0;
  private static final int WHITE = 1;
  private static final char CHAR_BLACK = ' ';
  private static final char CHAR_WHITE = '#';

  public static void main(String[] args) throws IOException {
    long[] program = ParseUtility.extractLongs(FileUtility.fileToString("input/11.txt"));

    // Part one
    FileUtility.printAndOutput(runRobot(program, new HashMap<>()), "output/11a.txt");

    // Part two
    Map<Point, Integer> panels = new HashMap<>();
    panels.put(new Point(0, 0), WHITE);
    runRobot(program, panels);
    FileUtility.printAndOutput(panelsToString(panels), "output/11b.txt");
  }

  private static String panelsToString(Map<Point, Integer> panels) {
    int minX = panels.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
    int maxX = panels.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
    int minY = panels.keySet().stream().mapToInt(p -> p.y).min().getAsInt();
    int maxY = panels.keySet().stream().mapToInt(p -> p.y).max().getAsInt();

    StringBuilder builder = new StringBuilder();
    for (int y = minY; y <= maxY; y++) {
      for (int x = minX; x <= maxX; x++) {
        int color = panels.getOrDefault(new Point(x, y), WHITE);
        builder.append(color == WHITE ? CHAR_WHITE : CHAR_BLACK);
      }
      builder.append("\n");
    }

    return builder.toString();
  }

  /** Runs the robot program until termination - returns the number of unique panels visited. */
  public static int runRobot(long[] program, Map<Point, Integer> panels) {
    ShipComputer computer = new ShipComputer(program);
    Point position = new Point(0, 0);
    Direction direction = Direction.NORTH;
    int uniquePanels = 0;

    boolean running = true;
    while (running) {
      if (!panels.containsKey(position)) {
        uniquePanels++;
      }

      computer.addInput(panels.getOrDefault(position, BLACK));
      running = computer.execute();
      final int newColor = (int) computer.removeOutput();
      final int rotation = (int) computer.removeOutput();

      // Paint, rotate, move
      panels.put(position, newColor);
      direction = (rotation == 0) ? direction.rotateLeft() : direction.rotateRight();
      position = position.plus(direction.vector);
    }

    return uniquePanels;
  }

  public enum Direction {
    EAST(new Point(1, 0)),
    NORTH(new Point(0, -1)),
    WEST(new Point(-1, 0)),
    SOUTH(new Point(0, 1));

    public final Point vector;

    Direction(Point vector) {
      this.vector = vector;
    }

    Direction rotateLeft() {
      switch (this) {
        case EAST:
          return NORTH;
        case NORTH:
          return WEST;
        case WEST:
          return SOUTH;
        case SOUTH:
          return EAST;
      }
      throw new UnsupportedOperationException();
    }

    Direction rotateRight() {
      switch (this) {
        case EAST:
          return SOUTH;
        case NORTH:
          return EAST;
        case WEST:
          return NORTH;
        case SOUTH:
          return WEST;
      }
      throw new UnsupportedOperationException();
    }

    Direction opposite() {
      switch (this) {
        case EAST:
          return WEST;
        case NORTH:
          return SOUTH;
        case WEST:
          return EAST;
        case SOUTH:
          return NORTH;
      }
      throw new UnsupportedOperationException();
    }
  }
}
