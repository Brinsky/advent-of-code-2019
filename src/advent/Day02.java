package advent;

import java.io.IOException;
import java.util.Arrays;

public class Day02 {
  private static final int TARGET_VALUE = 19690720; // Provided in Day 2, Part 2 problem description

  public static void main(String[] args) throws IOException {
    int[] program = ParseUtility.extractInts(FileUtility.fileToString("input/02.txt"));

    // Part one
    FileUtility.printAndOutput(runProgram(program, 12, 2), "output/02a.txt");

    // Part two
    FileUtility.printAndOutput(searchNounVerbSpace(program, TARGET_VALUE), "output/02b.txt");
  }

  private static int runProgram(int[] program, int noun, int verb) {
    int[] memory = Arrays.copyOf(program, program.length);
    memory[1] = noun;
    memory[2] = verb;

    int i = 0; // Instruction pointer
    while (i >= 0 && i < memory.length) {
      switch (memory[i]) {
        case 1: // ADD $1 $2 $3 --- m[$3] = m[$1] + m[$2]
          memory[memory[i + 3]] = memory[memory[i + 1]] + memory[memory[i + 2]];
          i += 4;
          break;
        case 2: // MUL $1 $2 $3 --- m[$3] = m[$1] * m[$2]
          memory[memory[i + 3]] = memory[memory[i + 1]] * memory[memory[i + 2]];
          i += 4;
          break;
        case 99: // END --- terminates program
          return memory[0];
        default:
          throw new RuntimeException("Unexpected opcode: " + memory[i]);
      }
    }

    throw new RuntimeException("Instruction pointer out of bounds");
  }

  private static final int MIN_NOUN_VERB = 0;
  private static final int MAX_NOUN_VERB = 99;

  private static int searchNounVerbSpace(int[] program, int targetValue) {
    for (int noun = MIN_NOUN_VERB; noun <= MAX_NOUN_VERB; noun++) {
      for (int verb = MIN_NOUN_VERB; verb <= MAX_NOUN_VERB; verb++) {
        if (runProgram(program, noun, verb) == targetValue) {
          return hash(noun, verb);
        }
      }
    }

    throw new RuntimeException("Target value not found");
  }

  private static int hash(int noun, int verb) {
    return 100 * noun + verb;
  }
}
