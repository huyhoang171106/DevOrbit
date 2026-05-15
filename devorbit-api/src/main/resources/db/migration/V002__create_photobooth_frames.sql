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

