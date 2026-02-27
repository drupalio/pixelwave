package pixelweave.ui;

import pixelweave.input.MouseHandler;
import pixelweave.render.Color;
import pixelweave.render.command.DisplayList;
import pixelweave.render.command.RectCommand;
import pixelweave.render.command.TextCommand;

public final class ButtonWidget extends Widget {
    private String text;
    private final Runnable onClick;

    private boolean hovered;
    private Color baseBackground = Color.fromHex("#3498db");
    private Color hoverBackground = Color.fromHex("#2878ad");
    private Color textColor = Color.named("white");
    private int borderRadius = 8;
    private int fontSize = 20;

    public ButtonWidget(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    protected void render(DisplayList list) {
        Color background = hovered ? hoverBackground : baseBackground;
        list.add(new RectCommand(x, y, width, height, borderRadius, background));

        int textX = x + 16;
        int textY = y + (height / 2) + 5;
        list.add(new TextCommand(text, textX, textY, fontSize, textColor));
    }

    @Override
    protected void update(MouseHandler mouseHandler, boolean clickReleased) {
        hovered = contains(mouseHandler.x(), mouseHandler.y());
        if (hovered && clickReleased) {
            onClick.run();
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBaseBackground(Color baseBackground) {
        this.baseBackground = baseBackground;
    }

    public void setHoverBackground(Color hoverBackground) {
        this.hoverBackground = hoverBackground;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
