import { apiGet, apiPost, apiDelete, apiUpload } from "../../lib/api";
import type { StoredFrame } from "../../types/frames";

const BASE = "/api/photobooth/frames";

function mapStored(row: any): StoredFrame {
  return {
    id: row.frameId,
    name: row.name,
    displayName: row.displayName,
    photoCount: row.photoCount,
    description: row.description ?? "",
    slots: typeof row.slots === "string" ? JSON.parse(row.slots) : (row.slots ?? []),
    overlayImage: row.overlayImageUrl ?? "",
    filter: row.filter ?? "normal",
    backgroundColor: row.backgroundColor ?? "#ffffff",
  };
}

async function listFrames(): Promise<StoredFrame[]> {
  const data = await apiGet(BASE);
  if (!Array.isArray(data)) return [];
  return data.map(mapStored);
}

async function getFrame(frameId: string): Promise<StoredFrame | null> {
  const data = await apiGet(`${BASE}/${frameId}`);
  if (!data) return null;
  return mapStored(data);
}

async function upsertFrame(frame: StoredFrame): Promise<boolean> {
  const body = {
    frameId: frame.id,
    name: frame.name,
    displayName: frame.displayName,
    photoCount: frame.photoCount,
    description: frame.description ?? "",
    slots: typeof frame.slots === "string" ? JSON.parse(frame.slots) : frame.slots,
    overlayImageUrl: frame.overlayImage ?? "",
    filter: frame.filter ?? "normal",
    backgroundColor: frame.backgroundColor ?? "#ffffff",
  };
  const result = await apiPost(BASE, body);
  return !!result;
}

async function deleteFrame(frameId: string): Promise<boolean> {
  try {
    await apiDelete(`${BASE}/${frameId}`);
    return true;
  } catch {
    return false;
  }
}

async function uploadFrameImage(frameId: string, file: File): Promise<string | null> {
  try {
    const formData = new FormData();
    formData.append("file", file);
    const result = await apiUpload<{ url?: string }>(`${BASE}/${frameId}/overlay`, formData);
    return result?.url ?? null;
  } catch {
    // Fallback: data URL
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => resolve(null);
      reader.readAsDataURL(file);
    });
  }
}

export const frameService = {
  list: listFrames,
  get: getFrame,
  upsert: upsertFrame,
  delete: deleteFrame,
  uploadImage: uploadFrameImage,
};
