package cz.urbangaming.galgs.utils;

import java.util.Comparator;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 *          Credit goes, again, to deBerg.
 */
public class PolarOrderComparator implements Comparator<Point2D> {
    private Point2D pivot = null;

    public PolarOrderComparator(Point2D pivot) {
        this.pivot = pivot;
    }

    public int compare(Point2D q1, Point2D q2) {
        double dx1 = q1.x() - pivot.x();
        double dy1 = q1.y() - pivot.y();
        double dx2 = q2.x() - pivot.x();
        double dy2 = q2.y() - pivot.y();

        if (dy1 >= 0 && dy2 < 0)
            return -1; // q1 above; q2 below
        else if (dy2 >= 0 && dy1 < 0)
            return +1; // q1 below; q2 above
        else if (dy1 == 0 && dy2 == 0) { // 3-collinear and horizontal
            if (dx1 >= 0 && dx2 < 0)
                return -1;
            else if (dx2 >= 0 && dx1 < 0)
                return +1;
            else
                return 0;
        }
        else
            return -Utils.ccw(pivot, q1, q2); // both above or below

        // Note: ccw() recomputes dx1, dy1, dx2, and dy2
    }
}