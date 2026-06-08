import { useEffect, useRef } from "react";
import { useLocation } from "react-router-dom";

const scrollPositions = new Map<string, number>();

export function useScrollRestoration(key?: string) {
  const location = useLocation();
  const locationKey = key ?? location.pathname;
  const isRestoring = useRef(false);

  // Save scroll position before leaving
  useEffect(() => {
    const handleSave = () => {
      scrollPositions.set(locationKey, window.scrollY);
    };
    window.addEventListener("beforeunload", handleSave);
    return () => {
      window.removeEventListener("beforeunload", handleSave);
      // Also save on unmount (route change)
      handleSave();
    };
  }, [locationKey]);

  // Restore scroll position on mount
  useEffect(() => {
    const saved = scrollPositions.get(locationKey);
    if (saved !== undefined && saved > 0) {
      isRestoring.current = true;
      requestAnimationFrame(() => {
        window.scrollTo({ top: saved, behavior: "instant" as ScrollBehavior });
        isRestoring.current = false;
      });
    } else {
      window.scrollTo({ top: 0, behavior: "instant" as ScrollBehavior });
    }
  }, [locationKey]);

  return { isRestoring: isRestoring.current };
}

export function scrollToTop(behavior: ScrollBehavior = "smooth") {
  window.scrollTo({ top: 0, behavior });
}

export function scrollToElement(elementId: string, offset = 0) {
  const el = document.getElementById(elementId);
  if (el) {
    const top = el.getBoundingClientRect().top + window.scrollY - offset;
    window.scrollTo({ top, behavior: "smooth" });
  }
}
