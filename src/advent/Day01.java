package advent;

import java.io.IOException;
import java.util.List;

public class Day01 {
  public static void main(String[] args) throws IOException {
    List<Integer> moduleMasses =
        ParseUtility.extractIntegers(FileUtility.fileToString("input/01.txt"));

    // Part one
    FileUtility.printAndOutput(
        DataUtility.toIntStream(moduleMasses).map(Day01::computeInitialFuel).sum(),
        "output/01a.txt");

    // Part two
    FileUtility.printAndOutput(
        DataUtility.toIntStream(moduleMasses)
            .map(Day01::computeInitialFuel)
            .map(Day01::computeTotalFuel)
            .sum(),
        "output/01b.txt");
  }

  private static int computeInitialFuel(int mass) {
    return (mass / 3) - 2;
  }

  private static int computeTotalFuel(int initialFuel) {
    int additionalFuel = (initialFuel / 3) - 2;
    return initialFuel + (additionalFuel > 0 ? computeTotalFuel(additionalFuel) : 0);
  }
}
