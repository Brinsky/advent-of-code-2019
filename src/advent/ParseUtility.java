package advent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtility {
  private static final Pattern INT_PATTERN = Pattern.compile("[+\\-]?\\d+");

  public static <T> List<T> extractValues(
      String s, Pattern pattern, Function<String, T> parseFunction) {
    List<T> values = new ArrayList<>();
    Matcher matcher = pattern.matcher(s);
    while (matcher.find()) {
      values.add(parseFunction.apply(matcher.group()));
    }

    return values;
  }

  public static List<Integer> extractIntegers(String s) {
    return extractValues(s, INT_PATTERN, Integer::parseInt);
  }

  public static int[] extractInts(String s) {
    return extractIntegers(s).stream().mapToInt(Integer::intValue).toArray();
  }

  public static long[] extractLongs(String s) {
    return extractValues(s, INT_PATTERN, Long::parseLong).stream()
        .mapToLong(Long::longValue)
        .toArray();
  }

  /** Returns group #1 from the first match found in the given string */
  public static String firstMatch(String s, Pattern p) {
    Matcher m = p.matcher(s);
    m.find();
    return m.group(1);
  }

  /** Returns group #1 (cast to an int) from the first match found in the given string */
  public static int firstMatchInt(String s, Pattern p) {
    Matcher m = p.matcher(s);
    m.find();
    return Integer.parseInt(m.group(1));
  }

  /** Returns a list of strings matched by capturing groups (i.e. groups #1 and onward). */
  public static List<String> getMatchedGroups(String s, Pattern p) {
    Matcher matcher = p.matcher(s);
    matcher.find();

    List<String> matchedGroups = new ArrayList<>(matcher.groupCount());
    for (int i = 1; i <= matcher.groupCount(); i++) {
      matchedGroups.add(matcher.group(i));
    }

    return matchedGroups;
  }
}
