package cz.urbangaming.galgs.utils;

import java.util.Comparator;

// compare other points relative to atan2 angle (bewteen -pi/2 and pi/2) they make with this Point
public class Atan2OrderComparator implements Comparator<Point2D> {
    private Point2D pivot = null;
    
    public Atan2OrderComparator(Point2D pivot) {
        this.pivot = pivot;
    }

    public int compare(Point2D q1, Point2D q2) {
        double angle1 = Utils.angleTo(pivot,q1);
        double angle2 = Utils.angleTo(pivot,q2);
        if      (angle1 < angle2) return -1;
        else if (angle1 > angle2) return +1;
        else                      return  0;
    }
}
