---
phase: 1
plan: 1
wave: 1
depends_on: []
files_modified:
  - devorbit-web/src/types/photobooth.ts
  - devorbit-web/src/lib/photobooth.ts
  - devorbit-web/src/constants/frames.ts
autonomous: true

must_haves:
  truths:
    - "Photobooth types support multiple frame slots for '4-frame' layouts"
    - "Compositing logic handles multiple uploaded images and maps them to slots"
    - "Frames are defined with decorative overlays and optional filters"
    - "Utility functions exist for image loading and downloading"
  artifacts:
    - "devorbit-web/src/types/photobooth.ts"
    - "devorbit-web/src/lib/photobooth.ts"
    - "devorbit-web/src/constants/frames.ts"
---

# Plan 1.1: Photobooth Upload Engine & Frame Definitions

<objective>
Build the core engine for the upload-based photobooth feature. This includes type definitions for multi-slot frames, a robust Canvas-based compositing library, and a set of premium Korean-style frame definitions.

Purpose: Decouple the complex image processing logic from the UI components, ensuring high-quality, consistent photo generation.
Output: Core types, compositing utilities, and frame constants.
</objective>

<context>
Load for context:
- docs/stories/photobooth-upload.md
- devorbit-web/src/types/api.ts
</context>

<tasks>

<task type="auto">
  <name>Create photobooth type definitions</name>
  <files>devorbit-web/src/types/photobooth.ts</files>
  <action>
    Define the data structures for the photobooth feature:
    - `FrameTheme`: 'classic' | 'korean-4-cut' | 'modern' | 'minimal'
    - `FrameSlot`: Coordinates {x, y, width, height} for where photos go.
    - `PhotoFrame`: ID, name, theme, slots[], overlaySvg (commands), photoFilter, aspect ratio (usually 2:3 for strips or 4:3 for single).
    - `UploadedPhoto`: ID, dataUrl, file reference.
    
    AVOID: Complex state management here. Keep it to pure data interfaces.
  </action>
  <verify>Check file existence and TS syntax.</verify>
  <done>Types are defined and exported.</done>
</task>

<task type="auto">
  <name>Implement Canvas compositing engine</name>
  <files>devorbit-web/src/lib/photobooth.ts</files>
  <action>
    Create a module with:
    - `compositePhotosWithFrame(photos: UploadedPhoto[], frame: PhotoFrame)`: 
      - Creates a canvas of appropriate resolution (e.g., 1200x1800 for 4-cut).
      - Draws each photo into its corresponding `FrameSlot` using "cover" fit.
      - Applies frame.photoFilter if present.
      - Draws the `overlaySvg` commands (borders, logos, text) on top.
      - Returns a high-quality data URL.
    - `loadImage(url: string): Promise<HTMLImageElement>`: Helper for canvas drawing.
    - `downloadResult(dataUrl: string, filename: string)`: Browser download helper.

    AVOID: Async loading inside the main draw loop if possible — pre-load images.
  </action>
  <verify>Functions are exported and handle multiple slots correctly.</verify>
  <done>Compositing engine is functional and pure.</done>
</task>

<task type="auto">
  <name>Define premium Korean-style frames</name>
  <files>devorbit-web/src/constants/frames.ts</files>
  <action>
    Create a collection of frames:
    1. **UIT 20th Anniversary (Classic)**: Single large photo with UIT branding.
    2. **Korean 4-Cut (White)**: Vertical strip with 4 slots, white border, UIT logo at bottom.
    3. **Korean 4-Cut (Pink/Blue)**: Pastel versions of the 4-cut.
    4. **Film Strip**: Black border, 3 slots, film-grain aesthetics.

    Use the Canvas command pattern for overlays (rect, text, line) to keep it lightweight.
  </action>
  <verify>Constants are exported and follow the `PhotoFrame` type.</verify>
  <done>At least 4 high-quality frames are available.</done>
</task>

</tasks>

<verification>
After all tasks, verify:
- [ ] `devorbit-web/src/types/photobooth.ts` exists and is valid.
- [ ] `compositePhotosWithFrame` handles mapping N photos to N slots.
- [ ] Frames include the "4-cut" vertical layout.
</verification>

<success_criteria>
- [ ] Core engine can composite multiple photos into a frame.
- [ ] Frame definitions include specific Korean-style 4-cut layouts.
- [ ] Code is typed and follows project patterns.
</success_criteria>
