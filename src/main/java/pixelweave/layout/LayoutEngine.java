package pixelweave.layout;

import pixelweave.css.ComputedStyle;
import pixelweave.dom.DomNode;

import java.util.Map;

public final class LayoutEngine {
    public LayoutBox layout(DomNode root, Map<DomNode, ComputedStyle> styles, int viewportWidth, int viewportHeight) {
        LayoutBox box = new LayoutBox(root);
        layoutRecursive(box, styles, 0, 0, viewportWidth, viewportHeight);
        return box;
    }

    private int layoutRecursive(LayoutBox box, Map<DomNode, ComputedStyle> styles, int x, int y, int maxWidth, int defaultHeight) {
        ComputedStyle style = styles.getOrDefault(box.node(), new ComputedStyle());

        int width = parsePx(style.get("width"), maxWidth);
        int height = parsePx(style.get("height"), Math.max(defaultHeight, 24));
        int padding = parsePx(style.get("padding"), 0);

        int contentX = x + padding;
        int cursorY = y + padding;

        for (DomNode childNode : box.node().children()) {
            LayoutBox childBox = new LayoutBox(childNode);
            box.addChild(childBox);
            int childHeight = layoutRecursive(childBox, styles, contentX, cursorY, width - (padding * 2), defaultHeight);
            cursorY += childHeight + padding;
        }

        int finalHeight = box.children().isEmpty() ? height : Math.max(height, cursorY - y + padding);
        box.setBounds(x, y, width, finalHeight);
        return finalHeight;
    }

    private int parsePx(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        if (value.endsWith("px")) {
            return Integer.parseInt(value.substring(0, value.length() - 2).trim());
        }
        return Integer.parseInt(value.trim());
    }
}
