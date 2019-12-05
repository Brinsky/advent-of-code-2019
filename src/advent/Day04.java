package advent;

import java.io.IOException;
import java.util.stream.IntStream;

public class Day04 {
  public static void main(String[] args) throws IOException {
    String[] bounds = FileUtility.fileToString("input/04.txt").split("-");
    int lower = Integer.parseInt(bounds[0]);
    int upper = Integer.parseInt(bounds[1]);

    // Part one
    FileUtility.printAndOutput(
        IntStream.range(lower, upper + 1)
            .mapToObj(Integer::toString)
            .filter(Day04::isAscending)
            .filter(Day04::hasAtLeastDouble)
            .count(),
        "output/04a.txt");

    // Part two
    FileUtility.printAndOutput(
        IntStream.range(lower, upper + 1)
            .mapToObj(Integer::toString)
            .filter(Day04::isAscending)
            .filter(Day04::hasStandaloneDouble)
            .count(),
        "output/04a.txt");
  }

  private static boolean validPassword(String password) {
    boolean seenAdjacentPair = false;
    for (int i = 1; i < password.length(); i++) {
      if (password.charAt(i - 1) > password.charAt(i)) {
        // Ensure characters are ascending
        return false;
      } else if (password.charAt(i - 1) == password.charAt(i)) {
        seenAdjacentPair = true;
      }
    }

    return seenAdjacentPair;
  }

  private static boolean isAscending(String password) {
    for (int i = 1; i < password.length(); i++) {
      if (password.charAt(i - 1) > password.charAt(i)) {
        return false;
      }
    }

    return true;
  }

  private static boolean hasAtLeastDouble(String password) {
    for (int i = 1; i < password.length(); i++) {
      if (password.charAt(i - 1) == password.charAt(i)) {
        return true;
      }
    }

    return false;
  }

  private static boolean hasStandaloneDouble(String password) {
    int seenCount = 1;
    for (int i = 1; i < password.length(); i++) {
      if (password.charAt(i - 1) == password.charAt(i)) {
        seenCount++;
      } else if (seenCount == 2) {
        return true;
      } else {
        seenCount = 1;
      }
    }

    return seenCount == 2;
  }
}
