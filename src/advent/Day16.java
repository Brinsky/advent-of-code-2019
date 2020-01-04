package advent;

import java.io.IOException;
import java.util.Arrays;

public class Day16 {

  private static final int PHASES = 100;
  private static final int REPETITIONS = 10_000;
  private static final int MESSAGE_LENGTH = 8;
  private static final int OFFSET_LENGTH = 7;

  public static void main(String[] args) throws IOException {
    int[] digits = FileUtility.fileToString("input/16.txt").chars().map(c -> c - '0').toArray();
    int offset = Integer.parseInt(concatenate(digits, OFFSET_LENGTH));

    // Part one
    FileUtility.printAndOutput(
        concatenate(applyPhases(digits, PHASES), MESSAGE_LENGTH), "output/16a.txt");

    // Part two
    // Each output element at position i only depends on input elements at or after position i.
    // Starting at position size/2, each output element is exactly the sum of all input elements
    // from its position onwards.

    // Assume that the offset (a.k.a position at which the message starts) is at least size/2.
    // Given an offset N, let S = input[N] + input[N + 1] + input[N + 2] ...
    // Then we know output[N] = S and output[k] = output[k - 1] - input[k - 1] for all k > N

    // To make things easier, we truncate all elements before position N, after which point we act
    // as if N = 0

    final int newLength = digits.length * REPETITIONS;
    if (offset >= newLength - MESSAGE_LENGTH) {
      throw new RuntimeException("Offset puts final message outside of array");
    } else if (offset < newLength / 2) {
      throw new RuntimeException(
          "Fast algorithm only works for messages at least halfway into the array");
    }

    // Create the "repeated" array from the original input, but start at the message offset position
    int[] truncated = new int[newLength - offset];
    for (int i = offset; i < newLength; i++) {
      truncated[i - offset] = digits[i % digits.length];
    }

    FileUtility.printAndOutput(
        concatenate(applyCumulativeSumPhases(truncated, PHASES), MESSAGE_LENGTH), "output/16b.txt");
  }

  private static int[] applyCumulativeSumPhases(int[] input, int phases) {
    for (int i = 0; i < phases; i++) {
      input = applyCumulativeSumPhase(input);
    }
    return input;
  }

  private static int[] applyCumulativeSumPhase(int[] input) {
    int[] output = new int[input.length];
    output[0] = Arrays.stream(input).sum();
    for (int i = 1; i < output.length; i++) {
      output[i] = output[i - 1] - input[i - 1];
    }
    for (int i = 0; i < output.length; i++) {
      output[i] = getFirstDigit(output[i]);
    }
    return output;
  }

  private static String concatenate(int[] values, int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(values[i]);
    }
    return builder.toString();
  }

  private static int getFirstDigit(int number) {
    return Math.abs(number % 10);
  }

  private static int[] applyPhases(int[] input, int phases) {
    for (int i = 0; i < phases; i++) {
      input = applyPhase(input);
    }
    return input;
  }

  private static int[] applyPhase(int[] input) {
    int[] output = new int[input.length];
    for (int outputIndex = 0; outputIndex < output.length; outputIndex++) {
      for (int inputIndex = outputIndex; inputIndex < input.length; inputIndex++) {
        int patternValue = getPatternAtIndex(outputIndex, inputIndex);
        output[outputIndex] += input[inputIndex] * patternValue;
      }
      output[outputIndex] = getFirstDigit(output[outputIndex]);
    }
    return output;
  }

  private static final int[] BASE_PATTERN = new int[] {0, 1, 0, -1};

  private static int getPatternAtIndex(int outputIndex, int inputIndex) {
    final int index = ((inputIndex + 1) / (outputIndex + 1));
    return BASE_PATTERN[index % BASE_PATTERN.length];
  }
}
