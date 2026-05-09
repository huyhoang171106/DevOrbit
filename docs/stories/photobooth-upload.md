# Story: Korean-Style Upload Photobooth

## Status
- **Status:** Draft
- **Role:** Student / User
- **Lane:** Normal
- **Risk:** Low

## Context
Users want to create trending "Korean-style" photobooth photos (4-frame vertical strips, film tones, cute frames) by uploading their own photos instead of using a physical booth or live camera.

## Requirements
- [ ] Users can upload 1-4 photos (depending on frame type).
- [ ] Users can select from a variety of predefined Korean-style frames.
- [ ] The system composites the photos into the selected frame with appropriate filters/tones.
- [ ] The final result can be downloaded as a high-quality image.
- [ ] (Bonus) QR code or email sharing (future phase).

## Success Criteria
- [ ] Uploaded photos are correctly fitted into frame slots (cover fit).
- [ ] Frame overlays (logos, text, borders) are rendered on top.
- [ ] Downloaded image matches the preview perfectly.
- [ ] UI is premium, responsive, and easy to use.

## Design
- **Engine**: Canvas 2D API for high-performance compositing.
- **Frames**: Data-driven frame definitions with slot coordinates and decorative overlays.
- **UI**: Modern, "Claymorphic" or "Glassmorphic" design consistent with DevOrbit.
