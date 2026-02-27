package pixelweave.layout;

import pixelweave.dom.DomNode;

import java.util.ArrayList;
import java.util.List;

public final class LayoutBox {
    private final DomNode node;
    private final List<LayoutBox> children = new ArrayList<>();

    private int x;
    private int y;
    private int width;
    private int height;

    public LayoutBox(DomNode node) {
        this.node = node;
    }

    public DomNode node() {
        return node;
    }

    public List<LayoutBox> children() {
        return children;
    }

    public void addChild(LayoutBox child) {
        children.add(child);
    }

    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
