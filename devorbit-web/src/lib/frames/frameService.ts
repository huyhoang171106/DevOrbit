import { apiGet, apiAdminPost, apiAdminDelete, apiBaseUrl } from "../../lib/api";
import { getAdminToken } from "../auth";
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

function getToken(): string {
  const token = getAdminToken();
  if (!token) throw new Error("Unauthorized");
  return token;
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
  const token = getToken();
  const body = {
    frameId: frame.id,
    name: frame.name,
    displayName: frame.displayName,
    photoCount: frame.photoCount,
    description: frame.description ?? "",
    slots: typeof frame.slots === "string" ? frame.slots : JSON.stringify(frame.slots),
    overlayImageUrl: frame.overlayImage ?? "",
    filter: frame.filter ?? "normal",
    backgroundColor: frame.backgroundColor ?? "#ffffff",
  };
  const result = await apiAdminPost(BASE, token, body);
  return !!result;
}

async function deleteFrame(frameId: string): Promise<boolean> {
  try {
    await apiAdminDelete(`${BASE}/${frameId}`, getToken());
    return true;
  } catch {
    return false;
  }
}

async function uploadFrameImage(frameId: string, file: File): Promise<string | null> {
  try {
    const token = getToken();
    const formData = new FormData();
    formData.append("file", file);
    const result = await fetch(`${apiBaseUrl}${BASE}/${frameId}/overlay`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: formData,
    }).then(async (res) => {
      if (!res.ok) throw new Error(`Upload failed: ${res.status}`);
      return (await res.json()) as { url?: string };
    });
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
