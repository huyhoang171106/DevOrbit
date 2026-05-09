---
phase: 1
plan: 2
wave: 2
depends_on: ["1.1"]
files_modified:
  - devorbit-web/src/pages/PhotoboothPage.tsx
  - devorbit-web/src/components/photobooth/UploadZone.tsx
  - devorbit-web/src/components/photobooth/FramePicker.tsx
  - devorbit-web/src/components/photobooth/PhotoPreview.tsx
autonomous: true

must_haves:
  truths:
    - "Photobooth page is accessible via a clean route"
    - "Upload zone handles drag-and-drop and file selection for multiple images"
    - "Frame picker allows switching between different Korean-style layouts"
    - "Preview updates in real-time as frames are selected"
    - "Download button triggers the high-quality composited result"
  artifacts:
    - "devorbit-web/src/pages/PhotoboothPage.tsx"
    - "devorbit-web/src/components/photobooth/UploadZone.tsx"
    - "devorbit-web/src/components/photobooth/FramePicker.tsx"
    - "devorbit-web/src/components/photobooth/PhotoPreview.tsx"
---

# Plan 1.2: Photobooth UI Implementation

<objective>
Implement the user interface for the photobooth feature. This includes the main page layout, a multi-image upload zone, a premium frame selector, and a real-time preview of the composited photo.

Purpose: Provide a delightful, "WOW" experience for users to create and download their custom photobooth photos.
Output: A fully functional photobooth page with upload, select, and download capabilities.
</objective>

<context>
Load for context:
- devorbit-web/src/index.css (design tokens)
- devorbit-web/src/lib/photobooth.ts (engine)
- devorbit-web/src/constants/frames.ts (frame data)
</context>

<tasks>

<task type="auto">
  <name>Create PhotoboothPage and Route</name>
  <files>devorbit-web/src/pages/PhotoboothPage.tsx, devorbit-web/src/router.tsx</files>
  <action>
    - Create the main `PhotoboothPage` component using a two-column layout:
      - Left/Top: Control panel (Upload + Frame Picker).
      - Right/Bottom: Preview area.
    - Register the route `/photobooth` in `router.tsx`.
    - Use `.bg-clay-bg` and `.clay-card` for consistent styling.
  </action>
  <verify>Check if page renders at /photobooth.</verify>
  <done>Page is accessible and styled.</done>
</task>

<task type="auto">
  <name>Implement UploadZone and FramePicker</name>
  <files>devorbit-web/src/components/photobooth/UploadZone.tsx, devorbit-web/src/components/photobooth/FramePicker.tsx</files>
  <action>
    - `UploadZone`: Create a drag-and-drop area. When files are uploaded, convert them to `UploadedPhoto` (data URLs) and store in page state.
    - `FramePicker`: Display a horizontal scrollable list of available frames from `PHOTO_FRAMES`. Show a thumbnail preview of each frame.
    - Ensure smooth transitions and hover effects using Claymorphic classes.
  </action>
  <verify>Verify files can be uploaded and frames can be selected.</verify>
  <done>Inputs are functional.</done>
</task>

<task type="auto">
  <name>Implement PhotoPreview and Download</name>
  <files>devorbit-web/src/components/photobooth/PhotoPreview.tsx</files>
  <action>
    - `PhotoPreview`: Use a `<canvas>` or `<img>` to show the result of `compositePhotosWithFrame`.
    - Add a "Download" button using `.btn-primary`.
    - Handle cases where no photos are uploaded (show placeholder).
    - Ensure the preview is responsive (scale down to fit screen).
  </action>
  <verify>Verify that selecting a frame updates the preview and download works.</verify>
  <done>Feature is end-to-end functional.</done>
</task>

</tasks>

<verification>
After all tasks, verify:
- [ ] User can upload 4 photos.
- [ ] Selecting "Korean 4-Cut" frame shows the 4 photos in a vertical strip.
- [ ] Clicking Download saves a high-quality image.
- [ ] UI looks premium and follows the Claymorphic design system.
</verification>

<success_criteria>
- [ ] Fully functional upload-to-download photobooth experience.
- [ ] Modern, high-impact UI.
- [ ] Optimized for both desktop and mobile layouts.
</success_criteria>
