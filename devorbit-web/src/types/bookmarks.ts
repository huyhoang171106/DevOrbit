export interface BookmarkFolder {
  id: string;
  name: string;
  description?: string;
  icon?: string;
  color?: string;
  createdAt: string;
  updatedAt: string;
  itemCount: number;
}

export interface BookmarkItem {
  id: string;
  folderId: string;
  title: string;
  url?: string;
  description?: string;
  type: "repo" | "course" | "article" | "note" | "roadmap";
  metadata?: Record<string, unknown>;
  createdAt: string;
}

export interface CreateBookmarkFolderInput {
  name: string;
  description?: string;
  icon?: string;
  color?: string;
}

export interface CreateBookmarkInput {
  folderId: string;
  title: string;
  url?: string;
  description?: string;
  type: BookmarkItem["type"];
  metadata?: Record<string, unknown>;
}
