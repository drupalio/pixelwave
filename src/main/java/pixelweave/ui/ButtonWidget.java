package pixelweave.ui;

import pixelweave.input.MouseHandler;
import pixelweave.render.Color;
import pixelweave.render.command.DisplayList;
import pixelweave.render.command.RectCommand;
import pixelweave.render.command.TextCommand;

public final class ButtonWidget extends Widget {
    private final String text;
    private final Runnable onClick;

    private boolean hovered;

    public ButtonWidget(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    protected void render(DisplayList list) {
        Color background = hovered ? Color.fromHex("#2878ad") : Color.fromHex("#3498db");
        list.add(new RectCommand(x, y, width, height, 8, background));

        int textX = x + 16;
        int textY = y + (height / 2) + 5;
        list.add(new TextCommand(text, textX, textY, 20, Color.named("white")));
    }

    @Override
    protected void update(MouseHandler mouseHandler, boolean clickReleased) {
        hovered = contains(mouseHandler.x(), mouseHandler.y());
        if (hovered && clickReleased) {
            onClick.run();
        }
    }
}
