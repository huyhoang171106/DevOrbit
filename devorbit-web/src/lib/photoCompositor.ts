import { FrameDefinition, FilterType } from "./frames/frameDefinitions";
import { SlotFilter, FILTER_PRESETS } from "./photoFilters";

const LOGICAL_MAX = 2000;
const imageCache = new Map<string, HTMLImageElement>();

function getLogicalSize(imgW: number, imgH: number) {
  const maxDim = Math.max(imgW, imgH);
  if (maxDim === 0) return { w: LOGICAL_MAX, h: LOGICAL_MAX };
  const scale = LOGICAL_MAX / maxDim;
  return { w: Math.round(imgW * scale), h: Math.round(imgH * scale) };
}

export class PhotoCompositor {
  static async loadImages(files: File[]): Promise<HTMLImageElement[]> {
    const images: HTMLImageElement[] = [];

    for (const file of files) {
      const img = await this.loadImageFromFile(file);
      images.push(img);
    }

    return images;
  }

  private static loadImageFromFile(file: File): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = () =>
          reject(new Error(`Failed to load image: ${file.name}`));
        img.src = e.target?.result as string;
      };
      reader.onerror = () =>
        reject(new Error(`Failed to read file: ${file.name}`));
      reader.readAsDataURL(file);
    });
  }

  private static loadImageFromUrl(url: string): Promise<HTMLImageElement> {
    if (imageCache.has(url)) {
      return Promise.resolve(imageCache.get(url)!);
    }

    return new Promise((resolve, reject) => {
      const img = new Image();
      img.crossOrigin = "anonymous";
      img.onload = () => {
        imageCache.set(url, img);
        resolve(img);
      };
      img.onerror = () =>
        reject(new Error(`Failed to load image: ${url}`));
      img.src = url;
    });
  }

  static clearCache() {
    imageCache.clear();
  }

  static async compositePhotos(
    images: HTMLImageElement[],
    frame: FrameDefinition,
    offsets?: { dx: number; dy: number; zoom?: number }[],
    slotFilters?: SlotFilter[],
  ): Promise<HTMLCanvasElement> {
    // Determine canvas size from frame overlay image if available
    let canvasW = LOGICAL_MAX;
    let canvasH = LOGICAL_MAX;
    let overlayImg: HTMLImageElement | null = null;

    if (frame.overlayImage) {
      try {
        overlayImg = await this.loadImageFromUrl(frame.overlayImage);
        canvasW = overlayImg.naturalWidth || overlayImg.width || LOGICAL_MAX;
        canvasH = overlayImg.naturalHeight || overlayImg.height || LOGICAL_MAX;
      } catch (err) {
        console.error("Failed to load frame overlay image:", err);
      }
    }

    const { w: logicalW, h: logicalH } = getLogicalSize(canvasW, canvasH);

    const scaleX = canvasW / logicalW;
    const scaleY = canvasH / logicalH;

    const canvas = document.createElement("canvas");
    canvas.width = canvasW;
    canvas.height = canvasH;
    const ctx = canvas.getContext("2d")!;

    // Fill background
    ctx.fillStyle = frame.backgroundColor;
    ctx.fillRect(0, 0, canvasW, canvasH);

    // Scale slots from logical space to actual canvas size
    const scaledSlots = frame.slots.map((s) => ({
      ...s,
      x: Math.round(s.x * scaleX),
      y: Math.round(s.y * scaleY),
      width: Math.round(s.width * scaleX),
      height: Math.round(s.height * scaleY),
      borderRadius: Math.round(s.borderRadius * scaleX),
    }));

    // Draw each photo into its scaled slot
    scaledSlots.forEach((slot, index) => {
      if (index < images.length) {
        this.drawImageInSlot(ctx, images[index], slot, offsets?.[index], slotFilters?.[index]);
      }
    });

    // Draw vector overlays
    frame.overlays.forEach((overlay) => {
      this.drawOverlay(ctx, overlay);
    });

    // Draw overlay image on top at its natural size
    if (overlayImg) {
      ctx.drawImage(overlayImg, 0, 0, canvasW, canvasH);
    }

    // Apply filter if specified
    if (frame.filter !== "normal") {
      this.applyFilter(canvas, frame.filter);
    }

    return canvas;
  }

  private static drawImageInSlot(
    ctx: CanvasRenderingContext2D,
    image: HTMLImageElement,
    slot: {
      x: number;
      y: number;
      width: number;
      height: number;
      borderRadius: number;
      border?: { color: string; width: number };
    },
    offset?: { dx: number; dy: number; zoom?: number },
    slotFilter?: SlotFilter,
  ) {
    const imgAspect = image.width / image.height;
    const slotAspect = slot.width / slot.height;

    // Cover-fit: base crop
    let sourceWidth = image.width;
    let sourceHeight = image.height;
    if (imgAspect > slotAspect) {
      sourceWidth = image.height * slotAspect;
    } else {
      sourceHeight = image.width / slotAspect;
    }

    // Apply zoom
    const zoom = offset?.zoom ?? 1;
    sourceWidth /= zoom;
    sourceHeight /= zoom;

    // Center
    let sourceX = (image.width - sourceWidth) / 2;
    let sourceY = (image.height - sourceHeight) / 2;

    // Apply user offset (clamped so crop stays within image bounds)
    if (offset) {
      const maxDx = (image.width - sourceWidth) / 2;
      const maxDy = (image.height - sourceHeight) / 2;
      sourceX += Math.max(-maxDx, Math.min(maxDx, offset.dx));
      sourceY += Math.max(-maxDy, Math.min(maxDy, offset.dy));
    }

    // Draw with per-slot filter
    ctx.save();
    if (slotFilter && slotFilter !== "normal") {
      ctx.filter = FILTER_PRESETS[slotFilter]?.css || "none";
    }
    ctx.beginPath();
    this.roundRect(
      ctx,
      slot.x,
      slot.y,
      slot.width,
      slot.height,
      slot.borderRadius,
    );
    ctx.clip();
    ctx.drawImage(
      image,
      sourceX,
      sourceY,
      sourceWidth,
      sourceHeight,
      slot.x,
      slot.y,
      slot.width,
      slot.height,
    );
    ctx.restore();

    // Draw border if specified
    if (slot.border) {
      ctx.strokeStyle = slot.border.color;
      ctx.lineWidth = slot.border.width;
      ctx.beginPath();
      this.roundRect(
        ctx,
        slot.x,
        slot.y,
        slot.width,
        slot.height,
        slot.borderRadius,
      );
      ctx.stroke();
    }
  }

  private static drawOverlay(ctx: CanvasRenderingContext2D, overlay: any) {
    ctx.fillStyle = overlay.color || overlay.fillColor || "#ffffff";
    ctx.strokeStyle = overlay.color || "#ffffff";
    ctx.lineWidth = overlay.strokeWidth || 1;

    switch (overlay.type) {
      case "rect":
        if (overlay.radius !== undefined) {
          ctx.beginPath();
          this.roundRect(
            ctx,
            overlay.x,
            overlay.y,
            overlay.width || 0,
            overlay.height || 0,
            overlay.radius,
          );
          if (overlay.fillColor) ctx.fill();
          if (overlay.strokeWidth) ctx.stroke();
        } else {
          if (overlay.fillColor) {
            ctx.fillStyle = overlay.fillColor;
            ctx.fillRect(
              overlay.x,
              overlay.y,
              overlay.width || 0,
              overlay.height || 0,
            );
          }
          if (overlay.strokeWidth) {
            ctx.strokeRect(
              overlay.x,
              overlay.y,
              overlay.width || 0,
              overlay.height || 0,
            );
          }
        }
        break;

      case "circle":
        ctx.beginPath();
        const radius =
          overlay.radius ||
          Math.min(overlay.width || 0, overlay.height || 0) / 2;
        ctx.arc(
          overlay.x + (overlay.width || 0) / 2,
          overlay.y + (overlay.height || 0) / 2,
          radius,
          0,
          Math.PI * 2,
        );
        if (overlay.fillColor) {
          ctx.fillStyle = overlay.fillColor;
          ctx.fill();
        }
        if (overlay.strokeWidth) ctx.stroke();
        break;

      case "line":
        ctx.beginPath();
        ctx.moveTo(overlay.x, overlay.y);
        ctx.lineTo(
          overlay.x + (overlay.width || 0),
          overlay.y + (overlay.height || 0),
        );
        ctx.stroke();
        break;

      case "text":
        ctx.fillStyle = overlay.color || "#ffffff";
        ctx.font = `${overlay.fontWeight || 400} ${overlay.fontSize || 16}px Sora, sans-serif`;
        ctx.textAlign = overlay.textAlign || "center";
        ctx.textBaseline = "middle";

        const textX = overlay.x + (overlay.width || 0) / 2;
        const textY = overlay.y + (overlay.height || 0) / 2;

        ctx.fillText(
          overlay.content || "",
          textX,
          textY,
          overlay.width || 1000,
        );
        break;
    }
  }

  private static roundRect(
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    width: number,
    height: number,
    radius: number,
  ) {
    const r = Math.min(radius, Math.min(width, height) / 2);
    ctx.moveTo(x + r, y);
    ctx.lineTo(x + width - r, y);
    ctx.arcTo(x + width, y, x + width, y + r, r);
    ctx.lineTo(x + width, y + height - r);
    ctx.arcTo(x + width, y + height, x + width - r, y + height, r);
    ctx.lineTo(x + r, y + height);
    ctx.arcTo(x, y + height, x, y + height - r, r);
    ctx.lineTo(x, y + r);
    ctx.arcTo(x, y, x + r, y, r);
  }

  private static applyFilter(canvas: HTMLCanvasElement, filter: FilterType) {
    const ctx = canvas.getContext("2d")!;
    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    const data = imageData.data;

    switch (filter) {
      case "sepia":
        this.applySepiaFilter(data);
        break;
      case "vintage":
        this.applyVintageFilter(data);
        break;
      case "cool":
        this.applyCoolFilter(data);
        break;
    }

    ctx.putImageData(imageData, 0, 0);
  }

  private static applySepiaFilter(data: Uint8ClampedArray) {
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i];
      const g = data[i + 1];
      const b = data[i + 2];

      data[i] = Math.min(255, r * 1.07 + 20);
      data[i + 1] = Math.min(255, g * 0.74 + 20);
      data[i + 2] = Math.max(0, b * 0.43 + 20);
    }
  }

  private static applyVintageFilter(data: Uint8ClampedArray) {
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i];
      const g = data[i + 1];
      const b = data[i + 2];

      data[i] = Math.min(255, r * 1.1 + 10);
      data[i + 1] = Math.min(255, g * 1.05 + 5);
      data[i + 2] = Math.max(0, b * 0.8);
    }
  }

  private static applyCoolFilter(data: Uint8ClampedArray) {
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i];
      const g = data[i + 1];
      const b = data[i + 2];

      data[i] = Math.max(0, r * 0.9);
      data[i + 1] = Math.min(255, g * 1.0);
      data[i + 2] = Math.min(255, b * 1.2);
    }
  }

  static async exportImage(
    canvas: HTMLCanvasElement,
    fileName: string,
  ): Promise<void> {
    return new Promise((resolve, reject) => {
      canvas.toBlob(
        (blob) => {
          if (!blob) {
            reject(new Error("Failed to export canvas"));
            return;
          }

          const url = URL.createObjectURL(blob);
          const link = document.createElement("a");
          link.href = url;
          link.download = fileName;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          URL.revokeObjectURL(url);
          resolve();
        },
        "image/png",
        1,
      );
    });
  }
}
