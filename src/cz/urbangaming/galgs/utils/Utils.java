package cz.urbangaming.galgs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.urbangaming.galgs.Scene;

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
    public static List<Point2D> generateSomeVertices(int howMany, int minX, int minY, int maxX, int maxY) {
        
          Point2D[] hhh = new Point2D[] {new Point2D(115.0f, 156.0f), new Point2D(137.0f, 367.0f), new Point2D(262.0f, 440.0f), new Point2D(521.0f, 441.0f), new Point2D(624.0f, 250.0f), new Point2D(476.0f, 19.0f)};
        //Point2D[] hhh = new Point2D[] {new Point2D(25.0f, 416.0f), new Point2D(167.0f, 248.0f), new Point2D(359.0f, 118.0f), new Point2D(629.0f, 80.0f), new Point2D(871.0f, 173.0f)};

                List<Point2D> hoo = new ArrayList<Point2D>();
                for (Point2D point2d : hhh) {
                    hoo.add(point2d);
                }
return hoo;
/*
        List<Point2D> vertices = new ArrayList<Point2D>(howMany);
        for (int i = 0; i < howMany; i++) {
            vertices.add(new Point2D((float) randInt(minX, maxX), (float) randInt(minY, maxY)));
        }
        // Log.d(GAlg.DEBUG_TAG, "RANDOM VERTICES:"+vertices);
        return vertices;
        */
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
    public static boolean isInRectangle(Point2D centerPoint, float size, Point2D pointToTry) {
        return pointToTry.x() >= centerPoint.x() - size && pointToTry.x() <= centerPoint.x() + size &&
                pointToTry.y() >= centerPoint.y() - size && pointToTry.y() <= centerPoint.y() + size;
    }

    /**
     * Converts to our Point2D utility class
     * @param scenePoints
     * @return
     */
    public static float[] pointVectorToArray(List<Point2D> scenePoints) {
        float[] ret = new float[scenePoints.size() * Scene.COORDS_PER_VERTEX];
        int i = 0;
        for (Point2D point : scenePoints) {
            ret[i] = point.x();
            ret[i + 1] = point.y();
            ret[i + 2] = 0f;
            i += 3;
        }
        return ret;
    }

    /**
     * Is a->b->c a counterclockwise turn?
     * 
     * @param a
     *            first point
     * @param b
     *            second point
     * @param c
     *            third point
     * @return { -1, 0, +1 } if a->b->c is a { clockwise, collinear; counterclocwise } turn.
     */
    public static int ccw(Point2D a, Point2D b, Point2D c) {
        double area2 = (b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x());
        if (area2 < 0)
            return -1;
        else if (area2 > 0)
            return +1;
        else
            return 0;
    }
}
