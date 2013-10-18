package cz.urbangaming.galgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
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
     * @param vertices
     * @return
     */
    public List<Float> convexHullGiftWrapping(List<Float> vertices) {
        if (vertices.size() > 3) {
            List<Float> vertexOnTheHull = Utils.maxYMaxX(vertices);
            List<Float> verticesOnHull = new ArrayList<Float>();
            List<Float> currentVertex = new ArrayList<Float>(3);
            do {
                verticesOnHull.addAll(vertexOnTheHull);
                currentVertex = vertices.subList(0, 3);
                for (int i = 3; i < vertices.size(); i += 3) {
                    List<Float> nextVertex = vertices.subList(i, i + 3);
                    List<Float> threeVertices = new ArrayList<Float>(9);
                    threeVertices.addAll(nextVertex);
                    threeVertices.addAll(vertexOnTheHull);
                    threeVertices.addAll(currentVertex);
                    if (currentVertex.equals(vertexOnTheHull) || Utils.position(threeVertices) == 1) {
                        currentVertex = nextVertex;
                    }
                }
                vertexOnTheHull = currentVertex;
            } while (!currentVertex.equals(verticesOnHull.subList(0, 3)));
            //Log.d(GAlg.DEBUG_TAG, "RIGHTMOST BOTTOM:" + vertexOnHull);
            return verticesOnHull;
        } else {
            return vertices;
        }
    }

    public List<Float> convexHullGrahamScan(List<Float> vertices) {
        //TODO Remove this transformation
        List<Point2D> trueVertices = new ArrayList<Point2D>();
        for (int i = 0; i < vertices.size(); i += 3) {
            List<Float> nextVertex = vertices.subList(i, i + 3);
            trueVertices.add(new Point2D(nextVertex.get(0), nextVertex.get(1)));
        }
        
        //Alg itself
        Deque<Point2D> verticesOnHull = new LinkedList<Point2D>();
        Collections.sort(trueVertices,new YXOrderComparator());
        Collections.sort(trueVertices, new PolarOrderComparator(trueVertices.get(0)));
        verticesOnHull.push(trueVertices.get(0));
        
        // find index k1 of first point not equal to points[0]
                int k1;
                for (k1 = 1; k1 < trueVertices.size(); k1++)
                    if (!trueVertices.get(0).equals(trueVertices.get(k1))) break;
                //TODO TODO TODO
                if (k1 == trueVertices.size()) return null;        // all points equal

                // find index k2 of first point not collinear with points[0] and points[k1]
                int k2;
                for (k2 = k1 + 1; k2 < trueVertices.size(); k2++)
                    if (Utils.ccw(trueVertices.get(0), trueVertices.get(k1), trueVertices.get(k2)) != 0) break;
                verticesOnHull.push(trueVertices.get(k2-1));

                // Graham scan; note that points[N-1] is extreme point different from points[0]
                for (int i = k2; i < trueVertices.size(); i++) {
                    Point2D top = verticesOnHull.pop();
                    while (Utils.ccw(verticesOnHull.peek(), top, trueVertices.get(i)) <= 0) {
                        top = verticesOnHull.pop();
                    }
                    verticesOnHull.push(top);
                    verticesOnHull.push(trueVertices.get(i));
                }
        
                
                //TRAnsform it back to whatever
                List<Float> returnVerts = new ArrayList<Float>();
                for (Point2D point2d : verticesOnHull) {
                    returnVerts.add(point2d.x());
                    returnVerts.add(point2d.y());
                    returnVerts.add(0f);
                }
        return returnVerts;
    }
}
