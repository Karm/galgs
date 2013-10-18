package cz.urbangaming.galgs.utils;


/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class Point2D {

    private float x;
    private float y;

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public void updateWith(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null)
            return false;
        if (other.getClass() != this.getClass())
            return false;
        Point2D that = (Point2D) other;
        return this.x == that.x && this.y == that.y;
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public int hashCode() {
        int hashX = ((Float) x).hashCode();
        int hashY = ((Float) y).hashCode();
        return 31 * hashX + hashY;
    }
}
