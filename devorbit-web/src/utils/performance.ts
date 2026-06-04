type MetricName = "TTFB" | "FCP" | "LCP" | "CLS" | "INP" | "navigation";

interface PerformanceMetric {
  name: MetricName;
  value: number;
  timestamp: number;
  url: string;
}

const metrics: PerformanceMetric[] = [];
const MAX_METRICS = 100;

export function observeWebVitals(): () => void {
  if (typeof window === "undefined" || !("performance" in window)) {
    return () => {};
  }

  // Observe LCP
  const lcpObserver = new PerformanceObserver((list) => {
    const entries = list.getEntries();
    if (entries.length > 0) {
      const lastEntry = entries[entries.length - 1];
      recordMetric("LCP", lastEntry.startTime);
    }
  });
  lcpObserver.observe({ type: "largest-contentful-paint", buffered: true });

  // Observe FCP
  const fcpObserver = new PerformanceObserver((list) => {
    const entries = list.getEntries();
    if (entries.length > 0) {
      recordMetric("FCP", entries[0].startTime);
    }
  });
  fcpObserver.observe({ type: "paint", buffered: true });

  // Observe CLS
  let clsValue = 0;
  const clsObserver = new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      if (!(entry instanceof PerformanceEventTiming)) {
        clsValue += (entry as LayoutShift).value || 0;
      }
    }
    recordMetric("CLS", clsValue);
  });
  try {
    clsObserver.observe({ type: "layout-shift", buffered: true });
  } catch {
    // CLS not supported
  }

  // Observe INP
  const inpObserver = new PerformanceObserver((list) => {
    const entries = list.getEntries();
    for (const entry of entries) {
      if (entry instanceof PerformanceEventTiming) {
        recordMetric("INP", entry.duration);
      }
    }
  });
  try {
    inpObserver.observe({ type: "first-input", buffered: true });
  } catch {
    // INP not supported
  }

  return () => {
    lcpObserver.disconnect();
    fcpObserver.disconnect();
    clsObserver.disconnect();
    inpObserver.disconnect();
  };
}

function recordMetric(name: MetricName, value: number): void {
  metrics.push({
    name,
    value: Math.round(value * 100) / 100,
    timestamp: Date.now(),
    url: window.location.pathname,
  });
  if (metrics.length > MAX_METRICS) metrics.shift();
}

export function getMetrics(): PerformanceMetric[] {
  return [...metrics];
}

export function getMetricsSummary(): Record<string, number> {
  const summary: Record<string, number> = {};
  for (const m of metrics) {
    if (!summary[m.name] || m.value > summary[m.name]) {
      summary[m.name] = m.value;
    }
  }
  return summary;
}

export function measureRenderTime(label: string): () => void {
  if (process.env.NODE_ENV === "production") return () => {};
  const start = performance.now();
  return () => {
    const end = performance.now();
    console.debug(`[Perf] ${label}: ${(end - start).toFixed(2)}ms`);
  };
}
