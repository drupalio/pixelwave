package pixelweave.render.command;

import pixelweave.render.ShapeRenderer;
import pixelweave.render.TextRenderer;

public interface DrawCommand {
    void execute(ShapeRenderer shapeRenderer, TextRenderer textRenderer, int viewportHeight);
}
