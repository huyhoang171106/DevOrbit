-- Photobooth frames table
CREATE TABLE photobooth_frames (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    frame_id TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    display_name TEXT NOT NULL,
    photo_count INTEGER NOT NULL CHECK (photo_count IN (1, 2, 3, 4, 6)),
    description TEXT DEFAULT '',
    slots JSONB NOT NULL DEFAULT '[]'::jsonb,
    overlay_image_url TEXT,
    filter TEXT DEFAULT 'normal',
    background_color TEXT DEFAULT '#ffffff',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_photobooth_frames_frame_id ON photobooth_frames(frame_id);

-- Bucket for frame overlay images
INSERT INTO storage.buckets (id, name, public)
VALUES ('frame-overlays', 'frame-overlays', true)
ON CONFLICT (id) DO NOTHING;

-- Allow public read on frame-overlays bucket
CREATE POLICY "Public read access"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'frame-overlays');

-- Allow admin/authenticated upload to frame-overlays
CREATE POLICY "Authenticated upload"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'frame-overlays');

-- Enable RLS on photobooth_frames
ALTER TABLE photobooth_frames ENABLE ROW LEVEL SECURITY;

-- Allow public read
CREATE POLICY "Public read photobooth_frames"
ON photobooth_frames FOR SELECT
TO public
USING (true);

-- Allow anon/public to insert/update/delete (admin page uses anon key)
CREATE POLICY "Public insert photobooth_frames"
ON photobooth_frames FOR INSERT
TO public
WITH CHECK (true);

CREATE POLICY "Public update photobooth_frames"
ON photobooth_frames FOR UPDATE
TO public
USING (true);

CREATE POLICY "Public delete photobooth_frames"
ON photobooth_frames FOR DELETE
TO public
USING (true);

-- Seed default frames
INSERT INTO photobooth_frames (frame_id, name, display_name, photo_count, description, slots, filter, background_color) VALUES
('frame01', 'Frame 01', 'Frame 3 Pics', 3, 'Three photos with decorative frame overlay',
 '[{"id":"slot1","x":0,"y":0,"width":2000,"height":667,"borderRadius":0},{"id":"slot2","x":0,"y":667,"width":2000,"height":666,"borderRadius":0},{"id":"slot3","x":0,"y":1333,"width":2000,"height":667,"borderRadius":0}]'::jsonb,
 'normal', '#ffffff'),

('frame02', 'Frame 02', 'Frame 6 Pics', 6, 'Six photos with decorative frame overlay',
 '[{"id":"slot1","x":0,"y":0,"width":2000,"height":333,"borderRadius":0},{"id":"slot2","x":0,"y":333,"width":2000,"height":334,"borderRadius":0},{"id":"slot3","x":0,"y":667,"width":2000,"height":333,"borderRadius":0},{"id":"slot4","x":0,"y":1000,"width":2000,"height":334,"borderRadius":0},{"id":"slot5","x":0,"y":1334,"width":2000,"height":333,"borderRadius":0},{"id":"slot6","x":0,"y":1667,"width":2000,"height":333,"borderRadius":0}]'::jsonb,
 'normal', '#ffffff'),

('frame03', 'Frame 03', 'Frame 4 Pics', 4, 'Four photos with decorative frame overlay',
 '[{"id":"slot1","x":0,"y":0,"width":2000,"height":500,"borderRadius":0},{"id":"slot2","x":0,"y":500,"width":2000,"height":500,"borderRadius":0},{"id":"slot3","x":0,"y":1000,"width":2000,"height":500,"borderRadius":0},{"id":"slot4","x":0,"y":1500,"width":2000,"height":500,"borderRadius":0}]'::jsonb,
 'normal', '#ffffff')
ON CONFLICT (frame_id) DO NOTHING;
