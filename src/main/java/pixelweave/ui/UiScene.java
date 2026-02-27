package pixelweave.ui;

import pixelweave.input.MouseHandler;
import pixelweave.render.command.DisplayList;

public final class UiScene {
    private final RootWidget root = new RootWidget();
    private final DisplayList displayList = new DisplayList();

    public RootWidget root() {
        return root;
    }

    public DisplayList displayList() {
        return displayList;
    }

    public void update(MouseHandler mouseHandler, boolean clickReleased) {
        root.updateTree(mouseHandler, clickReleased);
    }

    public void record() {
        displayList.clear();
        root.renderTree(displayList);
    }
}
