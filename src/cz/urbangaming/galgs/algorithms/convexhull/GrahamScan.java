package cz.urbangaming.galgs.algorithms.convexhull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import android.opengl.GLES20;
import android.util.Pair;
import cz.urbangaming.galgs.utils.Point2D;
import cz.urbangaming.galgs.utils.PolarOrderComparator;
import cz.urbangaming.galgs.utils.Utils;
import cz.urbangaming.galgs.utils.YXOrderComparator;

/**
 * Convex hull with Graham's scan
 * Source: Graphics Gems V., Alan W. Paeth, 1995
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 * @param vertices
 * @return
 */
public class GrahamScan {

    private static GrahamScan instance = null;

    private GrahamScan() {
    }

    /**
     * We ain't wanna more instances lying around...
     * 
     * @return
     */
    public static synchronized GrahamScan getInstance() {
        if (instance == null) {
            instance = new GrahamScan();
        }
        return instance;
    }

    public Pair<List<Point2D>, Integer> convexHullGrahamScan(List<Point2D> vertices) {
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
        return new Pair<List<Point2D>, Integer>(new ArrayList<Point2D>(verticesOnHull), GLES20.GL_LINE_LOOP);
    }
}
