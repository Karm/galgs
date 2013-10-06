package cz.urbangaming.galgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class Utils {
    private static Random rand = new Random();

    public static List<Float> generateSomeVertices(int howMany, int minX, int minY, int maxX, int maxY) {
        // 3? yes, x y z per vertex...
        List<Float> vertices = new ArrayList<Float>(howMany * 3);
        for (int i = 0; i < howMany; i++) {
            Float x = (float) randInt(minX, maxX);
            Float y = (float) randInt(minY, maxY);
            Float z = 0f; // unused...
            vertices.add(x);
            vertices.add(y);
            vertices.add(z);
        }
        // Log.d(GAlg.DEBUG_TAG, "RANDOM VERTICES:"+vertices);
        return vertices;
    }

    public static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean isInRectangle(double centerX, double centerY, double size, double x, double y) {
        return x >= centerX - size && x <= centerX + size &&
                y >= centerY - size && y <= centerY + size;
    }
}
