# PixelWeave

Minimal custom Java UI toolkit prototype with a native window + OpenGL render loop.

## Design inspirations

- **Kivy-inspired**: widget tree with stateful controls and per-frame input/update/render traversal.
- **Skia-inspired**: retained **display list** (draw command recording) separated from backend draw execution.

## Current status

This repository now implements a practical foundation for Phase 1 and early Phase 2/7 ideas:

- Native GLFW window (800x600)
- OpenGL context initialization
- Render loop targeting ~60 FPS
- Solid background clear color
- Mouse tracking + click release detection
- Widget tree (`RootWidget`, `ButtonWidget`)
- Display list draw commands (`RectCommand`, `TextCommand`) executed by renderer
- Resize-aware viewport updates and graceful close handling

Scaffolding is also included for phases 3-6 (DOM/CSS/Layout packages).

## Run

```bash
gradle run
```

Notes:
- On Java 22+ the app enables native access automatically for LWJGL via `--enable-native-access=ALL-UNNAMED`.
- LWJGL natives are selected by OS + CPU architecture (including macOS Apple Silicon).

## Next steps

1. Replace the rectangle clear/scissor fallback with VBO/shader-based geometry + rounded corner shader.
2. Add STB TrueType glyph atlas and render text commands.
3. Bind HTML/CSS/layout output into widget construction and draw command recording.


## Arquitectura (Documento técnico)

- Ver `docs/PIXELWAVE_ARCHITECTURE_ES.md` para el diseño integral del toolkit.
