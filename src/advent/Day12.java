package advent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 {
  private static final int ITERATIONS = 1000;
  private static final int NUM_COMPONENTS = 3;

  public static void main(String[] args) throws IOException {
    List<Moon> initial =
        Arrays.stream(FileUtility.fileToString("input/12.txt").split("\n"))
            .map(Moon::new)
            .collect(Collectors.toList());

    // Part one
    List<Moon> state = initial.stream().map(Moon::copy).collect(Collectors.toList());
    step(state, ITERATIONS);
    FileUtility.printAndOutput(state.stream().mapToInt(Moon::totalEnergy).sum(), "output/12a.txt");

    // Part two
    // The key insight here is to focus on each component (X, Y, Z) individually and find the time
    // at which it first becomes periodic, then to cross reference these periods to compute the time
    // at which the whole system will become periodic.
    // We assume that we are only looking for each component to repeat the *initial* state, since
    // all inputs observed so far behave this way (i.e. the first repeated state is just the first
    // state) and because it greatly simplifies finding the period of the overall system (it is just
    // the LCM of the periods of each component). The code will throw an exception if this
    // assumption is false.
    int[] period = IntStream.range(0, NUM_COMPONENTS).map(c -> findPeriod(initial, c)).toArray();
    FileUtility.printAndOutput(MathUtility.lcm(period), "output/12b.txt");
  }

  private static int findPeriod(List<Moon> initial, int component) {
    Map<Tuple, Integer> pastSlices = new HashMap<>();
    List<Moon> moons = initial.stream().map(Moon::copy).collect(Collectors.toList());
    int iteration = 0;

    while (!pastSlices.containsKey(getSlice(moons, component))) {
      pastSlices.put(getSlice(moons, component), iteration);
      step(moons);
      iteration++;
    }

    final int stateRepeated = pastSlices.get(getSlice(moons, component));
    if (stateRepeated != 0) {
      throw new RuntimeException("");
    }

    return iteration - stateRepeated;
  }

  /**
   * Produces a "slice" - a tuple that contains the position and velocity values for a the specified
   * component (i.e. X, Y, or Z) of each moon. E.g. (p1x, p2x, ..., pnx, v1x, v2x, ..., vnx).
   */
  private static Tuple getSlice(List<Moon> moons, int component) {
    Tuple combined = Tuple.zero(moons.size() * 2);
    for (int i = 0; i < moons.size(); i++) {
      combined.set(i, moons.get(i).position.get(component));
      // Velocity values go in the second half of the tuple
      combined.set(i + moons.size(), moons.get(i).velocity.get(component));
    }
    return combined;
  }

  private static int energy(Tuple t) {
    int sum = 0;
    for (int i = 0; i < t.length(); i++) {
      sum += Math.abs(t.get(i));
    }
    return sum;
  }

  private static void step(List<Moon> moons, int times) {
    for (int i = 0; i < times; i++) {
      step(moons);
    }
  }

  private static void step(List<Moon> moons) {
    for (Moon moonA : moons) {
      for (Moon moonB : moons) {
        if (moonA != moonB) {
          moonA.applyGravity(moonB);
        }
      }
    }
    for (Moon moon : moons) {
      moon.step();
    }
  }

  private static class Moon {
    public final Tuple position;
    public final Tuple velocity;

    public Moon(String s) {
      position = new Tuple(ParseUtility.extractInts(s));
      velocity = Tuple.zero(position.length());
    }

    public Moon(Tuple position, Tuple velocity) {
      this.position = position.copy();
      this.velocity = velocity.copy();
    }

    public void applyGravity(Moon other) {
      for (int i = 0; i < 3; i++) {
        int diff = other.position.get(i) - this.position.get(i);
        if (diff != 0) {
          this.velocity.add(i, diff / Math.abs(diff));
        }
      }
    }

    public void step() {
      position.plusEquals(velocity);
    }

    public int totalEnergy() {
      return energy(position) * energy(velocity);
    }

    @Override
    public String toString() {
      return position.toString()
          + " ["
          + energy(position)
          + "] "
          + velocity.toString()
          + " ["
          + energy(velocity)
          + "] <"
          + totalEnergy()
          + ">";
    }

    @Override
    public int hashCode() {
      return position.hashCode() + 7 * velocity.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Moon)) {
        return false;
      }

      Moon other = (Moon) o;
      return this.position.equals(other.position) && this.velocity.equals(other.velocity);
    }

    public Moon copy() {
      return new Moon(position, velocity);
    }
  }

  // Mutable
  private static class Tuple {
    private final int[] tuple;

    public Tuple(int... components) {
      this.tuple = Arrays.copyOf(components, components.length);
    }

    public static Tuple zero(int length) {
      return new Tuple(new int[length]);
    }

    public Tuple plusEquals(Tuple other) {
      if (this.length() != other.length()) {
        throw new RuntimeException(
            "Expected " + this.length() + " components, but got " + other.length());
      }
      for (int i = 0; i < tuple.length; i++) {
        set(i, get(i) + other.get(i));
      }
      return this;
    }

    public void add(int index, int value) {
      tuple[index] += value;
    }

    public void set(int index, int value) {
      tuple[index] = value;
    }

    public int get(int index) {
      return tuple[index];
    }

    public int length() {
      return tuple.length;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder("(");

      for (int i = 0; i < length(); i++) {
        builder.append(tuple[i]);
        builder.append((i < length() - 1) ? ", " : ")");
      }

      return builder.toString();
    }

    @Override
    public int hashCode() {
      int hash = 0;
      int powerOfTen = 1;
      for (int i = 0; i < length(); i++) {
        hash += tuple[i] * powerOfTen;
        powerOfTen *= 10;
      }
      return hash;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Tuple)) {
        return false;
      }

      Tuple other = (Tuple) o;
      if (other.length() != this.length()) {
        return false;
      }
      for (int i = 0; i < this.length(); i++) {
        if (other.get(i) != this.get(i)) {
          return false;
        }
      }
      return true;
    }

    public Tuple copy() {
      return new Tuple(tuple);
    }
  }
}
