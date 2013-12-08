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

    /**
     * Useless call, remove...
     * 
     * @param vertices
     * @return
     */
    public List<Point2D> linkedPoints(List<Point2D> vertices) {
        return vertices;
    }

    public List<Point2D> sweepTriangulation(List<Point2D> V) {
        // Sort vertices
        List<Point2D> originalV = V;
        Log.d(GAlg.DEBUG_TAG, "UNsorted V:" + V.toString());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Collections.sort(V, new XYOrderComparator());
        Log.d(GAlg.DEBUG_TAG, "Sorted V:" + V.toString());
        Deque<Point2D> stack = new ArrayDeque<Point2D>();
        stack.push(V.get(0));
        stack.push(V.get(1));
        triangles.add(V.get(0));
        triangles.add(V.get(1));
        Log.d(GAlg.DEBUG_TAG, "STACK #1:" + stack.toString());
        for (int i = 2; i < V.size(); i++) {
            Point2D p = V.get(i);
            Log.d(GAlg.DEBUG_TAG, "P is :" + p.toString());
            if (isAdjacentTo(p, stack.getLast(), originalV) && !isAdjacentTo(p, stack.getFirst(), originalV)) {
                Point2D top = stack.pop();
                triangles.add(p);
                triangles.add(top);
                while (stack.size() > 1) {
                    // while (!stack.isEmpty()) {
                    triangles.add(p);
                    triangles.add(stack.pop());
                }
                Log.d(GAlg.DEBUG_TAG, "STACK #2:" + stack.toString());
                stack.push(top);
                stack.push(p);
                Log.d(GAlg.DEBUG_TAG, "STACK #3:" + stack.toString());
            } else if (!isAdjacentTo(p, stack.getLast(), originalV) && isAdjacentTo(p, stack.getFirst(), originalV)) {
                // toArray? Really? This is so freakin wrong!
                Log.d(GAlg.DEBUG_TAG, "STACK #4:" + stack.toString());
                while (stack.size() > 1 && Utils.ccw(p, (Point2D) stack.toArray()[0], (Point2D) stack.toArray()[1]) > 0) {
                    triangles.add(p);
                    triangles.add((Point2D) stack.toArray()[1]);
                    stack.pop();
                    Log.d(GAlg.DEBUG_TAG, "STACK # W 5:" + stack.toString());
                }
                stack.push(p);
                Log.d(GAlg.DEBUG_TAG, "STACK #6:" + stack.toString());
            } else if (isAdjacentTo(p, stack.getLast(), originalV) && isAdjacentTo(p, stack.getFirst(), originalV)) {
                Object[] stackAsArray = stack.toArray();
                for (int k = 1; k < stackAsArray.length - 1; k++) {
                    triangles.add(p);
                    triangles.add((Point2D) stackAsArray[k]);
                }
                Log.d(GAlg.DEBUG_TAG, "STACK #Almost END:" + stack.toString());
                break;
            } else {
                Log.d(GAlg.DEBUG_TAG, "IS THIS ERROR?");
            }
        }
        Log.d(GAlg.DEBUG_TAG, "STACK #END:" + stack.toString());
        Log.d(GAlg.DEBUG_TAG, "Triangles:" + triangles.toString());
        return triangles;
    }

    public boolean isAdjacentTo(Point2D a, Point2D b, List<Point2D> points) {
        int indexA = points.indexOf(a);
        if (indexA < 0 || indexA > points.size() - 1) {
            Log.d(GAlg.DEBUG_TAG, "isAdjacentTo(" + a + "," + b + "): Err");
            return false;
        }
        int indexBL = indexA - 1;
        int indexBR = indexA + 1;
        if (indexBL >= 0 && indexBL < points.size() && points.get(indexBL).equals(b)) {
            Log.d(GAlg.DEBUG_TAG, "isAdjacentTo(" + a + "," + b + "): True");
            return true;
        }
        if (indexBR >= 0 && indexBR < points.size() && points.get(indexBR).equals(b)) {
            Log.d(GAlg.DEBUG_TAG, "isAdjacentTo(" + a + "," + b + "): True");
            return true;
        }
        Log.d(GAlg.DEBUG_TAG, "isAdjacentTo(" + a + "," + b + "): False");
        return false;
    }

}
