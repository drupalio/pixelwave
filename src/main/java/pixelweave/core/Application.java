package pixelweave.core;

import pixelweave.render.Renderer;

public final class Application {
    public void run() {
        Window window = new Window(800, 600, "PixelWeave");
        Renderer renderer = new Renderer(window);
        RenderLoop loop = new RenderLoop(window, renderer);

        try {
            window.init();
            renderer.init();
            loop.run();
        } finally {
            renderer.cleanup();
            window.cleanup();
        }
    }
}
