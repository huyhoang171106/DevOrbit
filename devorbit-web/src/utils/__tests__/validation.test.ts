import { describe, it, expect } from "vitest";
import {
  validateEmail,
  validatePhone,
  validateStudentId,
  validateRequired,
  validateMinLength,
  validateMaxLength,
} from "../validation";

describe("validateEmail", () => {
  it("returns true for valid email", () => {
    expect(validateEmail("student@uit.edu.vn")).toBe(true);
  });

  it("returns false for invalid email", () => {
    expect(validateEmail("not-an-email")).toBe(false);
  });

  it("returns false for empty string", () => {
    expect(validateEmail("")).toBe(false);
  });
});

describe("validatePhone", () => {
  it("returns true for valid Vietnamese phone", () => {
    expect(validatePhone("0912345678")).toBe(true);
  });

  it("returns false for invalid phone", () => {
    expect(validatePhone("12345")).toBe(false);
  });
});

describe("validateStudentId", () => {
  it("returns true for valid student ID", () => {
    expect(validateStudentId("21520101")).toBe(true);
  });

  it("returns false for short ID", () => {
    expect(validateStudentId("123")).toBe(false);
  });
});

describe("validateRequired", () => {
  it("returns null when value is provided", () => {
    expect(validateRequired("hello", "Name")).toBeNull();
  });

  it("returns error when empty", () => {
    expect(validateRequired("", "Name")).toBe("Name không được để trống");
  });

  it("returns error for whitespace", () => {
    expect(validateRequired("   ", "Name")).toBe("Name không được để trống");
  });
});

describe("validateMinLength", () => {
  it("returns null when long enough", () => {
    expect(validateMinLength("abcdef", 3, "Field")).toBeNull();
  });

  it("returns error when too short", () => {
    expect(validateMinLength("ab", 3, "Field")).toContain("3 ký tự");
  });
});

describe("validateMaxLength", () => {
  it("returns null when within limit", () => {
    expect(validateMaxLength("abc", 5, "Field")).toBeNull();
  });

  it("returns error when over limit", () => {
    expect(validateMaxLength("abcdef", 3, "Field")).toContain("3 ký tự");
  });
});
