import { describe, it, expect } from "vitest";
import { calculateGPA, letterGrade, classification, convertTo4Scale } from "../gpa";

describe("calculateGPA", () => {
  it("returns 0 for empty grades", () => {
    const result = calculateGPA([]);
    expect(result.gpa).toBe(0);
    expect(result.subjectCount).toBe(0);
  });

  it("calculates weighted GPA correctly", () => {
    const grades = [
      { subjectId: "CS101", subjectName: "Lập trình", credits: 4, grade: 3.5, semester: "1", year: "2025-2026" },
      { subjectId: "CS102", subjectName: "Toán", credits: 3, grade: 3.0, semester: "1", year: "2025-2026" },
      { subjectId: "CS103", subjectName: "Tiếng Anh", credits: 2, grade: 4.0, semester: "1", year: "2025-2026" },
    ];
    const result = calculateGPA(grades);
    // (3.5*4 + 3.0*3 + 4.0*2) / (4+3+2) = (14+9+8)/9 = 31/9 = 3.44
    expect(result.gpa).toBe(3.44);
    expect(result.totalCredits).toBe(9);
    expect(result.subjectCount).toBe(3);
  });
});

describe("letterGrade", () => {
  it("returns A+ for 3.8", () => expect(letterGrade(3.8)).toBe("A+"));
  it("returns A for 3.4", () => expect(letterGrade(3.4)).toBe("A"));
  it("returns B+ for 3.0", () => expect(letterGrade(3.0)).toBe("B+"));
  it("returns D for 1.0", () => expect(letterGrade(1.0)).toBe("D"));
  it("returns F for 0.5", () => expect(letterGrade(0.5)).toBe("F"));
});

describe("classification", () => {
  it("returns Xuất sắc for 3.7", () => expect(classification(3.7)).toBe("Xuất sắc"));
  it("returns Giỏi for 3.4", () => expect(classification(3.4)).toBe("Giỏi"));
  it("returns Khá for 2.8", () => expect(classification(2.8)).toBe("Khá"));
  it("returns Yếu for 1.0", () => expect(classification(1.0)).toBe("Yếu"));
});

describe("convertTo4Scale", () => {
  it("converts 9.5 to 4.0", () => expect(convertTo4Scale(9.5)).toBe(4.0));
  it("converts 8.0 to 3.2", () => expect(convertTo4Scale(8.0)).toBe(3.2));
  it("converts 5.0 to 1.6", () => expect(convertTo4Scale(5.0)).toBe(1.6));
  it("converts 2.0 to 0.0", () => expect(convertTo4Scale(2.0)).toBe(0.0));
});
