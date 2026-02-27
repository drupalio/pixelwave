package pixelweave.core;

import pixelweave.css.ComputedStyle;
import pixelweave.css.CssParser;
import pixelweave.css.StyleEngine;
import pixelweave.dom.DomNode;
import pixelweave.dom.HtmlLoader;
import pixelweave.input.MouseHandler;
import pixelweave.render.Color;
import pixelweave.render.Renderer;
import pixelweave.ui.ButtonWidget;
import pixelweave.ui.UiScene;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class Application {
    public void run() {
        Window window = new Window(800, 600, "PixelWeave");
        Renderer renderer = new Renderer(window);
        MouseHandler mouseHandler = new MouseHandler();

        UiScene uiScene = new UiScene();
        uiScene.root().setBounds(0, 0, 800, 600);

        ButtonWidget button = buildButtonFromResources();
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

    private ButtonWidget buildButtonFromResources() {
        ButtonWidget button = new ButtonWidget("Click Me", () -> System.out.println("mainBtn clicked"));
        button.setBounds(32, 32, 200, 50);

        try {
            String html = readResource("/ui.html");
            String css = readResource("/ui.css");

            HtmlLoader htmlLoader = new HtmlLoader();
            DomNode root = htmlLoader.parseString(html);

            DomNode buttonNode = findFirstTag(root, "button");
            if (buttonNode == null) {
                return button;
            }

            CssParser cssParser = new CssParser();
            StyleEngine styleEngine = new StyleEngine();
            Map<DomNode, ComputedStyle> styles = styleEngine.computeStyles(root, cssParser.parse(css));
            ComputedStyle computedStyle = styles.get(buttonNode);

            if (buttonNode.text() != null && !buttonNode.text().isBlank()) {
                button.setText(buttonNode.text());
            }

            if (computedStyle != null) {
                button.setBounds(
                        32,
                        32,
                        parseInt(computedStyle.get("width"), 200),
                        parseInt(computedStyle.get("height"), 50)
                );
                button.setBorderRadius(parseInt(computedStyle.get("border-radius"), 8));
                button.setFontSize(parseInt(computedStyle.get("font-size"), 20));

                Color baseColor = parseColor(computedStyle.get("background-color"), Color.fromHex("#3498db"));
                button.setBaseBackground(baseColor);
                button.setHoverBackground(darken(baseColor, 0.18f));
                button.setTextColor(parseColor(computedStyle.get("color"), Color.named("white")));
            }
        } catch (Exception ex) {
            System.err.println("Failed to load ui.html/ui.css, using fallback button: " + ex.getMessage());
        }

        return button;
    }

    private DomNode findFirstTag(DomNode node, String tagName) {
        if (node.tagName().equalsIgnoreCase(tagName)) {
            return node;
        }
        for (DomNode child : node.children()) {
            DomNode found = findFirstTag(child, tagName);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private String readResource(String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Resource not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.endsWith("px") ? value.substring(0, value.length() - 2) : value;
        try {
            return Integer.parseInt(normalized.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private Color parseColor(String value, Color fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        try {
            if (value.startsWith("#")) {
                return Color.fromHex(value);
            }
            return Color.named(value);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    private Color darken(Color color, float amount) {
        float factor = Math.max(0f, Math.min(1f, 1f - amount));
        return new Color(color.r() * factor, color.g() * factor, color.b() * factor, color.a());
    }
}
