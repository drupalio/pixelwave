package pixelweave.core;

import pixelweave.input.MouseHandler;
import pixelweave.render.Renderer;
import pixelweave.ui.ButtonWidget;
import pixelweave.ui.UiScene;

public final class Application {
    public void run() {
        Window window = new Window(800, 600, "PixelWeave");
        Renderer renderer = new Renderer(window);
        MouseHandler mouseHandler = new MouseHandler();

        UiScene uiScene = new UiScene();
        uiScene.root().setBounds(0, 0, 800, 600);

        ButtonWidget button = new ButtonWidget("Click Me", () -> System.out.println("mainBtn clicked"));
        button.setBounds(32, 32, 200, 50);
        uiScene.root().addChild(button);

        RenderLoop loop = new RenderLoop(window, renderer, uiScene, mouseHandler);

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
