-- Add image_url column to community_messages for image-only messages.
-- Each image message stores the Supabase Storage public URL here.

ALTER TABLE community_messages
ADD COLUMN IF NOT EXISTS image_url TEXT;

COMMENT ON COLUMN community_messages.image_url IS 'Public URL of image sent in community chat (nullable for text-only messages)';
