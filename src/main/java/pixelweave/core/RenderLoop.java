package pixelweave.core;

import pixelweave.render.Renderer;

public final class RenderLoop {
    private static final double TARGET_FRAME_SECONDS = 1.0 / 60.0;

    private final Window window;
    private final Renderer renderer;

    public RenderLoop(Window window, Renderer renderer) {
        this.window = window;
        this.renderer = renderer;
    }

    public void run() {
        double previous = window.nowSeconds();
        while (!window.shouldClose()) {
            double current = window.nowSeconds();
            double delta = current - previous;
            previous = current;

            renderer.render(delta);
            window.swapBuffers();
            window.pollEvents();

            double frameTime = window.nowSeconds() - current;
            sleepToCapFramerate(frameTime);
        }
    }

    private void sleepToCapFramerate(double frameTimeSeconds) {
        double remaining = TARGET_FRAME_SECONDS - frameTimeSeconds;
        if (remaining <= 0) {
            return;
        }

        long millis = (long) (remaining * 1000.0);
        int nanos = (int) ((remaining * 1_000_000_000.0) % 1_000_000.0);

        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            window.requestClose();
        }
    }
}
