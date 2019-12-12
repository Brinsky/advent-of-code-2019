package advent;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class ShipComputer {
  private static final int[] POWERS_OF_TEN = {1, 10, 100, 1000, 10000, 100000};

  private final int[] memory;
  private int i = 0; // Instruction pointer

  private final Queue<Integer> input = new ArrayDeque<>();
  private final Queue<Integer> output = new ArrayDeque<>();

  public ShipComputer(int[] program) {
    this.memory = Arrays.copyOf(program, program.length);
  }

  public int readMemeory(int index) {
    return memory[index];
  }

  public void addInput(int value) {
    input.add(value);
  }

  public int removeOutput() {
    return output.remove();
  }

  public boolean hasOutput() {
    return !output.isEmpty();
  }
  
  public int outputSize() {
    return output.size();
  }

  /**
   * Executes the program in memory until the end of the program or until an input instruction for
   * which no input is queued. In the latter case, the program can be resumed from the same position
   * by calling this method again once input has been queued.
   *
   * @return {@code true} if the program is waiting for input, {@code false} if the program has
   *     terminated.
   */
  public boolean execute() {
    while (i >= 0 && i < memory.length) {
      int opCode = memory[i] % 100;

      switch (opCode) {
        case 1: // ADD
          writeParameter(memory, i, 2, readParameter(memory, i, 0) + readParameter(memory, i, 1));
          i += 4;
          break;
        case 2: // MULTIPLY
          writeParameter(memory, i, 2, readParameter(memory, i, 0) * readParameter(memory, i, 1));
          i += 4;
          break;
        case 3: // INPUT
          if (input.isEmpty()) {
            return true;
          }
          writeParameter(memory, i, 0, input.remove());
          i += 2;
          break;
        case 4: // OUT
          output.add(readParameter(memory, i, 0));
          i += 2;
          break;
        case 5: // JUMP IF TRUE
          if (readParameter(memory, i, 0) != 0) {
            i = readParameter(memory, i, 1);
          } else {
            i += 3;
          }
          break;
        case 6: // JUMP IF FALSE
          if (readParameter(memory, i, 0) == 0) {
            i = readParameter(memory, i, 1);
          } else {
            i += 3;
          }
          break;
        case 7: // LESS THAN
          writeParameter(
              memory, i, 2, readParameter(memory, i, 0) < readParameter(memory, i, 1) ? 1 : 0);
          i += 4;
          break;
        case 8: // EQUALS
          writeParameter(
              memory, i, 2, readParameter(memory, i, 0) == readParameter(memory, i, 1) ? 1 : 0);
          i += 4;
          break;
        case 99: // END --- terminates program
          return false;
        default:
          throw new RuntimeException("Unexpected opcode: " + memory[i]);
      }
    }

    throw new RuntimeException("Instruction pointer out of bounds");
  }

  private static void writeParameter(
      int[] memory, int instructionStart, int parameterIndex, int value) {
    final int modes = memory[instructionStart] / 100;
    if ((modes / POWERS_OF_TEN[parameterIndex]) % 10
        != 0) { // If output location parameter is in immediate mode
      throw new RuntimeException("Parameters describing output locations must be in position mode");
    }
    memory[memory[instructionStart + parameterIndex + 1]] = value;
  }

  private static int readParameter(int[] memory, int instructionStart, int parameterIndex) {
    // 0 = position mode, 1 = immediate mode
    final int modes = memory[instructionStart] / 100;
    final int parameter = memory[instructionStart + parameterIndex + 1];
    return ((modes / POWERS_OF_TEN[parameterIndex]) % 10) == 0 ? memory[parameter] : parameter;
  }
}
