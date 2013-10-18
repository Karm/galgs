package cz.urbangaming.galgs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import cz.urbangaming.galgs.utils.Point2D;
import cz.urbangaming.galgs.utils.PolarOrderComparator;
import cz.urbangaming.galgs.utils.Utils;
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

    public List<Point2D> convexHullGrahamScan(List<Point2D> vertices) {
        Deque<Point2D> verticesOnHull = new ArrayDeque<Point2D>();
        Collections.sort(vertices, new YXOrderComparator());
        Collections.sort(vertices, new PolarOrderComparator(vertices.get(0)));
        verticesOnHull.push(vertices.get(0));

        // find index k1 of first point not equal to points[0]
        int k1;
        for (k1 = 1; k1 < vertices.size(); k1++)
            if (!vertices.get(0).equals(vertices.get(k1)))
                break;
        // TODO TODO TODO
        if (k1 == vertices.size())
            return null; // all points equal

        // find index k2 of first point not collinear with points[0] and points[k1]
        int k2;
        for (k2 = k1 + 1; k2 < vertices.size(); k2++)
            if (Utils.ccw(vertices.get(0), vertices.get(k1), vertices.get(k2)) != 0)
                break;
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
}
