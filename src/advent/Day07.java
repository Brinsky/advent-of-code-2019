package advent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day07 {
  private static final int NUM_AMPLIFIERS = 5;

  public static void main(String[] args) throws IOException {
    int[] program = ParseUtility.extractInts(FileUtility.fileToString("input/07.txt"));

    // Part one
    FileUtility.printAndOutput(
        findMaxThrust(program, new int[] {0, 1, 2, 3, 4}, false), "output/07a.txt");

    // Part one
    FileUtility.printAndOutput(
        findMaxThrust(program, new int[] {5, 6, 7, 8, 9}, true), "output/07b.txt");
  }

  private static int findMaxThrust(int[] program, int[] phaseOptions, boolean feedbackLoop) {
    Map<Integer, Boolean> phasesUsed =
        Arrays.stream(phaseOptions)
            .boxed()
            .collect(Collectors.toMap(Function.identity(), i -> false));
    return findMaxThrust(program, feedbackLoop, new int[NUM_AMPLIFIERS], 0, phasesUsed);
  }

  // Recursively tries every valid phase setting configuration
  private static int findMaxThrust(
      int[] program,
      boolean feedbackLoop,
      int[] phaseSettings,
      int index,
      Map<Integer, Boolean> phasesUsed) {
    if (index == phaseSettings.length) {
      return computeThrust(program, phaseSettings, feedbackLoop);
    }

    int maxThrust = Integer.MIN_VALUE;
    for (int phase : phasesUsed.keySet()) {
      if (!phasesUsed.get(phase)) {
        phasesUsed.put(phase, true);
        phaseSettings[index] = phase;
        int thrust = findMaxThrust(program, feedbackLoop, phaseSettings, index + 1, phasesUsed);
        if (thrust > maxThrust) {
          maxThrust = thrust;
        }
        phasesUsed.put(phase, false);
      }
    }

    return maxThrust;
  }

  private static int computeThrust(int[] program, int[] phaseSettings, boolean feedbackLoop) {
    ShipComputer[] amplifiers = new ShipComputer[phaseSettings.length];
    for (int i = 0; i < phaseSettings.length; i++) {
      amplifiers[i] = new ShipComputer(program);
      amplifiers[i].addInput(phaseSettings[i]);
    }

    int previousOutput = 0;
    do {
      for (ShipComputer amplifier : amplifiers) {
        amplifier.addInput(previousOutput);

        // Once any program terminates completely, the feedback loop should stop running
        if (!amplifier.execute()) {
          feedbackLoop = false;
        }

        previousOutput = amplifier.removeOutput();
      }
    } while (feedbackLoop);

    return previousOutput;
  }
}
