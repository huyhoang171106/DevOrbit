export interface SubjectProgress {
  subjectId: string;
  subjectName: string;
  credits: number;
  status: "not_started" | "in_progress" | "completed" | "failed";
  semester: number;
  academicYear: string;
  grade?: number;
  attendance?: number;
  assignmentsCompleted?: number;
  totalAssignments?: number;
}

export interface SemesterSummary {
  semester: number;
  year: string;
  subjects: SubjectProgress[];
  registeredCredits: number;
  completedCredits: number;
  currentGPA: number;
}

export interface StudyPlan {
  id: string;
  name: string;
  targetGPA: number;
  startYear: string;
  expectedGraduation: string;
  semesters: SemesterSummary[];
}

export type ProgressFilter = "all" | "in_progress" | "completed" | "not_started";
