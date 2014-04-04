package cz.urbangaming.galgs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;
import cz.urbangaming.galgs.GAlg;
import cz.urbangaming.galgs.Scene;

/**
 * All kinds of mess.
 * TODO: Debug, optimize, clean-up...
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
        List<Point2D> vertices = new ArrayList<Point2D>(howMany);
        for (int i = 0; i < howMany; i++) {
            vertices.add(new Point2D((float) randInt(minX, maxX), (float) randInt(minY, maxY)));
        }
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
    public static boolean isInRectangle(Point2D centerPoint, float size, Point2D pointToTry) {
        return pointToTry.x() >= centerPoint.x() - size && pointToTry.x() <= centerPoint.x() + size &&
                pointToTry.y() >= centerPoint.y() - size && pointToTry.y() <= centerPoint.y() + size;
    }

    /**
     * TODO: This is silly, let's merge it with the aforementioned one.
     * 
     * @param pX
     * @param pY
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static boolean insideRect(float pX, float pY, float x, float y, float width, float height) {
        return pX >= x && pX < x + width && pY >= y && pY < y + height;
    }

    /**
     * Converts to our Point2D utility class
     * 
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
        if (area2 < 0) {
            return -1;
        }
        else if (area2 > 0) {
            return +1;
        }
        else {
            return 0;
        }
    }

    /**
     * Are two points on the same chain?
     * TODO: Does it really work? :-D
     * 
     * @param a
     * @param b
     * @param orderedPolygon
     * @return
     */
    public static boolean areOnTheSameChain(Point2D a, Point2D b, List<Point2D> orderedPolygon) {
        Point2D pivot = orderedPolygon.get(orderedPolygon.size() / 2);
        boolean returnValue = false;
        if ((b.x() < pivot.x() && a.x() < pivot.x()) || (b.x() > pivot.x() && a.x() > pivot.x())) {
            returnValue = true;
        }
        Log.d(GAlg.DEBUG_TAG, "Are on the same chain? Pa:" + a + ", Pb:" + b + ", Are they:" + returnValue);
        return returnValue;
    }

    /**
     * This is a brutal force crap used only for some testing.
     * TODO: Remove or improve with space partitioning in future.
     * 
     * @param a
     * @param b
     * @param polygon
     * @return
     */
    public static boolean isItLegalDiagonal(Point2D a, Point2D b, List<Point2D> polygon) {
        boolean isItLegal = true;
        for (int i = 0; i < polygon.size(); i++) {
            if (i < polygon.size() - 1) {
                if (linesIntersect(a, b, polygon.get(i), polygon.get(i + 1)) != null) {
                    isItLegal = false;
                    Log.d(GAlg.DEBUG_TAG, "LINES:(" + a + ";" + b + ") and (" + polygon.get(i) + ";" + polygon.get(i + 1) + ") INTERSECT #A");
                    break;
                }
            } else {
                if (linesIntersect(a, b, polygon.get(i), polygon.get(0)) != null) {
                    isItLegal = false;
                    Log.d(GAlg.DEBUG_TAG, "LINES:(" + a + ";" + b + ") and (" + polygon.get(i) + ";" + polygon.get(0) + ") INTERSECT #B");
                    break;
                }
            }
        }
        return isItLegal;
    }

    /**
     * A suboptimal version of lines intersection.
     * TODO: Enhance with deBerg's sweep line stuff.
     * 
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    private static Point2D linesIntersect(Point2D a, Point2D b, Point2D c, Point2D d) {
        // intersection point
        double intersectX = 0d;
        double intersectY = 0d;

        double destS1X, destS1Y, destS2X, destS2Y;
        destS1X = b.x() - a.x();
        destS1Y = b.y() - a.y();
        destS2X = d.x() - c.x();
        destS2Y = d.y() - c.y();

        double s, t;
        s = (-destS1Y * (a.x() - c.x()) + destS1X * (a.y() - c.y())) / (-destS2X * destS1Y + destS1X * destS2Y);
        t = (destS2X * (a.y() - c.y()) - destS2Y * (a.x() - c.x())) / (-destS2X * destS1Y + destS1X * destS2Y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            intersectX = a.x() + (t * destS1X);
            intersectY = a.y() + (t * destS1Y);
            Log.d(GAlg.DEBUG_TAG, "COLLISION ON POINT:[" + intersectX + "," + intersectY + "]");
            if (((int) intersectX == (int) a.x() && (int) intersectY == (int) a.y()) || ((int) intersectX == (int) b.x() && (int) intersectY == (int) b.y()) || ((int) intersectX == (int) c.x() && (int) intersectY == (int) c.y())
                    || ((int) intersectX == (int) d.x() && (int) intersectY == (int) d.y())) {
                Log.d(GAlg.DEBUG_TAG, "COLLISION IGNORED...");
                return null;
            }
            return new Point2D((float) intersectX, (float) intersectY);
        }

        return null; // No collision
    }

    public static Point2D getMiddlePoint(List<Point2D> points) {
        points.get(points.size() / 2);
        Point2D point = points.get(points.size() / 2);
        return point;
    }

    public static List<Point2D> getLeftPoints(List<Point2D> points) {
        return new ArrayList<Point2D>(points.subList(0, points.size() / 2));
    }

    public static List<Point2D> getRightPoints(List<Point2D> points) {
        return new ArrayList<Point2D>(points.subList(points.size() / 2 + 1, points.size()));
    }
}
