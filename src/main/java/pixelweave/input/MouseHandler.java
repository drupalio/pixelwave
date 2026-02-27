package pixelweave.input;

public final class MouseHandler {
    private double x;
    private double y;
    private boolean pressed;

    public void updatePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void updatePressed(boolean pressed) {
        this.pressed = pressed;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public boolean pressed() {
        return pressed;
    }
}
