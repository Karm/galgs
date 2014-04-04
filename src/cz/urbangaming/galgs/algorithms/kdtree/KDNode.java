package cz.urbangaming.galgs.algorithms.kdtree;

import cz.urbangaming.galgs.algorithms.kdtree.KDTree.KDNodeType;
import cz.urbangaming.galgs.utils.Point2D;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class KDNode {
    public KDNode leftChild, rightChild, parent;
    public int treeDepth;
    public Point2D point;
    public KDNodeType type;

    public KDNode(KDNode parent, KDNodeType type, Point2D point, int treeDepth) {
        this.parent = parent;
        this.treeDepth = treeDepth;
        this.point = point;
        this.type = type;
    }
}
