# PixelWeave

Minimal custom Java UI toolkit prototype with a native window + OpenGL render loop.

## Current status

This repository implements **Phase 1** from the plan:

- Native GLFW window (800x600)
- OpenGL context initialization
- Render loop targeting ~60 FPS
- Solid background clear color
- Resize-aware viewport updates
- Graceful close handling

Scaffolding is also included for phases 2-7 (DOM/CSS/Layout/Render/Input packages).

## Run

```bash
./gradlew run
```

## Next steps

1. Replace placeholder shape/text renderers with shader-backed drawing.
2. Connect DOM + CSS + layout pipeline into `Renderer`.
3. Add button hover/click visual state and callback dispatch.
