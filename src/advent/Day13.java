package advent;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import advent.Day03.Point;

public class Day13 {

  public static final int QUARTERS = 2;

  public static void main(String[] args) throws IOException {
    long[] program = ParseUtility.extractLongs(FileUtility.fileToString("input/13.txt"));

    // Part one
    Game initial = new Game(program);
    initial.tick();
    FileUtility.printAndOutput(initial.getBlockCount(), "output/13a.txt");

    // Part two
    Game game = new Game(program, QUARTERS);
    // This strategy should prevent the ball from going out of bounds, but it isn't inherently
    // guaranteed to terminate since the ball coulds get stuck in some kind of loop
    while (game.tick()) {
      game.movePaddleTowardsBall();
    }
    FileUtility.printAndOutput(game.getScore(), "output/13b.txt");
  }

  private static String gridToString(Map<Point, Tile> grid) {
    if (grid.isEmpty()) {
      return "";
    }

    int minX = grid.keySet().stream().mapToInt(Point::getX).min().getAsInt();
    int maxX = grid.keySet().stream().mapToInt(Point::getX).max().getAsInt();
    int minY = grid.keySet().stream().mapToInt(Point::getY).min().getAsInt();
    int maxY = grid.keySet().stream().mapToInt(Point::getY).max().getAsInt();

    StringBuilder builder = new StringBuilder();
    for (int y = minY; y <= maxY; y++) {
      for (int x = minX; x <= maxX; x++) {
        builder.append(grid.containsKey(new Point(x, y)) ? grid.get(new Point(x, y)).icon : '.');
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  private static class Game {

    private final ShipComputer computer;
    private final Map<Point, Tile> grid = new HashMap<>();
    private int score = 0;
    private int blockCount = 0;
    private Point ball;
    private Point paddle;

    public Game(long[] rom, long quarters) {
      rom = Arrays.copyOf(rom, rom.length);
      rom[0] = quarters;
      computer = new ShipComputer(rom);
    }

    public Game(long[] rom) {
      this(rom, rom[0]);
    }

    /**
     * Returns true if awaiting input. Returns false if all blocks are destroyed, the ball is out of
     * bounds, or execution has halted.
     */
    public boolean tick() {
      boolean awaitingInput = computer.execute();

      while (computer.hasOutput()) {
        Point point = new Point((int) computer.removeOutput(), (int) computer.removeOutput());

        if (point.x == -1 && point.y == 0) {
          score = (int) computer.removeOutput();
        } else {
          Tile newTile = Tile.parseTile((int) computer.removeOutput());
          Tile oldTile = grid.getOrDefault(point, null);

          if (oldTile == Tile.BLOCK && newTile != Tile.BLOCK) {
            blockCount--;
          }

          switch (newTile) {
            case BALL:
              ball = point;
              break;
            case PADDLE:
              paddle = point;
              break;
            case BLOCK:
              if (oldTile != Tile.BLOCK) {
                blockCount++;
              }
              break;
            default:
          }

          grid.put(point, newTile);
        }
      }

      return awaitingInput && blockCount > 0 && ball.y < paddle.y;
    }

    public void movePaddleTowardsBall() {
      if (ball.x < paddle.x) {
        computer.addInput(-1); // LEFT
      } else if (ball.x == paddle.x) {
        computer.addInput(0); // NEUTRAL
      } else { // if (ball.x > paddle.x)
        computer.addInput(1); // RIGHT
      }
    }

    public int getScore() {
      return score;
    }

    public int getBlockCount() {
      return blockCount;
    }

    @Override
    public String toString() {
      if (grid.isEmpty()) {
        return "";
      }

      int minX = grid.keySet().stream().mapToInt(Point::getX).min().getAsInt();
      int maxX = grid.keySet().stream().mapToInt(Point::getX).max().getAsInt();
      int minY = grid.keySet().stream().mapToInt(Point::getY).min().getAsInt();
      int maxY = grid.keySet().stream().mapToInt(Point::getY).max().getAsInt();

      StringBuilder builder = new StringBuilder();
      for (int y = minY; y <= maxY; y++) {
        for (int x = minX; x <= maxX; x++) {
          builder.append(grid.containsKey(new Point(x, y)) ? grid.get(new Point(x, y)).icon : '?');
        }
        builder.append('\n');
      }
      builder.append("[ CURRENT SCORE: " + score + " ]");

      return builder.toString();
    }
  }

  private enum Tile {
    EMPTY(' '),
    WALL('#'),
    BLOCK('B'),
    PADDLE('-'),
    BALL('O');

    public final char icon;

    Tile(char icon) {
      this.icon = icon;
    }

    public static Tile parseTile(int tile) {
      switch (tile) {
        case 0:
          return EMPTY;
        case 1:
          return WALL;
        case 2:
          return BLOCK;
        case 3:
          return PADDLE;
        case 4:
          return BALL;
        default:
          throw new RuntimeException("Unexpected tile value: " + tile);
      }
    }
  }
}
