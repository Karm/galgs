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

    
 // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
 // intersect the intersection point may be stored in the doubles i_x and i_y.
 public boolean get_line_intersection(double p0_x, double p0_y, double p1_x, double p1_y, double p2_x, double p2_y, double p3_x, double p3_y){
     // intersection point
     double i_x = 0d;
     double i_y = 0d;

     double s1_x, s1_y, s2_x, s2_y;
     s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
     s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

     double s, t;
     s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
     t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

     if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
         // Collision detected
         i_x = p0_x + (t * s1_x);
         i_y = p0_y + (t * s1_y);
         Log.d(GAlg.DEBUG_TAG, "COLLISION ON POINT:["+i_x+","+i_y+"]");
         if((i_x == p0_x && i_y == p0_y) || (i_x == p1_x && i_y == p1_y) || (i_x == p2_x && i_y == p2_y) || (i_x == p3_x && i_y == p3_y)) {
             Log.d(GAlg.DEBUG_TAG, "COLLISION IGNORED...");
             return false;
         }
         return true;
     }

     return false; // No collision
 }
    
    /*
     * public static boolean linesIntersect(Point2D a,
     * Point2D b,
     * Point2D c,
     * Point2D d)
     * {
     * return ((Utils.ccw(a, b, c) *
     * Utils.ccw(a, b, d) <= 0)
     * && (Utils.ccw(c, d, a) *
     * Utils.ccw(c, d, b) <= 0));
     * }
     */
    public  boolean linesIntersect(Point2D a,
            Point2D b,
            Point2D c,
            Point2D d)
    {
        
        
        return get_line_intersection(a.x(), a.y(), b.x(), b.y(), c.x(), c.y(), d.x(), d.y());
        
        /*
        return ((relativeCCW(a.x(), a.y(), b.x(), b.y(), c.x(), c.y()) *
                relativeCCW(a.x(), a.y(), b.x(), b.y(), d.x(), d.y()) <= 0)
                && (relativeCCW(c.x(), c.y(), d.x(), d.y(), a.x(), a.y()) *
                relativeCCW(c.x(), c.y(), d.x(), d.y(), b.x(), b.y()) <= 0));*/
    }

    public static int relativeCCW(double x1, double y1,

            double x2, double y2,

            double px, double py)

    {

        x2 -= x1;

        y2 -= y1;

        px -= x1;

        py -= y1;

        double ccw = px * y2 - py * x2;

        if (ccw == 0.0) {

            // The point is colinear, classify based on which side of

            // the segment the point falls on.  We can calculate a

            // relative value using the projection of px,py onto the

            // segment - a negative value indicates the point projects

            // outside of the segment in the direction of the particular

            // endpoint used as the origin for the projection.

            ccw = px * x2 + py * y2;

            if (ccw > 0.0) {

                // Reverse the projection to be relative to the original x2,y2

                // x2 and y2 are simply negated.

                // px and py need to have (x2 - x1) or (y2 - y1) subtracted

                //    from them (based on the original values)

                // Since we really want to get a positive answer when the

                //    point is "beyond (x2,y2)", then we want to calculate

                //    the inverse anyway - thus we leave x2 & y2 negated.

                px -= x2;

                py -= y2;

                ccw = px * x2 + py * y2;

                if (ccw < 0.0) {

                    ccw = 0.0;

                }

            }

        }

        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);

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
     * IMplementation might not be OK.
     * this chain thing is connected to the sweeping line, the vertical one...
     * 
     * @param a
     * @param b
     * @return
     */
    public boolean areOnTheSameChain(Point2D a, Point2D b) {
        boolean returnValue = true;
        if (b.x() < a.x()) {
            returnValue = false;
        }
        Log.d(GAlg.DEBUG_TAG, "Are on the same chain? Pa:" + a + ", Pb:" + b + ", Are they:" + returnValue);
        return returnValue;
    }

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
            if (areOnTheSameChain(p, stack.getFirst())) {
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

        return triangles;
    }

    public List<Point2D> sweepTriangulationXX(List<Point2D> V) {
        Collections.sort(V, new XYOrderComparator());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Deque<Point2D> stack = new ArrayDeque<Point2D>();
        stack.push(V.get(0));
        stack.push(V.get(1));
        triangles.add(V.get(0));
        triangles.add(V.get(1));
        for (int i = 2; i < V.size(); i++) {
            Point2D p = V.get(i);
            Point2D top = stack.pop();
            triangles.add(p);
            triangles.add(top);
            triangles.add(p);
            triangles.add(stack.pop());
            stack.push(top);
            stack.push(p);
        }
        return triangles;
    }

    public List<Point2D> sweepTriangulationX(List<Point2D> V) {
        // Sort vertices
        List<Point2D> originalV = V;
        Log.d(GAlg.DEBUG_TAG, "UNsorted V:" + V.toString());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Collections.sort(V, new XYOrderComparator());
        Log.d(GAlg.DEBUG_TAG, "Sorted V:" + V.toString());
        Deque<Point2D> stack = new ArrayDeque<Point2D>();
        stack.push(V.get(0));
        stack.push(V.get(1));
        //triangles.add(V.get(0));
        //triangles.add(V.get(1));
        Log.d(GAlg.DEBUG_TAG, "STACK #1:" + stack.toString());
        for (int i = 2; i < V.size(); i++) {
            Point2D p = V.get(i);
            Log.d(GAlg.DEBUG_TAG, "P is :" + p.toString());
            if (isAdjacentTo(p, stack.getLast(), originalV) && !isAdjacentTo(p, stack.getFirst(), originalV)) {
                Point2D top = stack.pop();
                triangles.add(p);
                triangles.add(top);
                // don't add bottom
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
                // toArray? Really? This is so freakin evil!
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
