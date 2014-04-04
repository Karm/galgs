package cz.urbangaming.galgs.algorithms.convexhull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.opengl.GLES20;
import android.util.Pair;
import cz.urbangaming.galgs.utils.Point2D;
import cz.urbangaming.galgs.utils.Utils;
import cz.urbangaming.galgs.utils.YXOrderComparator;

/**
 * GiftWrapping
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class GiftWrapping {

    private static GiftWrapping instance = null;

    private GiftWrapping() {
    }

    /**
     * We ain't wanna more instances lying around...
     * 
     * @return
     */
    public static synchronized GiftWrapping getInstance() {
        if (instance == null) {
            instance = new GiftWrapping();
        }
        return instance;
    }

    /**
     * Convex Hull with Gift Wrapping
     * 
     * @param vertices
     * @return
     */
    public Pair<List<Point2D>, Integer> convexHullGiftWrapping(List<Point2D> points) {
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
            return new Pair<List<Point2D>, Integer>(verticesOnHull, GLES20.GL_LINE_LOOP);
        } else {
            return new Pair<List<Point2D>, Integer>(points, GLES20.GL_LINE_LOOP);
        }
    }
}
