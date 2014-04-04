package cz.urbangaming.galgs.algorithms.triangulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import android.opengl.GLES20;
import android.util.Log;
import android.util.Pair;
import cz.urbangaming.galgs.GAlg;
import cz.urbangaming.galgs.utils.Point2D;
import cz.urbangaming.galgs.utils.Utils;
import cz.urbangaming.galgs.utils.XYOrderComparator;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class SweepTriangulation {

    private static SweepTriangulation instance = null;

    private SweepTriangulation() {
    }

    /**
     * We ain't wanna more instances lying around...
     * 
     * @return
     */
    public static synchronized SweepTriangulation getInstance() {
        if (instance == null) {
            instance = new SweepTriangulation();
        }
        return instance;
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
    public Pair<List<Point2D>, Integer> sweepTriangulation(List<Point2D> V) {
        List<Point2D> originalV = new ArrayList<Point2D>(V);
        Collections.sort(V, new XYOrderComparator());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Deque<Point2D> stack = new ArrayDeque<Point2D>();
        Log.d(GAlg.DEBUG_TAG, "Sorted V:" + V.toString());
        stack.push(V.get(0));
        stack.push(V.get(1));
        for (int i = 2; i < V.size(); i++) {
            Point2D p = V.get(i);
            if (Utils.areOnTheSameChain(p, stack.getFirst(), V)) {
                Log.d(GAlg.DEBUG_TAG, "Entered Case1");
                Point2D tempTop = stack.pop();
                while (!stack.isEmpty() && Utils.isItLegalDiagonal(p, tempTop, originalV)) {
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
        return new Pair<List<Point2D>, Integer>(triangles, GLES20.GL_LINES);
    }

    /**
     * This method works, but it's fiendishly suboptimal...
     * 
     * @param V
     * @return
     */
    public Pair<List<Point2D>, Integer> naiveTriangulation(List<Point2D> V) {
        Collections.sort(V, new XYOrderComparator());
        List<Point2D> triangles = new ArrayList<Point2D>();
        Log.d(GAlg.DEBUG_TAG, "Sorted V:" + V.toString());
        triangles.add(V.get(0));
        triangles.add(V.get(1));
        for (int i = 2; i < V.size(); i++) {
            for (int j = 1; j <= i; j++) {
                if (Utils.isItLegalDiagonal(V.get(i), V.get(i - j), triangles)) {
                    triangles.add(V.get(i));
                    triangles.add(V.get(i - j));
                }
            }
        }
        return new Pair<List<Point2D>, Integer>(triangles, GLES20.GL_LINES);
    }
}
