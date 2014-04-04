package cz.urbangaming.galgs.utils;

/**
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class RectangularArea {
    private float x, y, width, height;

    public RectangularArea(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public RectangularArea(float x, float y, float endx, float endy, boolean setByEndPoint) {
        if (setByEndPoint) {
            this.x = x;
            this.y = y;
            this.width = endx - x;
            this.height = endy - y;
        } else {
            this.x = x;
            this.y = y;
            this.width = endx;
            this.height = endy;
        }
    }

    public float endX() {
        return x + width;
    }

    public float endY() {
        return y + height;
    }

    public boolean inside(float x, float y) {
        return Utils.insideRect(x, y, this.x, this.y, this.width, this.height);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}
