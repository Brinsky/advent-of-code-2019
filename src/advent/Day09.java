package advent;

import java.io.IOException;

public class Day09 {
  public static void main(String[] args) throws IOException {
    long[] program = ParseUtility.extractLongs(FileUtility.fileToString("input/09.txt"));

    // Part one
    FileUtility.printAndOutput(runWithInput(program, 1), "output/09a.txt");

    // Part two
    FileUtility.printAndOutput(runWithInput(program, 2), "output/09b.txt");
  }

  private static long runWithInput(long[] program, long initialInput) {
    ShipComputer computer = new ShipComputer(program);
    computer.addInput(initialInput);
    computer.execute();

    if (computer.outputSize() > 1) {
      throw new RuntimeException("Extraneous output from test program");
    }

    return computer.removeOutput();
  }
}
