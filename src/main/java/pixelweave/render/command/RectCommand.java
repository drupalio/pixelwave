package pixelweave.render.command;

import pixelweave.render.Color;
import pixelweave.render.ShapeRenderer;
import pixelweave.render.TextRenderer;

public record RectCommand(int x, int y, int width, int height, int radius, Color color) implements DrawCommand {
    @Override
    public void execute(ShapeRenderer shapeRenderer, TextRenderer textRenderer, int viewportHeight) {
        if (radius <= 0) {
            shapeRenderer.drawRect(x, y, width, height, color, viewportHeight);
            return;
        }
        shapeRenderer.drawRoundedRect(x, y, width, height, radius, color, viewportHeight);
    }
}
