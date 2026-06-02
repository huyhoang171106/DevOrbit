import { describe, it, expect, vi } from "vitest";
import { renderHook, act } from "@testing-library/react";
import { useDebounce, useDebouncedCallback } from "../useDebounce";

describe("useDebounce", () => {
  it("returns initial value immediately", () => {
    const { result } = renderHook(() => useDebounce("hello", 500));
    expect(result.current).toBe("hello");
  });

  it("updates value after delay", async () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: "hello", delay: 100 } }
    );
    expect(result.current).toBe("hello");
    rerender({ value: "world", delay: 100 });
    expect(result.current).toBe("hello");
    await vi.waitFor(() => expect(result.current).toBe("world"), { timeout: 300 });
  });
});

describe("useDebouncedCallback", () => {
  it("calls callback after delay", async () => {
    const fn = vi.fn();
    const { result } = renderHook(() => useDebouncedCallback(fn, 100));
    act(() => {
      result.current();
    });
    expect(fn).not.toHaveBeenCalled();
    await vi.waitFor(() => expect(fn).toHaveBeenCalledTimes(1), { timeout: 300 });
  });

  it("debounces multiple calls", async () => {
    const fn = vi.fn();
    const { result } = renderHook(() => useDebouncedCallback(fn, 100));
    act(() => {
      result.current();
      result.current();
      result.current();
    });
    expect(fn).not.toHaveBeenCalled();
    await vi.waitFor(() => expect(fn).toHaveBeenCalledTimes(1), { timeout: 300 });
  });
});
