package cz.urbangaming.galgs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import android.util.Log;
import cz.urbangaming.galgs.utils.Point2D;
import cz.urbangaming.galgs.utils.PolarOrderComparator;
import cz.urbangaming.galgs.utils.Utils;
import cz.urbangaming.galgs.utils.XYOrderComparator;
import cz.urbangaming.galgs.utils.YXOrderComparator;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class Algorithms {

    /**
     * Convex Hull with Gift Wrapping
     * 
     * @param vertices
     * @return
     */
    public List<Point2D> convexHullGiftWrapping(List<Point2D> points) {
        if (points.size() > 3) {
            Collections.sort(points, new YXOrderComparator());
            // isn't it rather clumsy?
            Point2D vertexOnTheHull = points.get(points.size() - 1);
            List<Point2D> verticesOnHull = new ArrayList<Point2D>();
            Point2D currentVertex;
            do {
                verticesOnHull.add(vertexOnTheHull);
                currentVertex = points.get(0);
                for (int i = 1; i < points.size(); i++) {
                    Point2D nextVertex = points.get(i);
                    if (currentVertex.equals(vertexOnTheHull) || Utils.ccw(nextVertex, vertexOnTheHull, currentVertex) == 1) {
                        currentVertex = nextVertex;
                    }
                }
                vertexOnTheHull = currentVertex;
            } while (!currentVertex.equals(verticesOnHull.get(0)));
            // Log.d(GAlg.DEBUG_TAG, "RIGHTMOST BOTTOM:" + vertexOnHull);
            return verticesOnHull;
        } else {
            return points;
        }
    }

    /**
     * Convex hull with Graham's scan
     * Source: Graphics Gems V., Alan W. Paeth, 1995
     * 
     * @param vertices
     * @return
     */
    public List<Point2D> convexHullGrahamScan(List<Point2D> vertices) {
        Deque<Point2D> verticesOnHull = new ArrayDeque<Point2D>();
        Collections.sort(vertices, new YXOrderComparator());
        Collections.sort(vertices, new PolarOrderComparator(vertices.get(0)));
        verticesOnHull.push(vertices.get(0));
        // find index firstPointNotEqual of first point not equal to points[0]
        int firstPointNotEqual;
        for (firstPointNotEqual = 1; firstPointNotEqual < vertices.size(); firstPointNotEqual++) {
            if (!vertices.get(0).equals(vertices.get(firstPointNotEqual))) {
                break;
            }
        }
        if (firstPointNotEqual == vertices.size()) {
            return null; // all points are equal
        }
        // find index k2 of first point not collinear with points[0] and points[firstPointNotEqual]
        int k2;
        for (k2 = firstPointNotEqual + 1; k2 < vertices.size(); k2++) {
            if (Utils.ccw(vertices.get(0), vertices.get(firstPointNotEqual), vertices.get(k2)) != 0) {
                break;
            }
        }
        verticesOnHull.push(vertices.get(k2 - 1));
        // Graham scan; note that points[N-1] is extreme point different from points[0]
        for (int i = k2; i < vertices.size(); i++) {
            Point2D top = verticesOnHull.pop();
            while (Utils.ccw(verticesOnHull.peek(), top, vertices.get(i)) <= 0) {
                top = verticesOnHull.pop();
            }
            verticesOnHull.push(top);
            verticesOnHull.push(vertices.get(i));
        }
        return new ArrayList<Point2D>(verticesOnHull);
    }

    public boolean linesIntersect(double pointAx, double pointAy, double pointBx, double pointBy, double pointCx, double pointCy, double pointDx, double pointDy) {
        // intersection point
        double intersectX = 0d;
        double intersectY = 0d;

        double destS1X, destS1Y, destS2X, destS2Y;
        destS1X = pointBx - pointAx;
        destS1Y = pointBy - pointAy;
        destS2X = pointDx - pointCx;
        destS2Y = pointDy - pointCy;

        double s, t;
        s = (-destS1Y * (pointAx - pointCx) + destS1X * (pointAy - pointCy)) / (-destS2X * destS1Y + destS1X * destS2Y);
        t = (destS2X * (pointAy - pointCy) - destS2Y * (pointAx - pointCx)) / (-destS2X * destS1Y + destS1X * destS2Y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            intersectX = pointAx + (t * destS1X);
            intersectY = pointAy + (t * destS1Y);
            Log.d(GAlg.DEBUG_TAG, "COLLISION ON POINT:[" + intersectX + "," + intersectY + "]");
            if (((int) intersectX == (int) pointAx && (int) intersectY == (int) pointAy) || ((int) intersectX == (int) pointBx && (int) intersectY == (int) pointBy) || ((int) intersectX == (int) pointCx && (int) intersectY == (int) pointCy)
                    || ((int) intersectX == (int) pointDx && (int) intersectY == (int) pointDy)) {
                Log.d(GAlg.DEBUG_TAG, "COLLISION IGNORED...");
                return false;
            }
            return true;
        }

        return false; // No collision
    }

    public boolean linesIntersect(Point2D a, Point2D b, Point2D c, Point2D d) {
        return linesIntersect(a.x(), a.y(), b.x(), b.y(), c.x(), c.y(), d.x(), d.y());
    }

    /**
     * Useless call, remove...
     * 
     * @param vertices
     * @return
     */
    public List<Point2D> linkedPoints(List<Point2D> vertices) {
        return vertices;
    }

    public boolean isItLegalDiagonal(Point2D a, Point2D b, List<Point2D> polygon) {
        boolean isItLegal = true;
        for (int i = 0; i < polygon.size(); i++) {
            if (i < polygon.size() - 1) {
                if (linesIntersect(a, b, polygon.get(i), polygon.get(i + 1))) {
                    isItLegal = false;
                    Log.d(GAlg.DEBUG_TAG, "LINES:(" + a + ";" + b + ") and (" + polygon.get(i) + ";" + polygon.get(i + 1) + ") INTERSECT #A");
                    break;
                }
            } else {
                if (linesIntersect(a, b, polygon.get(i), polygon.get(0))) {
                    isItLegal = false;
                    Log.d(GAlg.DEBUG_TAG, "LINES:(" + a + ";" + b + ") and (" + polygon.get(i) + ";" + polygon.get(0) + ") INTERSECT #B");
                    break;
                }
            }
        }
        return isItLegal;
    }

    /**
     * Are two points on the same chain?
     * 
     * @param a
     * @param b
     * @param orderedPolygon
     * @return
     */
    public boolean areOnTheSameChain(Point2D a, Point2D b, List<Point2D> orderedPolygon) {
        Point2D pivot = orderedPolygon.get(orderedPolygon.size() / 2);
        boolean returnValue = false;
        if ((b.x() < pivot.x() && a.x() < pivot.x()) || (b.x() > pivot.x() && a.x() > pivot.x())) {
            returnValue = true;
        }
        Log.d(GAlg.DEBUG_TAG, "Are on the same chain? Pa:" + a + ", Pb:" + b + ", Are they:" + returnValue);
        return returnValue;
    }

    /**
     * It still doesn't work properly :-(
     * Not even for some convex polygons!
     * Debug in progress...
     * 
     * Ffffffffffffuuuuuuuuuuuuuuuuu!
     * 
     * @param V
     * @return
     */
    public List<Point2D> sweepTriangulation(List<Point2D> V) {
        List<Point2D> originalV = new ArrayList<Point2D>(V);
        Collections.sort(V, new XYOrderComparator());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Deque<Point2D> stack = new ArrayDeque<Point2D>();
        Log.d(GAlg.DEBUG_TAG, "Sorted V:" + V.toString());
        stack.push(V.get(0));
        stack.push(V.get(1));
        for (int i = 2; i < V.size(); i++) {
            Point2D p = V.get(i);
            if (areOnTheSameChain(p, stack.getFirst(), V)) {
                Log.d(GAlg.DEBUG_TAG, "Entered Case1");
                Point2D tempTop = stack.pop();
                while (!stack.isEmpty() && isItLegalDiagonal(p, tempTop, originalV)) {
                    Log.d(GAlg.DEBUG_TAG, "Case1 While loop");
                    triangles.add(p);
                    triangles.add(tempTop);
                    tempTop = stack.pop();
                }
                stack.push(p);
            } else {
                Log.d(GAlg.DEBUG_TAG, "Entered Case2");
                Point2D top = stack.pop();
                triangles.add(p);
                triangles.add(top);
                while (!stack.isEmpty()) {
                    Log.d(GAlg.DEBUG_TAG, "Case2 While loop");
                    triangles.add(p);
                    triangles.add(stack.pop());
                }
                stack.push(top);
                stack.push(p);
            }
        }

        //TODO: This is wrong :-)
        //Add boundary
        for (int i = 1; i < originalV.size(); i++) {
            triangles.add(originalV.get(i));
            triangles.add(originalV.get(i - 1));
        }
        //First and last from boundary
        triangles.add(originalV.get(0));
        triangles.add(originalV.get(originalV.size() - 1));
        return triangles;
    }

    /**
     * This method works, but it's fiendishly suboptimal...
     * 
     * @param V
     * @return
     */
    public List<Point2D> naiveTriangulation(List<Point2D> V) {
        Collections.sort(V, new XYOrderComparator());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Log.d(GAlg.DEBUG_TAG, "Sorted V:" + V.toString());
        triangles.add(V.get(0));
        triangles.add(V.get(1));
        for (int i = 2; i < V.size(); i++) {
            for (int j = 1; j <= i; j++) {
                if (isItLegalDiagonal(V.get(i), V.get(i - j), triangles)) {
                    triangles.add(V.get(i));
                    triangles.add(V.get(i - j));
                }
            }
        }
        return triangles;
    }
}
