package pixelweave.render;

import pixelweave.core.Window;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

public final class Renderer {
    private final Window window;
    private Color background = Color.fromHex("#1e1e2e");

    public Renderer(Window window) {
        this.window = window;
    }

    public void init() {
        // Phase 1: OpenGL context is initialized by window.
    }

    public void render(double deltaSeconds) {
        glClearColor(background.r(), background.g(), background.b(), background.a());
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public void cleanup() {
        // Reserved for future resource cleanup.
    }
}
