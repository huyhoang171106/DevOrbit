import { describe, it, expect } from "vitest";
import { paginate, getPageRange } from "../pagination";

describe("paginate", () => {
  const items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  it("returns first page correctly", () => {
    const result = paginate(items, 1, 3);
    expect(result.items).toEqual([1, 2, 3]);
    expect(result.meta.totalPages).toBe(4);
    expect(result.meta.total).toBe(10);
  });

  it("returns second page", () => {
    const result = paginate(items, 2, 3);
    expect(result.items).toEqual([4, 5, 6]);
  });

  it("handles last page with fewer items", () => {
    const result = paginate(items, 4, 3);
    expect(result.items).toEqual([10]);
  });

  it("handles empty array", () => {
    const result = paginate([], 1, 10);
    expect(result.items).toEqual([]);
    expect(result.meta.totalPages).toBe(0);
  });
});

describe("getPageRange", () => {
  it("returns all pages when total fits within window", () => {
    const range = getPageRange(1, 5);
    expect(range).toEqual([1, 2, 3, 4, 5]);
  });

  it("includes ellipsis for large ranges", () => {
    const range = getPageRange(5, 20);
    expect(range).toContain("ellipsis");
    expect(range[0]).toBe(1);
    expect(range[range.length - 1]).toBe(20);
  });
})
