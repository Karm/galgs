package cz.urbangaming.galgs.utils;

import java.util.Comparator;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class YXOrderComparator implements Comparator<Point2D> {
    // an integer < 0 if lhs is less than rhs, 0 if they are equal, and > 0 if lhs is greater than rhs.
    public int compare(Point2D lhs, Point2D rhs) {
        if (lhs.y() < rhs.y())
            return -1;
        if (lhs.y() > rhs.y())
            return +1;
        if (lhs.x() < rhs.x())
            return -1;
        if (lhs.x() > rhs.x())
            return +1;
        return 0;
    }
}
