package pixelweave.ui;

import pixelweave.input.MouseHandler;
import pixelweave.render.command.DisplayList;

import java.util.ArrayList;
import java.util.List;

public abstract class Widget {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    private final List<Widget> children = new ArrayList<>();

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addChild(Widget widget) {
        children.add(widget);
    }

    public List<Widget> children() {
        return List.copyOf(children);
    }

    public final void renderTree(DisplayList list) {
        render(list);
        for (Widget child : children) {
            child.renderTree(list);
        }
    }

    public final void updateTree(MouseHandler mouseHandler, boolean clickReleased) {
        update(mouseHandler, clickReleased);
        for (Widget child : children) {
            child.updateTree(mouseHandler, clickReleased);
        }
    }

    protected abstract void render(DisplayList list);

    protected void update(MouseHandler mouseHandler, boolean clickReleased) {
        // Optional override for stateful widgets.
    }

    protected boolean contains(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
