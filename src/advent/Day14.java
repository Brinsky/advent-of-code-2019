package advent;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day14 {

  public static final String ORE = "ORE";
  public static final String FUEL = "FUEL";
  public static final long AVAILABLE_ORE = 1_000_000_000_000L;

  public static void main(String[] args) throws IOException {
    Map<String, Reaction> reactionsByOutput =
        Arrays.stream(FileUtility.fileToString("input/14.txt").split("\n"))
            .map(Reaction::new)
            .collect(Collectors.toUnmodifiableMap(r -> r.output.chemical, Function.identity()));
    Map<String, Long> leftoversPerUnitFuel =
        reactionsByOutput.values().stream()
            .map(r -> r.output.chemical)
            .collect(Collectors.toMap(Function.identity(), r -> 0L));

    // Part one
    long minOrePerUnitFuel = minimumOre(reactionsByOutput, leftoversPerUnitFuel);
    FileUtility.printAndOutput(minOrePerUnitFuel, "output/14a.txt");

    // Part two
    FileUtility.printAndOutput(
        maximumFuel(AVAILABLE_ORE, reactionsByOutput, minOrePerUnitFuel, leftoversPerUnitFuel),
        "output/14b.txt");
  }

  private static long maximumFuel(
      long availableOre,
      Map<String, Reaction> reactionsByOutput,
      final long minOrePerUnitFuel,
      final Map<String, Long> leftoversPerUnitFuel) {
    long totalFuel = 0;
    Map<String, Long> leftovers = newLeftoversMap(reactionsByOutput);
    do {
      // Produce as much fuel as the current amount of ore allows
      long fuel = availableOre / minOrePerUnitFuel;
      totalFuel += fuel;
      availableOre %= minOrePerUnitFuel;

      // Aggregate leftovers and undo as many reactions as possible in order to recoup ore. Note
      // that this operation may not zero out the leftovers, so we have to carry them over between
      // iterations.
      addMultipleOfLeftovers(leftovers, leftoversPerUnitFuel, fuel);
      availableOre += undoExtraReactions(reactionsByOutput, leftovers);
    } while (availableOre > minOrePerUnitFuel);

    return totalFuel;
  }

  private static long minimumOre(
      Map<String, Reaction> reactionsByOutput, Map<String, Long> leftovers) {
    return unreducedOre(new Quantity(FUEL, 1), reactionsByOutput, leftovers)
        - undoExtraReactions(reactionsByOutput, leftovers);
  }

  private static long undoExtraReactions(
      Map<String, Reaction> reactionsByOutput, Map<String, Long> leftovers) {
    long oreRegained = 0;
    boolean stillUndoing;
    do {
      stillUndoing = false;
      for (String chemical : leftovers.keySet()) {
        final long amount = leftovers.get(chemical);
        if (amount == 0) {
          continue;
        }

        Reaction reaction = reactionsByOutput.get(chemical);
        // The maximum number of times the corresponding reaction could be "undone"
        final long multiple = amount / reaction.output.amount;
        // How much of this chemical is left after undoing the reaction that many times
        final long remainder = amount - (reaction.output.amount * multiple);
        if (multiple == 0) {
          continue;
        }

        stillUndoing = true;
        leftovers.put(chemical, remainder);

        if (reaction.isOreReaction()) {
          oreRegained += reaction.getOnlyInput().amount * multiple;
        } else {
          for (Quantity input : reaction.inputs) {
            addToLeftovers(leftovers, input.chemical, input.amount * multiple);
          }
        }
      }

    } while (stillUndoing);

    return oreRegained;
  }

  private static int unreducedOre(
      Quantity currentOutput,
      Map<String, Reaction> reactionsByOutput,
      Map<String, Long> leftovers) {
    if (currentOutput.chemical.equals(ORE)) {
      return currentOutput.amount;
    }

    Reaction reaction = reactionsByOutput.get(currentOutput.chemical);

    // How many times the reaction must be performed to produce the smallest amount of output that
    // is greater than or equal to the current quantity
    final int multiple =
        (currentOutput.amount / reaction.output.amount)
            + (currentOutput.amount % reaction.output.amount == 0 ? 0 : 1);
    final int leftoverAmount = reaction.output.amount * multiple - currentOutput.amount;

    addToLeftovers(leftovers, currentOutput.chemical, leftoverAmount);

    int ore = 0;
    for (Quantity input : reaction.inputs) {
      ore +=
          unreducedOre(
              new Quantity(input.chemical, input.amount * multiple), reactionsByOutput, leftovers);
    }

    return ore;
  }

  private static void addMultipleOfLeftovers(
      Map<String, Long> destination, Map<String, Long> toMultiply, long multiple) {
    for (String chemical : toMultiply.keySet()) {
      addToLeftovers(destination, chemical, toMultiply.get(chemical) * multiple);
    }
  }

  private static void addToLeftovers(Map<String, Long> leftovers, String chemical, long amount) {
    leftovers.put(chemical, leftovers.get(chemical) + amount);
  }

  private static Map<String, Long> newLeftoversMap(Map<String, Reaction> reactionsByOutput) {
    return reactionsByOutput.values().stream()
        .map(r -> r.output.chemical)
        .collect(Collectors.toMap(Function.identity(), r -> 0L));
  }

  private static class Reaction {
    public final Set<Quantity> inputs;
    public final Quantity output;

    public Reaction(String s) {
      String[] quantities = s.split("(, | => )");
      inputs =
          Arrays.stream(quantities)
              .limit(quantities.length - 1)
              .map(Quantity::new)
              .collect(Collectors.toUnmodifiableSet());
      output = new Quantity(quantities[quantities.length - 1]);
    }

    public boolean isOreReaction() {
      return inputs.size() == 1 && getOnlyInput().chemical.equals(ORE);
    }

    public Quantity getOnlyInput() {
      if (inputs.size() != 1) {
        throw new RuntimeException("Cannot get only input when # of inputs is " + inputs.size());
      }
      return inputs.iterator().next();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Reaction)) {
        return false;
      }

      Reaction other = (Reaction) o;
      return this.inputs.equals(other.inputs) && this.output.equals(other.output);
    }

    @Override
    public int hashCode() {
      return Objects.hash(inputs, output);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      for (Quantity q : inputs) {
        builder.append(q).append(", ");
      }
      builder.delete(builder.length() - 2, builder.length());
      builder.append(" => ").append(output);

      return builder.toString();
    }
  }

  private static class Quantity {
    public final String chemical;
    public final int amount;

    private Quantity(String chemical, int amount) {
      this.chemical = chemical;
      this.amount = amount;
    }

    private Quantity(String s) {
      String[] parameters = s.split(" ");
      amount = Integer.parseInt(parameters[0]);
      chemical = parameters[1];
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Quantity)) {
        return false;
      }

      Quantity other = (Quantity) o;
      return this.chemical.equals(other.chemical) && this.amount == other.amount;
    }

    @Override
    public int hashCode() {
      return Objects.hash(chemical, amount);
    }

    @Override
    public String toString() {
      return amount + " " + chemical;
    }
  }
}
