package cz.urbangaming.galgs.algorithms.kdtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.opengl.GLES20;
import android.util.Pair;
import cz.urbangaming.galgs.utils.Point2D;
import cz.urbangaming.galgs.utils.RectangularArea;
import cz.urbangaming.galgs.utils.Utils;
import cz.urbangaming.galgs.utils.XYOrderComparator;
import cz.urbangaming.galgs.utils.YXOrderComparator;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 *          Credit:
 *          Computational Geometry, Algorithms and Applications
 *          de Berg, http://www.cs.uu.nl/geobook/
 *          Chapter 5, Orthogonal search
 */
public class KDTree {

    private static KDTree instance = null;

    private KDTree() {
    }

    /**
     * We ain't wanna more instances lying around...
     * 
     * @return
     */
    public static synchronized KDTree getInstance() {
        if (instance == null) {
            instance = new KDTree();
        }
        return instance;
    }

    //Let's do some coloring, shall we?
    //Like two sets of Lines...?
    enum KDNodeType {
        VERTICAL_SPLIT_TYPE, HORIZONTAL_SPLIT_TYPE
    }

    private RectangularArea pointsArea;
    private KDNode treeRoot;
    private List<Point2D> linesPoints = new ArrayList<Point2D>();

    public KDTree(int width, int height) {
        pointsArea = new RectangularArea(0, 0, width, height);
    }

    private KDNode build(List<Point2D> points) {
        Point2D middlePoint = Utils.getMiddlePoint(points);

        treeRoot = new KDNode(null, KDNodeType.VERTICAL_SPLIT_TYPE, middlePoint, 0);
        linesPoints.add(new Point2D(middlePoint.x(), pointsArea.y()));
        linesPoints.add(new Point2D(middlePoint.x(), pointsArea.endY()));

        treeRoot.leftChild = build(new RectangularArea(0, 0, middlePoint.x(), pointsArea.endY()), 1, treeRoot, Utils.getLeftPoints(points));
        treeRoot.rightChild = build(new RectangularArea(middlePoint.x(), 0, pointsArea.endX(), pointsArea.endY()), 1, treeRoot, Utils.getRightPoints(points));

        return treeRoot;
    }

    private KDNode build(RectangularArea rectArea, int treeDepth, KDNode parent, List<Point2D> points) {

        // We ain't got no point, bro!
        if (points.size() == 0) {
            return null;
        }

        Collections.sort(points, treeDepth % 2 == 0 ? new XYOrderComparator() : new YXOrderComparator());
        Point2D middlePoint = Utils.getMiddlePoint(points);

        KDNode head = new KDNode(parent, treeDepth % 2 == 0 ? KDNodeType.VERTICAL_SPLIT_TYPE : KDNodeType.HORIZONTAL_SPLIT_TYPE, middlePoint, treeDepth);

        if (treeDepth % 2 == 0) {
            // Splitting it vertically
            linesPoints.add(new Point2D(middlePoint.x(), rectArea.y()));
            linesPoints.add(new Point2D(middlePoint.x(), rectArea.endY()));
        } else {
            // Splitting it horizontally
            linesPoints.add(new Point2D(rectArea.x(), middlePoint.y()));
            linesPoints.add(new Point2D(rectArea.endX(), middlePoint.y()));
        }

        if (points.size() == 1) {
            // We've got just the head...
            return head;
        }

        if (treeDepth % 2 == 0) { // Split vertically
            head.leftChild = build(new RectangularArea(rectArea.x(), rectArea.y(), middlePoint.x(), rectArea.endY(), true), treeDepth + 1, head, Utils.getLeftPoints(points));
            head.rightChild = build(new RectangularArea(middlePoint.x(), rectArea.y(), rectArea.endX(), rectArea.endY(), true), treeDepth + 1, head, Utils.getRightPoints(points));
        } else { // Split horizontally
            head.leftChild = build(new RectangularArea(rectArea.x(), rectArea.y(), rectArea.endX(), middlePoint.y(), true), treeDepth + 1, head, Utils.getLeftPoints(points));
            head.rightChild = build(new RectangularArea(rectArea.x(), middlePoint.y(), rectArea.endX(), rectArea.endY(), true), treeDepth + 1, head, Utils.getRightPoints(points));
        }

        return head;
    }

    public Pair<List<Point2D>, Integer> buildKDTree(List<Point2D> vertices) {
        Collections.sort(vertices, new XYOrderComparator());
        build(vertices);
        return new Pair<List<Point2D>, Integer>(linesPoints, GLES20.GL_LINES);

    }
}
