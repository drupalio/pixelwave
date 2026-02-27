package pixelweave.render.command;

import pixelweave.render.Color;
import pixelweave.render.ShapeRenderer;
import pixelweave.render.TextRenderer;

public record TextCommand(String text, int x, int y, int fontSize, Color color) implements DrawCommand {
    @Override
    public void execute(ShapeRenderer shapeRenderer, TextRenderer textRenderer, int viewportHeight) {
        textRenderer.drawText(text, x, y, fontSize, color, viewportHeight);
    }
}
