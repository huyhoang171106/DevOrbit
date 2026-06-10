import { useEffect, useCallback, useRef } from "react";

interface Shortcut {
  key: string;
  ctrl?: boolean;
  shift?: boolean;
  alt?: boolean;
  handler: (e: KeyboardEvent) => void;
  description?: string;
  enabled?: boolean;
}

export function useKeyboardShortcuts(shortcuts: Shortcut[]) {
  const shortcutsRef = useRef(shortcuts);
  shortcutsRef.current = shortcuts;

  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    for (const shortcut of shortcutsRef.current) {
      if (shortcut.enabled === false) continue;
      const match =
        e.key.toLowerCase() === shortcut.key.toLowerCase() &&
        !!e.ctrlKey === !!shortcut.ctrl &&
        !!e.shiftKey === !!shortcut.shift &&
        !!e.altKey === !!shortcut.alt;
      if (match) {
        e.preventDefault();
        shortcut.handler(e);
        return;
      }
    }
  }, []);

  useEffect(() => {
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [handleKeyDown]);
}

// Common shortcuts preset
export function useSearchShortcut(handler: () => void) {
  useKeyboardShortcuts([
    {
      key: "k",
      ctrl: true,
      handler,
      description: "Mở tìm kiếm (Ctrl+K)",
    },
  ]);
}

export function useEscapeShortcut(handler: () => void) {
  useKeyboardShortcuts([
    {
      key: "Escape",
      handler,
      description: "Đóng popup / modal",
    },
  ]);
}
