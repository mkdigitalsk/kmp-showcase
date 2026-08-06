# App icon — source

The showcases sit side by side on one launcher: the **navy background says whose they are**, the **glyph
says which one**. This app's glyph is the letter **K**, white, on `#0E2A47` — the `brand.navy` token in
`design-system`. A publisher badge in a corner is the one thing that does not work: the outer 18 of the
108 canvas is reserved for the OEM mask, and that corner already carries the system's own badges.

| file | what it is |
| --- | --- |
| `android/foreground.svg` | the adaptive-icon **foreground layer only**, transparent, 108 canvas. The K is centred on (54,54) and reaches r=32.6 — see the safe-zone note below. |
| `ios/icon.svg` | the **composed** icon: full-bleed, opaque, 1024, square corners. iOS masks the corners itself and rejects transparency, so it has no layer split and no safe zone. |

## Generating

**Android** — Android Studio → *New › Image Asset* → Launcher Icons (Adaptive and Legacy), foreground =
`android/foreground.svg`, background = Color `#0E2A47`.
⚠ **Trim: No · Resize: 100%** — the SVG is already sized to the safe zone, and any other setting rescales
it. Output lands in `androidApp/src/main/res/` (`mipmap-anydpi-v26/`, the density `mipmap-*/`, the
foreground vector) plus `ic_launcher-playstore.png`.

**iOS** — export `ios/icon.svg` at 1024×1024 PNG into `AppIcon.appiconset`.

Hand-writing the vector instead skips the round icon, the legacy densities and the safe-zone maths — use
the generator.

## The safe zone is a circle

⚠ **The 66 safe zone is a circle, so the limit is the glyph's diagonal, not its width.** A 55×60 box fits
"66" on both axes and still loses its corners: they sit at `√(27.5² + 30²) = 40.7` from centre against a
radius of 33, and the circle mask cuts them off. Size any new glyph by its **furthest point from (54,54)**,
which must stay under 33 — the ready check is to render it with a 33-radius circle drawn on top.
