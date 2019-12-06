package advent;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Day05 {
  public static void main(String[] args) throws IOException {
    int[] program = ParseUtility.extractInts(FileUtility.fileToString("input/05.txt"));

    // Part one
    FileUtility.printAndOutput(runWithInput(program, 1), "output/05a.txt");

    // Part two
    FileUtility.printAndOutput(runWithInput(program, 5), "output/05b.txt");
  }

  private static int runWithInput(int[] program, int initialInput) {
    Queue<Integer> inputQueue = new ArrayDeque<>();
    inputQueue.add(initialInput);

    ArrayDeque<Integer> outputQueue = new ArrayDeque<>();

    ShipComputer.runProgram(program, inputQueue, outputQueue);
    int last = outputQueue.removeLast();

    // Assert all elements in output queue except the last are 0
    if (outputQueue.stream().filter(i -> i == 0).count() != outputQueue.size()) {
      throw new RuntimeException("Tests with non-zero output");
    }

    return last;
  }
}
