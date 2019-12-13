package advent;

import java.io.IOException;

public class Day05 {
  public static void main(String[] args) throws IOException {
    int[] program = ParseUtility.extractInts(FileUtility.fileToString("input/05.txt"));

    // Part one
    FileUtility.printAndOutput(runWithInput(program, 1), "output/05a.txt");

    // Part two
    FileUtility.printAndOutput(runWithInput(program, 5), "output/05b.txt");
  }

  private static long runWithInput(int[] program, int initialInput) {
    ShipComputer computer = new ShipComputer(program);
    computer.addInput(initialInput);
    computer.execute();

    while (computer.outputSize() > 1) {
      if (computer.removeOutput() != 0) {
        throw new RuntimeException("Found tests with non-zero output");
      }
    }

    return computer.removeOutput();
  }
}
