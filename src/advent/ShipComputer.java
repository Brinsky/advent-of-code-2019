package advent;

import java.util.*;

public class ShipComputer {
  private static final int[] POWERS_OF_TEN = {1, 10, 100, 1000, 10000, 100000};

  private final Map<Long, Long> memory;

  private long instructionPointer = 0; // Instruction pointer
  private long relativeBase = 0;

  private final Queue<Long> input = new ArrayDeque<>();
  private final Queue<Long> output = new ArrayDeque<>();

  public ShipComputer(int[] program) {
    ;
    memory = new HashMap<>(program.length);
    for (int i = 0; i < program.length; i++) {
      memory.put((long) i, (long) program[i]);
    }
  }

  public ShipComputer(long[] program) {
    memory = new HashMap<>(program.length);
    for (int i = 0; i < program.length; i++) {
      memory.put((long) i, program[i]);
    }
  }

  public long readMemory(long index) {
    if (index < 0) {
      throw new ArrayIndexOutOfBoundsException(
          "Cannot read memory at negative positions: " + index);
    }
    if (memory.containsKey(index)) {
      return memory.get(index);
    }
    return 0;
  }

  public void addInput(long value) {
    input.add(value);
  }

  public long removeOutput() {
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
    while (instructionPointer >= 0) {
      int opCode = (int) (readMemory(instructionPointer) % 100);

      switch (opCode) {
        case 1: // ADD
          writeParameter(2, readParameter(0) + readParameter(1));
          instructionPointer += 4;
          break;
        case 2: // MULTIPLY
          writeParameter(2, readParameter(0) * readParameter(1));
          instructionPointer += 4;
          break;
        case 3: // INPUT
          if (input.isEmpty()) {
            return true;
          }
          writeParameter(0, input.remove());
          instructionPointer += 2;
          break;
        case 4: // OUT
          output.add(readParameter(0));
          instructionPointer += 2;
          break;
        case 5: // JUMP IF TRUE
          if (readParameter(0) != 0) {
            instructionPointer = readParameter(1);
          } else {
            instructionPointer += 3;
          }
          break;
        case 6: // JUMP IF FALSE
          if (readParameter(0) == 0) {
            instructionPointer = readParameter(1);
          } else {
            instructionPointer += 3;
          }
          break;
        case 7: // LESS THAN
          writeParameter(2, readParameter(0) < readParameter(1) ? 1 : 0);
          instructionPointer += 4;
          break;
        case 8: // EQUALS
          writeParameter(2, readParameter(0) == readParameter(1) ? 1 : 0);
          instructionPointer += 4;
          break;
        case 9: // ADJUST RELATIVE BASE
          relativeBase += readParameter(0);
          instructionPointer += 2;
          break;
        case 99: // END --- terminates program
          return false;
        default:
          throw new RuntimeException("Unexpected opcode: " + readMemory(instructionPointer));
      }
    }

    throw new RuntimeException("Instruction pointer out of bounds");
  }

  private void writeParameter(int index, long value) {
    final long parameter = readMemory(instructionPointer + index + 1);
    final int mode = getMode(readMemory(instructionPointer), index);

    switch (mode) {
      case 0: // Position mode
        memory.put(parameter, value);
        return;
      case 1: // Immediate mode
        throw new RuntimeException(
            "Parameters describing output locations must not be in immediate mode");
      case 2: // Relative mode
        memory.put(relativeBase + parameter, value);
        return;
      default:
        throw new RuntimeException("Unknown parameter mode: " + mode);
    }
  }

  private long readParameter(int index) {
    final long parameter = readMemory(instructionPointer + index + 1);
    final int mode = getMode(readMemory(instructionPointer), index);

    switch (mode) {
      case 0: // Position mode
        return readMemory(parameter);
      case 1: // Immediate mode
        return parameter;
      case 2: // Relative mode
        return readMemory(relativeBase + parameter);
      default:
        throw new RuntimeException("Unknown parameter mode: " + mode);
    }
  }

  private static int getMode(long operation, int parameterIndex) {
    return (int) (((operation / 100) / POWERS_OF_TEN[parameterIndex]) % 10);
  }
}
