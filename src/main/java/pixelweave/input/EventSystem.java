package pixelweave.input;

import pixelweave.layout.LayoutBox;

public final class EventSystem {
    public boolean isHovered(LayoutBox box, double mouseX, double mouseY) {
        return mouseX >= box.x()
                && mouseX <= box.x() + box.width()
                && mouseY >= box.y()
                && mouseY <= box.y() + box.height();
    }
}
