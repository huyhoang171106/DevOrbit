import { useState, useCallback, useEffect } from "react";

const STORAGE_KEY = "devorbit-search-history";
const MAX_ITEMS = 20;

interface SearchEntry {
  query: string;
  timestamp: number;
  category?: string;
}

export function useSearchHistory() {
  const [history, setHistory] = useState<SearchEntry[]>([]);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed: SearchEntry[] = JSON.parse(raw);
        setHistory(parsed);
      }
    } catch {
      // corrupted storage
    }
    setLoaded(true);
  }, []);

  const persist = useCallback((entries: SearchEntry[]) => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
    } catch {
      // storage full
    }
  }, []);

  const addEntry = useCallback(
    (query: string, category?: string) => {
      if (!query.trim()) return;
      setHistory((prev) => {
        const filtered = prev.filter(
          (e) => e.query.toLowerCase() !== query.toLowerCase()
        );
        const updated = [
          { query: query.trim(), timestamp: Date.now(), category },
          ...filtered,
        ].slice(0, MAX_ITEMS);
        persist(updated);
        return updated;
      });
    },
    [persist]
  );

  const removeEntry = useCallback(
    (query: string) => {
      setHistory((prev) => {
        const updated = prev.filter((e) => e.query !== query);
        persist(updated);
        return updated;
      });
    },
    [persist]
  );

  const clearHistory = useCallback(() => {
    setHistory([]);
    localStorage.removeItem(STORAGE_KEY);
  }, []);

  return {
    history,
    loaded,
    addEntry,
    removeEntry,
    clearHistory,
  };
}
