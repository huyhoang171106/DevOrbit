import { describe, it, expect } from "vitest";
import { formatDate, getAcademicYear, getSemester, formatRelativeTime } from "../date";

describe("formatDate", () => {
  it("formats a date string correctly", () => {
    const result = formatDate("2026-05-18T00:00:00Z", "en-US");
    expect(result).toContain("May");
    expect(result).toContain("18");
    expect(result).toContain("2026");
  });

  it("returns a string", () => {
    const result = formatDate(new Date());
    expect(typeof result).toBe("string");
    expect(result.length).toBeGreaterThan(0);
  });
});

describe("getAcademicYear", () => {
  it("returns a string in YYYY-YYYY format", () => {
    const result = getAcademicYear();
    expect(result).toMatch(/^\d{4}-\d{4}$/);
  });
});

describe("getSemester", () => {
  it("returns 1 for September", () => {
    expect(getSemester(9)).toBe(1);
  });

  it("returns 2 for January", () => {
    expect(getSemester(1)).toBe(2);
  });

  it("returns 3 for July", () => {
    expect(getSemester(7)).toBe(3);
  });
});

describe("formatRelativeTime", () => {
  it("returns a non-empty string", () => {
    const result = formatRelativeTime(new Date());
    expect(result.length).toBeGreaterThan(0);
  });
});
