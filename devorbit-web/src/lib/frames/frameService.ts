import { supabase } from "../supabase";
import type { StoredFrame } from "../../types/frames";

const TABLE = "photobooth_frames";
const BUCKET = "frame-overlays";

async function listFrames(): Promise<StoredFrame[]> {
  const { data, error } = await supabase
    .from(TABLE)
    .select("*")
    .order("created_at", { ascending: true });

  if (error) {
    console.error("Failed to list frames:", error);
    return [];
  }

  return (data ?? []).map(mapRow);
}

async function getFrame(frameId: string): Promise<StoredFrame | null> {
  const { data, error } = await supabase
    .from(TABLE)
    .select("*")
    .eq("frame_id", frameId)
    .single();

  if (error || !data) return null;
  return mapRow(data);
}

async function upsertFrame(frame: StoredFrame): Promise<boolean> {
  const { error } = await supabase.from(TABLE).upsert(
    {
      frame_id: frame.id,
      name: frame.name,
      display_name: frame.displayName,
      photo_count: frame.photoCount,
      description: frame.description,
      slots: JSON.stringify(frame.slots),
      overlay_image_url: frame.overlayImage,
      filter: frame.filter,
      background_color: frame.backgroundColor,
    },
    { onConflict: "frame_id" },
  );

  if (error) {
    console.error("Failed to upsert frame:", error);
    return false;
  }
  return true;
}

async function deleteFrame(frameId: string): Promise<boolean> {
  const { error } = await supabase
    .from(TABLE)
    .delete()
    .eq("frame_id", frameId);

  if (error) {
    console.error("Failed to delete frame:", error);
    return false;
  }
  return true;
}

async function uploadFrameImage(
  frameId: string,
  file: File,
): Promise<string | null> {
  // Try Supabase Storage first
  const ext = file.name.split(".").pop() || "png";
  const path = `${frameId}.${ext}`;

  const { error } = await supabase.storage
    .from(BUCKET)
    .upload(path, file, { upsert: true });

  if (!error) {
    const { data: publicUrl } = supabase.storage
      .from(BUCKET)
      .getPublicUrl(path);
    return publicUrl?.publicUrl ?? null;
  }

  console.warn("Storage upload failed, falling back to data URL:", error.message);

  // Fallback: convert to data URL and store in DB
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = () => reject(new Error("Failed to read file"));
    reader.readAsDataURL(file);
  });
}

function mapRow(row: any): StoredFrame {
  return {
    id: row.frame_id,
    name: row.name,
    displayName: row.display_name,
    photoCount: row.photo_count,
    description: row.description ?? "",
    slots:
      typeof row.slots === "string" ? JSON.parse(row.slots) : row.slots ?? [],
    overlayImage: row.overlay_image_url ?? "",
    filter: row.filter ?? "normal",
    backgroundColor: row.background_color ?? "#ffffff",
  };
}

export const frameService = {
  list: listFrames,
  get: getFrame,
  upsert: upsertFrame,
  delete: deleteFrame,
  uploadImage: uploadFrameImage,
};
