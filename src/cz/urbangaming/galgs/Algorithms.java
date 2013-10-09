package cz.urbangaming.galgs;

import java.util.ArrayList;
import java.util.List;

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
    public static List<Float> convexHullGiftWrapping(List<Float> vertices) {
        if (vertices.size() > 3) {
            List<Float> vertexOnTheHull = Utils.maxYMaxX(vertices);
            List<Float> vertexsOnHull = new ArrayList<Float>();
            List<Float> currentVertex = new ArrayList<Float>(3);
            do {
                vertexsOnHull.addAll(vertexOnTheHull);
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
            } while (!currentVertex.equals(vertexsOnHull.subList(0, 3)));
            //Log.d(GAlg.DEBUG_TAG, "RIGHTMOST BOTTOM:" + vertexOnHull);
            return vertexsOnHull;
        } else {
            return vertices;
        }
    }
}
