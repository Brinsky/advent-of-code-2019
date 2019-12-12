package advent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day06 {

  public static final String COM = "COM";
  public static final String YOU = "YOU";
  public static final String SAN = "SAN";

  public static void main(String[] args) throws IOException {
    String[] orbits = FileUtility.fileToString("input/06.txt").split("\n");

    Map<String, OrbitalObject> orbitalObjects = new HashMap<>();

    for (String orbit : orbits) {
      String[] objects = orbit.split("\\)");
      OrbitalObject parent = getOrCreate(objects[0], orbitalObjects);
      OrbitalObject child = getOrCreate(objects[1], orbitalObjects);

      parent.children.add(child);
      child.parent = parent;
    }

    OrbitalObject root = orbitalObjects.get(COM);

    // Part one
    FileUtility.printAndOutput(orbitalChecksum(root, 0), "output/06a.txt");

    // Part two
    FileUtility.printAndOutput(
        minDistance(orbitalObjects.get(YOU).parent, orbitalObjects.get(SAN).parent),
        "output/06b.txt");
  }

  private static int minDistance(OrbitalObject a, OrbitalObject b) {
    if (a.name.equals(b.name)) {
      return 0;
    }
    Map<String, Integer> ancestors = new HashMap<>();

    // Cache the names of all ancestors from object A to the root (along with how far from A they
    // are)
    OrbitalObject current = a;
    int transfers = 0;
    do {
      ancestors.put(current.name, transfers);
      current = current.parent;
      transfers++;
    } while (current != null);

    // Check each parent of object B to find the first one in the ancestor set of object A
    current = b;
    transfers = 0;
    do {
      if (ancestors.containsKey(current.name)) {
        return ancestors.get(current.name) + transfers;
      }
      current = current.parent;
      transfers++;
    } while (current != null);

    throw new RuntimeException("No path found between provided objects");
  }

  private static int orbitalChecksum(OrbitalObject root, int currentDepth) {
    int sum = 0;
    for (OrbitalObject child : root.children) {
      sum += orbitalChecksum(child, currentDepth + 1);
    }

    return currentDepth + sum;
  }

  private static OrbitalObject getOrCreate(String name, Map<String, OrbitalObject> objects) {
    if (objects.containsKey(name)) {
      return objects.get(name);
    }
    OrbitalObject orbitalObject = new OrbitalObject(name);
    objects.put(name, orbitalObject);
    return orbitalObject;
  }

  private static class OrbitalObject {
    public final String name;
    public OrbitalObject parent;
    public final List<OrbitalObject> children = new ArrayList<>();

    public OrbitalObject(String name) {
      this.name = name;
    }
  }
}
