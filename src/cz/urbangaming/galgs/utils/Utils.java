package cz.urbangaming.galgs.utils;

import java.util.ArrayList;
import java.util.Comparator;
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

    /**
     * Generates random vertices
     * 
     * @param howMany
     * @param minX
     *            boundary
     * @param minY
     *            boundary
     * @param maxX
     *            boundary
     * @param maxY
     *            boundary
     * @return
     */
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

    /**
     * Random int within limits
     * 
     * @param min
     * @param max
     * @return
     */
    public static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Determines whether the given x,y point lies within the rectangle determined by center and size.
     * 
     * @param centerX
     * @param centerY
     * @param size
     * @param x
     * @param y
     * @return
     */
    public static boolean isInRectangle(double centerX, double centerY, double size, double x, double y) {
        return x >= centerX - size && x <= centerX + size &&
                y >= centerY - size && y <= centerY + size;
    }

    /**
     * Given the [0,0] being in the top-left corner of the screen, this returns the right-bottom most point.
     * 
     * @param vertices
     * @return
     */
    public static List<Float> maxYMaxX(List<Float> vertices) {
        if (vertices.size() > 1) {
            List<Float> vertex = new ArrayList<Float>(3);
            vertex.addAll(vertices.subList(0, 3));
            for (int i = 3; i < vertices.size(); i += 3) {
                if (vertices.get(i) > vertex.get(0) && vertices.get(i + 1) > vertex.get(1)) {
                    vertex.set(0, vertices.get(i));
                    vertex.set(1, vertices.get(i + 1));
                    vertex.set(2, vertices.get(i + 2));
                }
            }
            return vertex;
        } else {
            return vertices;
        }
    }

    /**
     * Determines whether the third vertex lies on the left or right side of the 
     * line determined by the first two vertices. The third option is that all 
     * the three vertices lies on the same line = they are collinear.
     * 
     * @param threeVertices
     * @return
     */
    public static int position(List<Float> threeVertices) {
        if (threeVertices.size() < 9) {
            throw new IllegalArgumentException("3 vertices expected");
        } else {
            float vX = threeVertices.get(0);
            float vY = threeVertices.get(1);
            // float vZ = threeVertices.get(2);

            float v1X = threeVertices.get(3);
            float v1Y = threeVertices.get(4);
            // float v1Z = threeVertices.get(5);

            float v2X = threeVertices.get(6);
            float v2Y = threeVertices.get(7);
            // float v2Z = threeVertices.get(8);

            // Matrix determinant
            // Hope not to see any rounding error on Floats :-)
            int orientation = (int) ((v2X - v1X) * (vY - v1Y) - (vX - v1X) * (v2Y - v1Y));

            if (orientation > 0)
                return -1; //left
            if (orientation < 0)
                return 1; //right
            return 0; //on the line
        }
    }
    
    
    
    
    /**
     * Returns the angle between this point and that point.
     * @return the angle in radians (between -pi and pi) between this point and that point (0 if equal)
     */
    public static double angleTo(Point2D thisOne, Point2D thatOne) {
        double dx = thatOne.x()  - thisOne.x() ;
        double dy = thatOne.y()  - thisOne.y() ;
        return Math.atan2(dy, dx);
    }
 
  
   
    /**
     * Is a->b->c a counterclockwise turn?
     * @param a first point
     * @param b second point
     * @param c third point
     * @return { -1, 0, +1 } if a->b->c is a { clockwise, collinear; counterclocwise } turn.
     */
    public static int ccw(Point2D a, Point2D b, Point2D c) {
        double area2 = (b.x()-a.x())*(c.y()-a.y()) - (b.y()-a.y())*(c.x()-a.x());
        if      (area2 < 0) return -1;
        else if (area2 > 0) return +1;
        else                return  0;
    }
}
