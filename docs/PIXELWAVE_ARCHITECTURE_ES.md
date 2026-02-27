# PixelWave UI Engine — Documento Técnico de Arquitectura (ES)

## 1) Visión y Objetivo

**Objetivo:** diseñar un toolkit UI moderno, multiplataforma, en Java, con renderizado Skia (Skija/Skiko), arquitectura inspirada en Kivy y con control total por píxel.

**Principios rectores:**
- Pipeline explícito: `Input -> Event Dispatch -> Reactive Update -> Layout -> Paint -> Composite -> Present`.
- Scene Graph liviano con dirty regions.
- Propiedades reactivas de bajo overhead (sin reflexión pesada en hot-path).
- Render loop propio desacoplado del OS UI loop.
- Backend agnóstico de API GPU (OpenGL / Vulkan / Metal) + fallback software.

---

## 2) Arquitectura General

```ascii
+---------------------- Application ----------------------+
|  App State / Services / ViewModels                      |
+--------------------------+------------------------------+
                           |
                           v
+--------------------- Declarative Layer -----------------+
| DSL (Java/Kotlin/JSON) -> Virtual Tree -> Diff Engine   |
+--------------------------+------------------------------+
                           |
                           v
+--------------------- Runtime Core ----------------------+
| Scene Graph | Reactive Props | Event System | Layout    |
+--------------------------+------------------------------+
                           |
                           v
+--------------------- Render Pipeline -------------------+
| Paint Commands -> Layer Tree -> Compositor -> Skia      |
+--------------------------+------------------------------+
                           |
                           v
+-------------------- Platform Backend -------------------+
| Windowing + Input + GPU Context (GL/VK/Metal/CPU)       |
+---------------------------------------------------------+
```

### Capas clave
1. **Core Runtime**: nodos, propiedades, invalidación, scheduler.
2. **UI Toolkit**: controles, estilos, temas, gestos.
3. **Renderer**: comandos de dibujo, rasterización y composición en Skia.
4. **Platform**: ventanas, input nativo, swapchain/surface.

---

## 3) Módulos Propuestos

```ascii
pixelwave/
 ├─ pixelwave-core
 │   ├─ scene/           (Node, Scene, DirtyFlags, HitTest)
 │   ├─ reactive/        (Property<T>, Binding, Signals)
 │   ├─ event/           (PointerEvent, GestureEvent, Dispatcher)
 │   ├─ layout/          (Box, Grid, Constraint)
 │   ├─ animation/       (Timeline, Spring, Easing)
 │   └─ scheduler/       (FrameClock, TaskQueue)
 │
 ├─ pixelwave-render-skia
 │   ├─ surface/         (SkiaSurfaceManager)
 │   ├─ paint/           (PaintOp, PaintRecorder)
 │   ├─ compositor/      (Layer, Blend, Clip, Effects)
 │   └─ backend/         (Skija/Skiko adapters)
 │
 ├─ pixelwave-platform-desktop
 │   ├─ window/          (Windows/macOS/Linux)
 │   ├─ input/           (mouse/touch/stylus/gesture)
 │   └─ gpu/             (GL/Vulkan/Metal selectors)
 │
 ├─ pixelwave-dsl-java
 ├─ pixelwave-dsl-kotlin
 ├─ pixelwave-dsl-json
 └─ pixelwave-controls
     ├─ Button, Text, ScrollView, List, Canvas
     └─ Theme, Style, Accessibility
```

---

## 4) Scene Graph Ligero y Optimizado

Cada `Node` mantiene:
- `bounds`, `transform`, `clip`, `opacity`, `zIndex`
- `layoutFlags`, `paintFlags`, `compositeFlags`
- `children[]` compactado (array/lista)
- caché de hit-test opcional

**Estrategia de performance:**
- Dirty bits por dominio (`LAYOUT`, `PAINT`, `COMPOSITE`, `INPUT`).
- Re-layout incremental (solo subárbol afectado).
- Render por regiones sucias (damage tracking).
- Cache de layer para subárboles estáticos.

---

## 5) Sistema de Eventos Unificado

### Tipos
- `PointerEvent`: mouse, touch, stylus (con pressure, tilt, id de contacto).
- `GestureEvent`: tap, long-press, pan, pinch, rotate, fling.
- `KeyboardEvent`, `FocusEvent`, `WheelEvent`.

### Flujo
```ascii
Native Input
   -> Normalize (Pointer Model común)
   -> Hit Test
   -> Capture Phase (root -> target)
   -> Target Phase
   -> Bubble Phase (target -> root)
   -> Gesture Arena arbitration
```

### Gesture Arena (inspiración móvil moderna)
Múltiples reconocedores compiten por una secuencia táctil; el ganador consume eventos para evitar conflictos (`Scroll` vs `Pinch`, etc.).

---

## 6) Sistema de Propiedades Reactivas

### Objetivos
- Actualizaciones O(1) amortizadas para listeners directos.
- Dirty checking automático sin recomposición global.
- Bindings bidireccionales opt-in (evitar ciclos por defecto).

### API propuesta
```java
public interface Property<T> {
    T get();
    void set(T value);
    Subscription observe(Observer<T> observer);
    <R> Property<R> map(Function<T, R> mapper);
    void bindBidirectional(Property<T> other);
}
```

### Semántica de invalidación
- `set()` marca dirty local y agenda flush en `FrameClock`.
- Cambios múltiples dentro del mismo frame se coalescen.
- Nodo afectado marca `PAINT` o `LAYOUT` según propiedad.

---

## 7) Layout System (Box / Grid / Constraints)

### BoxLayout
- Ejes horizontal/vertical.
- `spacing`, `padding`, `alignment`, `flexGrow`.

### GridLayout
- Tracks con `fixed`, `auto`, `fraction`.
- Soporte de `rowSpan/colSpan`.

### ConstraintLayout
- Anchors (`left/right/top/bottom/baseline/center`).
- Solver incremental (Cassowary-like simplificado).

**Regla de oro:** layout determinista en una o dos pasadas según contenedor.

---

## 8) Pipeline de Render y Compositing (Skia)

```ascii
Scene Graph
   -> Paint Recorder (PaintOp list)
   -> Layerization (clips, opacity, effects)
   -> Compositor pass
   -> Skia Canvas draw
   -> Surface flush + present
```

### Backend GPU/CPU
- Preferencia: Vulkan (Linux/Windows), Metal (macOS), OpenGL como fallback.
- CPU raster fallback para CI/headless/sistemas sin GPU.
- Selección automática en arranque + override por flags.

### Capas
- `PictureLayer`: comandos estáticos cacheables.
- `TextureLayer`: video/canvas externo.
- `EffectLayer`: blur, color filter, shadow.

---

## 9) Loop de Render Desacoplado

```ascii
while (running) {
  t = clock.now()
  input.poll()
  event.dispatch()
  reactive.flush()
  layout.reflowIfNeeded()
  paint.recordDirty()
  compositor.compose()
  renderer.present(vsync)
  framePacer.sleepUntilNextFrame()
}
```

- `FrameClock` configurable (60/120/variable refresh).
- Política de frames: renderizar solo si hay daño o animación activa.
- Modo `continuous` para apps gráficas intensivas.

---

## 10) Motor de Animaciones

- Animaciones por tiempo (`Timeline`) y por física (`Spring`).
- Easing: linear, cubic, quint, elastic, bounce, custom bezier.
- Animador desacoplado del renderer: emite cambios reactivos, no dibuja.

```ascii
Animation Tick -> Property Update -> Dirty Flags -> Next Frame Paint
```

---

## 11) Threading Model

- **UI Thread**: scene graph, layout, eventos, bindings.
- **Render Thread**: recording/composition/present (opcional separado).
- **IO/Worker Pool**: assets, decoding, red.

### Reglas
- Mutaciones de nodos solo en UI thread.
- Transfer de snapshots inmutables al render thread.
- Colas lock-free para frame packets cuando aplique.

---

## 12) Gestión de Memoria

- Pools para objetos de evento y comandos de pintura.
- Reuso de buffers nativos (ByteBuffer directo).
- Evitar churn de objetos en hot loop.
- Finalización explícita de recursos nativos Skia.

---

## 13) Modelo Declarativo (DSL)

### Opción A: DSL Java fluido
```java
UI.window("Demo", 800, 600,
    box(vertical(), spacing(12), padding(16),
        button("Click Me")
            .background(bind(vm.buttonColor))
            .onClick(e -> vm.toggleColor())
    )
);
```

### Opción B: DSL Kotlin
```kotlin
window("Demo", 800, 600) {
  column(spacing = 12, padding = 16) {
    button("Click Me") {
      background = bind(vm.buttonColor)
      onClick { vm.toggleColor() }
    }
  }
}
```

### Opción C: JSON/XML declarativo
- Útil para tooling y carga dinámica.
- Compila a virtual tree y luego a scene graph.

---

## 14) Comparación Técnica

## vs JavaFX
- Backend más moderno y portable con Skia + selección explícita de API GPU.
- Sistema de propiedades/reactividad diseñado para menor overhead en hot-path.
- Pipeline de capas/composición más cercano a motores gráficos actuales.
- Estrategia multitouch/gestos de primera clase (no accesorio).

## vs Swing
- Swing no nació GPU-first ni touch-first.
- PixelWave propone scene graph, compositor, animaciones y dirty regions desde diseño base.
- Mejor separación entre estado declarativo y backend de render.

## vs Qt
- Qt es C++ y maduro, pero PixelWave prioriza ecosistema Java/Kotlin e integración JVM.
- Arquitectura orientada a reactivo + DSL de alto nivel con bridge a Skia.
- Menor fricción para apps modernas en JVM.

## mejora frente a enfoque tipo Kivy
- Conserva simplicidad mental de árbol de widgets + propiedades.
- Mejora backend gráfico con Skia y compositing más robusto.
- Mayor foco en threading, daño incremental y multi-backend GPU.

---

## 15) Flujo Interno End-to-End

```ascii
[Input Nativo]
   -> [Normalizer]
   -> [Event Dispatcher + Gesture Arena]
   -> [Reactive Properties / Bindings]
   -> [Dirty Flags]
   -> [Layout Reflow Incremental]
   -> [Paint Recorder]
   -> [Layer Compositor]
   -> [Skia Backend]
   -> [Present]
```

---

## 16) Ejemplo mínimo funcional (MVP)

> Caso: ventana + botón + click + color reactivo dinámico.

```java
public final class MinimalApp {
    public static void main(String[] args) {
        var app = PixelWaveApp.create();
        var color = Properties.mutable(0xFF3498DB); // ARGB

        var button = Controls.button("Click Me")
                .width(200)
                .height(50)
                .background(color)
                .onClick(evt -> {
                    int current = color.get();
                    color.set(current == 0xFF3498DB ? 0xFF2ECC71 : 0xFF3498DB);
                });

        var root = Layouts.boxVertical()
                .padding(24)
                .spacing(12)
                .add(button);

        app.window("PixelWave", 800, 600)
           .setRoot(root)
           .run();
    }
}
```

### Resultado esperado
- Se abre ventana nativa.
- Botón se renderiza por Skia.
- Click alterna color usando propiedad reactiva.
- Repaint mínimo: solo región sucia del botón.

---

## 17) Decisiones de Diseño y Justificación

1. **Skia backend**: motor probado en producción (Chrome, Android, Flutter ecosistema).
2. **Widget tree + reactivo**: ergonomía alta para desarrollador y performance controlable.
3. **Dirty regions + layer cache**: escalabilidad en UI complejas.
4. **Render loop propio**: previsibilidad de frame pacing y animaciones.
5. **Módulos separados**: evolución independiente por plataforma y renderer.

---

## 18) Roadmap recomendado

### Fase A (Core)
- Scene graph, propiedades, event dispatch básico, BoxLayout.

### Fase B (Render)
- Integración Skija/Skiko, capas, clips, opacity, texto básico.

### Fase C (Interacción)
- Gestos multitouch, focus, accesibilidad inicial.

### Fase D (Declarativo)
- DSL Kotlin + tooling de inspección.

### Fase E (Optimización)
- Render thread separado, cachés avanzados, profiler integrado.

---

## 19) Riesgos y mitigación

- **Complejidad cross-platform GPU** -> matriz de compatibilidad + fallback CPU.
- **GC pauses** -> pools + reducción de objetos efímeros.
- **Sincronización UI/render** -> snapshots inmutables y contratos de threading.
- **Texto internacional** -> plan temprano para shaping avanzado (HarfBuzz futuro).

---

## 20) Conclusión

La propuesta de PixelWave combina la **simplicidad arquitectónica de Kivy** (árbol de widgets y propiedades) con un pipeline **moderno tipo motor gráfico** sobre **Skia**, habilitando control por píxel, multitouch, composición eficiente y un modelo declarativo preparado para escalar más allá de las limitaciones típicas de JavaFX y Swing.
