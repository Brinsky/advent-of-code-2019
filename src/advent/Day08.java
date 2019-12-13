package advent;

import java.io.IOException;
import java.util.function.Function;

public class Day08 {
  private static final int IMAGE_WIDTH = 25;
  private static final int IMAGE_HEIGHT = 6;
  private static final int PIXELS_PER_LAYER = IMAGE_HEIGHT * IMAGE_WIDTH;

  private static final char CHAR_BLACK = ' ';
  private static final char CHAR_WHITE = '#';
  private static final char CHAR_TRANSPARENT = 'X';

  public static void main(String[] args) throws IOException {
    int[] encodedImage =
        FileUtility.fileToString("input/08.txt").chars().map(c -> c - '0').toArray();
    Integer[][][] layers = getLayers(encodedImage);

    // Part one
    Integer[][] fewestZeroes = getLayerWithMinValue(layers, layer -> count(layer, 0));
    FileUtility.printAndOutput(count(fewestZeroes, 1) * count(fewestZeroes, 2), "output/08a.txt");

    // Part two
    FileUtility.printAndOutput(
        DataUtility.matrixToString(composeLayers(layers), Day08::getColor), "output/08b.txt");
  }

  private static Integer[][] getLayerWithMinValue(
      Integer[][][] layers, Function<Integer[][], Integer> valueFunction) {
    Integer[][] bestLayer = null;
    int minValue = Integer.MAX_VALUE;

    for (Integer[][] layer : layers) {
      int value = valueFunction.apply(layer);
      if (value < minValue) {
        minValue = value;
        bestLayer = layer;
      }
    }

    return bestLayer;
  }

  private static Integer[][][] getLayers(int[] encodedImage) {
    Integer[][][] layers =
        new Integer[encodedImage.length / PIXELS_PER_LAYER][IMAGE_WIDTH][IMAGE_HEIGHT];

    for (int layer = 0; layer < layers.length; layer++) {
      for (int x = 0; x < IMAGE_WIDTH; x++) {
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
          layers[layer][x][y] = encodedImage[layer * PIXELS_PER_LAYER + y * IMAGE_WIDTH + x];
        }
      }
    }

    return layers;
  }

  private static Integer[][] composeLayers(Integer[][][] layers) {
    Integer[][] image = new Integer[IMAGE_WIDTH][IMAGE_HEIGHT];
    DataUtility.fillMatrix(image, 2); // Start with only transparent pixels

    for (int layer = 0; layer < layers.length; layer++) {
      for (int x = 0; x < IMAGE_WIDTH; x++) {
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
          // Only overwrite the existing pixel if it was transparent
          if (image[x][y].equals(2)) {
            image[x][y] = layers[layer][x][y];
          }
        }
      }
    }

    return image;
  }

  private static <T> int count(T[][] layer, T value) {
    int count = 0;
    for (int x = 0; x < layer.length; x++) {
      for (int y = 0; y < layer[x].length; y++) {
        if (layer[x][y].equals(value)) {
          count++;
        }
      }
    }
    return count;
  }

  private static char getColor(int digit) {
    switch (digit) {
      case 0:
        return CHAR_BLACK;
      case 1:
        return CHAR_WHITE;
      case 2:
        return CHAR_TRANSPARENT;
      default:
        return '?';
    }
  }
}
