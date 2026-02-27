package pixelweave.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;

public final class ShapeRenderer {
    public void drawRect(int x, int y, int width, int height, Color color, int viewportHeight) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int glY = viewportHeight - (y + height);
        glEnable(GL_SCISSOR_TEST);
        glScissor(x, glY, width, height);
        glClearColor(color.r(), color.g(), color.b(), color.a());
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_SCISSOR_TEST);
    }

    public void drawRoundedRect(int x, int y, int width, int height, int radius, Color color, int viewportHeight) {
        // Phase 2 approximation: render as standard rect; shader-rounded edges can replace this.
        drawRect(x, y, width, height, color, viewportHeight);
    }
}
