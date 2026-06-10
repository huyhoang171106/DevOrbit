export interface GradeEntry {
  subjectId: string;
  subjectName: string;
  credits: number;
  grade: number; // 4.0 scale
  semester: string;
  year: string;
}

export interface GPAResult {
  gpa: number;
  totalCredits: number;
  totalGradePoints: number;
  subjectCount: number;
}

export function calculateGPA(grades: GradeEntry[]): GPAResult {
  if (grades.length === 0) {
    return { gpa: 0, totalCredits: 0, totalGradePoints: 0, subjectCount: 0 };
  }
  let totalGradePoints = 0;
  let totalCredits = 0;
  for (const entry of grades) {
    totalGradePoints += entry.grade * entry.credits;
    totalCredits += entry.credits;
  }
  return {
    gpa: totalCredits > 0 ? parseFloat((totalGradePoints / totalCredits).toFixed(2)) : 0,
    totalCredits,
    totalGradePoints,
    subjectCount: grades.length,
  };
}

export function letterGrade(grade: number): string {
  if (grade >= 3.6) return "A+";
  if (grade >= 3.2) return "A";
  if (grade >= 2.8) return "B+";
  if (grade >= 2.4) return "B";
  if (grade >= 2.0) return "C+";
  if (grade >= 1.6) return "C";
  if (grade >= 1.2) return "D+";
  if (grade >= 0.8) return "D";
  return "F";
}

export function classification(gpa: number): string {
  if (gpa >= 3.6) return "Xuất sắc";
  if (gpa >= 3.2) return "Giỏi";
  if (gpa >= 2.4) return "Khá";
  if (gpa >= 1.6) return "Trung bình";
  return "Yếu";
}

export function convertTo4Scale(grade10: number): number {
  if (grade10 >= 9.0) return 4.0;
  if (grade10 >= 8.5) return 3.6;
  if (grade10 >= 8.0) return 3.2;
  if (grade10 >= 7.0) return 2.8;
  if (grade10 >= 6.5) return 2.4;
  if (grade10 >= 5.5) return 2.0;
  if (grade10 >= 5.0) return 1.6;
  if (grade10 >= 4.0) return 1.2;
  if (grade10 >= 3.0) return 0.8;
  return 0.0;
}
