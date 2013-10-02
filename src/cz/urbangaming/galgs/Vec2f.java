package cz.urbangaming.galgs;

public class Vec2f {
    private float x;
    private float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f() {
        this.x = 0f;
        this.y = 0f;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float X() {
        return x;
    }

    public float Y() {
        return y;
    }
}
