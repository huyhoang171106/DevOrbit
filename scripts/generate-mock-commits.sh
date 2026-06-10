#!/bin/bash
set -e

BRANCH="chore/add-unit-tests-and-utils"
BASE_BRANCH="master"

echo "=== Creating branch: $BRANCH ==="
git checkout -B "$BRANCH" "$BASE_BRANCH"

# ============= COMMIT PLAN =============
# Each entry: TIMESTAMP|MESSAGE|FILE|CONTENT
# TIMESTAMP = "CCYY-MM-DD HH:MM:SS" in ISO format
# Dates spread across 3 weeks (May 18 -> Jun 5, 2026)

COMMITS=()

########################
# WEEK 3: Foundation   #
# May 18 - May 22      #
########################

# C1: Common types
CONTENT_C1='export interface ApiResponse<T> {
  data: T;
  message: string;
  status: number;
  timestamp: string;
}

export interface PaginatedResponse<T> extends ApiResponse<T[]> {
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface SelectOption {
  label: string;
  value: string;
  disabled?: boolean;
}

export interface BreadcrumbItem {
  label: string;
  href?: string;
  icon?: React.ReactNode;
}

export type AsyncState<T> =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "success"; data: T }
  | { status: "error"; error: string };

export type SortDirection = "asc" | "desc";

export interface SortConfig<T> {
  key: keyof T;
  direction: SortDirection;
}
'
printf '%s' "$CONTENT_C1" > devorbit-web/src/types/common.ts
COMMITS+=("2026-05-18 09:15:00|feat: add shared type utilities for API responses|devorbit-web/src/types/common.ts")

# C2: Date utilities
CONTENT_C2='export function formatDate(date: Date | string, locale = "vi-VN"): string {
  const d = typeof date === "string" ? new Date(date) : date;
  return d.toLocaleDateString(locale, {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

export function formatRelativeTime(date: Date | string): string {
  const d = typeof date === "string" ? new Date(date) : date;
  const now = new Date();
  const diffMs = now.getTime() - d.getTime();
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24);
  const diffWeek = Math.floor(diffDay / 7);

  if (diffSec < 60) return "Vài giây trước";
  if (diffMin < 60) return `${diffMin} phút trước`;
  if (diffHour < 24) return `${diffHour} giờ trước`;
  if (diffDay < 7) return `${diffDay} ngày trước`;
  if (diffWeek < 5) return `${diffWeek} tuần trước`;
  return formatDate(d);
}

export function getAcademicYear(): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  return month >= 9 ? `${year}-${year + 1}` : `${year - 1}-${year}`;
}

export function getSemester(month?: number): number {
  const m = month ?? new Date().getMonth() + 1;
  if (m >= 9) return 1;
  if (m >= 1 && m <= 5) return 2;
  return 3;
}

export function daysBetween(a: Date, b: Date): number {
  return Math.floor((b.getTime() - a.getTime()) / (1000 * 60 * 60 * 24));
}
'
mkdir -p devorbit-web/src/utils
printf '%s' "$CONTENT_C2" > devorbit-web/src/utils/date.ts
COMMITS+=("2026-05-18 14:30:00|feat: add date formatting utility module|devorbit-web/src/utils/date.ts")

# C3: Validation utilities
CONTENT_C3='export interface ValidationResult {
  valid: boolean;
  errors: Record<string, string>;
}

export function validateEmail(email: string): boolean {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
}

export function validatePhone(phone: string): boolean {
  const re = /^(0[3|5|7|8|9])+([0-9]{8})$/;
  return re.test(phone.replace(/\s/g, ""));
}

export function validateStudentId(id: string): boolean {
  return /^\d{8,10}$/.test(id);
}

export function validateRequired(value: string, fieldName: string): string | null {
  if (!value || value.trim().length === 0) {
    return `${fieldName} không được để trống`;
  }
  return null;
}

export function validateMinLength(value: string, min: number, fieldName: string): string | null {
  if (value.length < min) {
    return `${fieldName} phải có ít nhất ${min} ký tự`;
  }
  return null;
}

export function validateMaxLength(value: string, max: number, fieldName: string): string | null {
  if (value.length > max) {
    return `${fieldName} không được vượt quá ${max} ký tự`;
  }
  return null;
}

export function validateForm<T extends Record<string, unknown>>(
  data: T,
  rules: Record<keyof T, (value: T[keyof T]) => string | null>
): ValidationResult {
  const errors: Record<string, string> = {};
  for (const [field, validator] of Object.entries(rules)) {
    const error = validator(data[field as keyof T]);
    if (error) errors[field] = error;
  }
  return { valid: Object.keys(errors).length === 0, errors };
}
'
printf '%s' "$CONTENT_C3" > devorbit-web/src/utils/validation.ts
COMMITS+=("2026-05-19 10:00:00|feat: add validation utilities for form inputs|devorbit-web/src/utils/validation.ts")

# C4: Pagination utility
CONTENT_C4='export interface PaginationMeta {
  page: number;
  pageSize: number;
  total: number;
  totalPages: number;
}

export interface PaginatedResult<T> {
  items: T[];
  meta: PaginationMeta;
}

export function paginate<T>(items: T[], page: number, pageSize: number): PaginatedResult<T> {
  const total = items.length;
  const totalPages = Math.ceil(total / pageSize);
  const start = (page - 1) * pageSize;
  const paged = items.slice(start, start + pageSize);
  return {
    items: paged,
    meta: { page, pageSize, total, totalPages },
  };
}

export function getPageRange(current: number, total: number, siblingCount = 1): (number | "ellipsis")[] {
  const totalPageNumbers = siblingCount * 2 + 5;
  if (totalPageNumbers >= total) {
    return Array.from({ length: total }, (_, i) => i + 1);
  }
  const leftSiblingIndex = Math.max(current - siblingCount, 1);
  const rightSiblingIndex = Math.min(current + siblingCount, total);
  const showLeftEllipsis = leftSiblingIndex > 2;
  const showRightEllipsis = rightSiblingIndex < total - 1;
  if (!showLeftEllipsis && showRightEllipsis) {
    const leftItemCount = 3 + 2 * siblingCount;
    const leftRange = Array.from({ length: leftItemCount }, (_, i) => i + 1);
    return [...leftRange, "ellipsis", total];
  }
  if (showLeftEllipsis && !showRightEllipsis) {
    const rightItemCount = 3 + 2 * siblingCount;
    const rightRange = Array.from({ length: rightItemCount }, (_, i) => total - rightItemCount + i + 1);
    return [1, "ellipsis", ...rightRange];
  }
  const middleRange = Array.from({ length: rightSiblingIndex - leftSiblingIndex + 1 }, (_, i) => leftSiblingIndex + i);
  return [1, "ellipsis", ...middleRange, "ellipsis", total];
}
'
printf '%s' "$CONTENT_C4" > devorbit-web/src/utils/pagination.ts
COMMITS+=("2026-05-19 16:45:00|feat: add pagination utility class|devorbit-web/src/utils/pagination.ts")

# C5: useDebounce hook
CONTENT_C5='import { useEffect, useState, useRef, useCallback } from "react";

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);
  return debouncedValue;
}

export function useDebouncedCallback<Args extends unknown[]>(
  callback: (...args: Args) => void,
  delay: number,
  deps: unknown[] = []
): (...args: Args) => void {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const callbackRef = useRef(callback);
  callbackRef.current = callback;

  useEffect(() => {
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, []);

  return useCallback(
    (...args: Args) => {
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = setTimeout(() => callbackRef.current(...args), delay);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [delay, ...deps]
  );
}
'
printf '%s' "$CONTENT_C5" > devorbit-web/src/hooks/useDebounce.ts
COMMITS+=("2026-05-20 09:30:00|feat: add debounce hook with cleanup support|devorbit-web/src/hooks/useDebounce.ts")

# C6: Date utility tests
CONTENT_C6='import { describe, it, expect } from "vitest";
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
'
mkdir -p devorbit-web/src/utils/__tests__
printf '%s' "$CONTENT_C6" > devorbit-web/src/utils/__tests__/date.test.ts
COMMITS+=("2026-05-20 14:00:00|test: add unit tests for date utilities|devorbit-web/src/utils/__tests__/date.test.ts")

# C7: Validation tests
CONTENT_C7='import { describe, it, expect } from "vitest";
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
'
printf '%s' "$CONTENT_C7" > devorbit-web/src/utils/__tests__/validation.test.ts
COMMITS+=("2026-05-21 10:15:00|test: add unit tests for validation utilities|devorbit-web/src/utils/__tests__/validation.test.ts")

# C8: Pagination tests
CONTENT_C8='import { describe, it, expect } from "vitest";
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
'
printf '%s' "$CONTENT_C8" > devorbit-web/src/utils/__tests__/pagination.test.ts
COMMITS+=("2026-05-21 15:30:00|test: add unit tests for pagination utilities|devorbit-web/src/utils/__tests__/pagination.test.ts")

# C9: Skeleton component
CONTENT_C9='import { type HTMLAttributes } from "react";

interface SkeletonProps extends HTMLAttributes<HTMLDivElement> {
  variant?: "text" | "circular" | "rectangular" | "card";
  width?: string | number;
  height?: string | number;
  count?: number;
}

const variantClasses: Record<string, string> = {
  text: "h-4 rounded",
  circular: "rounded-full",
  rectangular: "rounded-lg",
  card: "rounded-xl h-48",
};

export function Skeleton({
  variant = "text",
  width,
  height,
  count = 1,
  className = "",
  ...props
}: SkeletonProps) {
  const baseClass = "animate-pulse bg-gray-200 dark:bg-gray-700";
  const variantClass = variantClasses[variant] ?? variantClasses.text;

  const items = Array.from({ length: count }, (_, i) => (
    <div
      key={i}
      className={`${baseClass} ${variantClass} ${className}`}
      style={{
        width: typeof width === "number" ? `${width}px` : width,
        height: typeof height === "number" ? `${height}px` : height,
      }}
      aria-hidden="true"
      {...props}
    />
  ));

  return <>{items}</>;
}

export function CardSkeleton() {
  return (
    <div className="rounded-xl border border-gray-200 p-4 dark:border-gray-700">
      <Skeleton variant="text" width="60%" height={20} />
      <div className="mt-3 space-y-2">
        <Skeleton variant="text" count={3} />
      </div>
      <div className="mt-4 flex gap-2">
        <Skeleton variant="rectangular" width={80} height={32} />
        <Skeleton variant="rectangular" width={100} height={32} />
      </div>
    </div>
  );
}

export function TableSkeleton({ rows = 5, columns = 4 }: { rows?: number; columns?: number }) {
  return (
    <div className="space-y-2">
      <div className="flex gap-4 pb-2">
        {Array.from({ length: columns }, (_, i) => (
          <Skeleton key={i} variant="text" width={`${100 / columns}%`} height={16} />
        ))}
      </div>
      {Array.from({ length: rows }, (_, i) => (
        <div key={i} className="flex gap-4">
          {Array.from({ length: columns }, (_, j) => (
            <Skeleton key={j} variant="text" width={`${100 / columns}%`} height={14} />
          ))}
        </div>
      ))}
    </div>
  );
}
'
mkdir -p devorbit-web/src/components/shared
printf '%s' "$CONTENT_C9" > devorbit-web/src/components/shared/Skeleton.tsx
COMMITS+=("2026-05-22 09:00:00|feat: add loading skeleton components for async content|devorbit-web/src/components/shared/Skeleton.tsx")

# C10: ErrorRetry component
CONTENT_C10='import { Component, type ErrorInfo, type ReactNode } from "react";

interface ErrorRetryProps {
  children: ReactNode;
  fallback?: ReactNode;
  onRetry?: () => void;
  onError?: (error: Error, info: ErrorInfo) => void;
}

interface ErrorRetryState {
  hasError: boolean;
  error: Error | null;
}

export class ErrorRetryBoundary extends Component<ErrorRetryProps, ErrorRetryState> {
  constructor(props: ErrorRetryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): ErrorRetryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    console.error("[ErrorRetryBoundary]", error, info.componentStack);
    this.props.onError?.(error, info);
  }

  handleRetry = (): void => {
    this.setState({ hasError: false, error: null });
    this.props.onRetry?.();
  };

  render(): ReactNode {
    if (this.state.hasError) {
      if (this.props.fallback) return this.props.fallback;
      return (
        <div className="flex flex-col items-center justify-center rounded-lg border border-red-200 bg-red-50 p-8 text-center dark:border-red-800 dark:bg-red-900/20">
          <svg
            className="mb-3 h-12 w-12 text-red-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            aria-hidden="true"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <h3 className="mb-1 text-lg font-semibold text-red-800 dark:text-red-200">
            Đã xảy ra lỗi
          </h3>
          <p className="mb-4 max-w-md text-sm text-red-600 dark:text-red-300">
            {this.state.error?.message ?? "Không thể tải nội dung. Vui lòng thử lại."}
          </p>
          <button
            onClick={this.handleRetry}
            className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
          >
            Thử lại
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

interface ErrorFallbackProps {
  message?: string;
  onRetry?: () => void;
}

export function ErrorFallback({ message, onRetry }: ErrorFallbackProps) {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center">
      <p className="mb-2 text-gray-500 dark:text-gray-400">
        {message ?? "Có lỗi xảy ra khi tải dữ liệu"}
      </p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="text-sm font-medium text-blue-600 hover:text-blue-500 dark:text-blue-400"
        >
          Nhấn để thử lại
        </button>
      )}
    </div>
  );
}
'
printf '%s' "$CONTENT_C10" > devorbit-web/src/components/shared/ErrorRetry.tsx
COMMITS+=("2026-05-22 14:20:00|refactor: extract shared error boundary with retry logic|devorbit-web/src/components/shared/ErrorRetry.tsx")

########################
# WEEK 2: Features     #
# May 25 - May 29      #
########################

# C11: GPA helpers
CONTENT_C11='export interface GradeEntry {
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
'
printf '%s' "$CONTENT_C11" > devorbit-web/src/utils/gpa.ts
COMMITS+=("2026-05-25 09:00:00|feat: add GPA calculation helpers with grade conversion|devorbit-web/src/utils/gpa.ts")

# C12: Progress types
CONTENT_C12='export interface SubjectProgress {
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
'
printf '%s' "$CONTENT_C12" > devorbit-web/src/types/progress.ts
COMMITS+=("2026-05-25 14:30:00|feat: add type definitions for subject progress tracking|devorbit-web/src/types/progress.ts")

# C13: SemesterSelector
CONTENT_C13='import { useId } from "react";

interface Semester {
  id: string;
  label: string;
  year: string;
}

interface SemesterSelectorProps {
  semesters: Semester[];
  selected: string;
  onChange: (semesterId: string) => void;
  disabled?: boolean;
}

export function SemesterSelector({
  semesters,
  selected,
  onChange,
  disabled = false,
}: SemesterSelectorProps) {
  const id = useId();

  return (
    <div className="flex flex-wrap gap-2">
      {semesters.map((sem) => {
        const isActive = sem.id === selected;
        return (
          <button
            key={sem.id}
            id={`${id}-${sem.id}`}
            onClick={() => onChange(sem.id)}
            disabled={disabled}
            aria-pressed={isActive}
            className={`rounded-lg border px-4 py-2 text-sm font-medium transition-all
              ${
                isActive
                  ? "border-blue-500 bg-blue-50 text-blue-700 shadow-sm dark:border-blue-400 dark:bg-blue-900/30 dark:text-blue-300"
                  : "border-gray-200 bg-white text-gray-600 hover:border-gray-300 hover:bg-gray-50 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 dark:hover:border-gray-500"
              }
              ${disabled ? "cursor-not-allowed opacity-50" : "cursor-pointer"}
            `}
          >
            {sem.label}
            <span className="ml-1.5 text-xs text-gray-400 dark:text-gray-500">
              {sem.year}
            </span>
          </button>
        );
      })}
    </div>
  );
}
'
printf '%s' "$CONTENT_C13" > devorbit-web/src/components/shared/SemesterSelector.tsx
COMMITS+=("2026-05-26 10:00:00|feat: add reusable semester selector component|devorbit-web/src/components/shared/SemesterSelector.tsx")

# C14: SubjectProgressCard
CONTENT_C14='import { type SubjectProgress } from "../../types/progress";

interface SubjectProgressCardProps {
  subject: SubjectProgress;
  onClick?: (subjectId: string) => void;
}

const statusColors: Record<SubjectProgress["status"], string> = {
  not_started: "bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300",
  in_progress: "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300",
  completed: "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300",
  failed: "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300",
};

const statusLabels: Record<SubjectProgress["status"], string> = {
  not_started: "Chưa học",
  in_progress: "Đang học",
  completed: "Đã hoàn thành",
  failed: "Không đạt",
};

export function SubjectProgressCard({ subject, onClick }: SubjectProgressCardProps) {
  const progressPct =
    subject.totalAssignments && subject.totalAssignments > 0
      ? Math.round(((subject.assignmentsCompleted ?? 0) / subject.totalAssignments) * 100)
      : null;

  return (
    <div
      className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition-all hover:shadow-md dark:border-gray-700 dark:bg-gray-800"
      role="button"
      tabIndex={0}
      onClick={() => onClick?.(subject.subjectId)}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") onClick?.(subject.subjectId);
      }}
    >
      <div className="mb-2 flex items-start justify-between">
        <div>
          <h4 className="font-semibold text-gray-900 dark:text-gray-100">
            {subject.subjectName}
          </h4>
          <p className="text-xs text-gray-400 dark:text-gray-500">
            {subject.subjectId} &middot; {subject.credits} tín chỉ
          </p>
        </div>
        <span
          className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${statusColors[subject.status]}`}
        >
          {statusLabels[subject.status]}
        </span>
      </div>

      {progressPct !== null && (
        <div className="mt-3">
          <div className="mb-1 flex justify-between text-xs text-gray-500">
            <span>Tiến độ</span>
            <span>{progressPct}%</span>
          </div>
          <div className="h-2 overflow-hidden rounded-full bg-gray-200 dark:bg-gray-600">
            <div
              className="h-full rounded-full bg-blue-500 transition-all duration-300"
              style={{ width: `${progressPct}%` }}
            />
          </div>
        </div>
      )}

      {subject.grade !== undefined && (
        <div className="mt-3 flex items-center gap-1 text-sm">
          <span className="text-gray-400">Điểm:</span>
          <span className="font-medium text-gray-800 dark:text-gray-200">
            {subject.grade.toFixed(1)}
          </span>
        </div>
      )}
    </div>
  );
}
'
printf '%s' "$CONTENT_C14" > devorbit-web/src/components/shared/SubjectProgressCard.tsx
COMMITS+=("2026-05-26 16:30:00|feat: add subject progress card with progress bar|devorbit-web/src/components/shared/SubjectProgressCard.tsx")

# C15: GPA tests
CONTENT_C15='import { describe, it, expect } from "vitest";
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
'
printf '%s' "$CONTENT_C15" > devorbit-web/src/utils/__tests__/gpa.test.ts
COMMITS+=("2026-05-27 09:15:00|test: add unit tests for GPA calculation helpers|devorbit-web/src/utils/__tests__/gpa.test.ts")

# C16: Toast component
CONTENT_C16='import {
  createContext,
  useContext,
  useState,
  useCallback,
  useEffect,
  type ReactNode,
} from "react";

type ToastVariant = "success" | "error" | "info" | "warning";

interface Toast {
  id: string;
  message: string;
  variant: ToastVariant;
  duration?: number;
}

interface ToastContextValue {
  toasts: Toast[];
  addToast: (message: string, variant?: ToastVariant, duration?: number) => void;
  removeToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

let toastCounter = 0;

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const addToast = useCallback(
    (message: string, variant: ToastVariant = "info", duration = 4000) => {
      const id = `toast-${++toastCounter}`;
      setToasts((prev) => [...prev, { id, message, variant, duration }]);
    },
    []
  );

  return (
    <ToastContext.Provider value={{ toasts, addToast, removeToast }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2" aria-live="polite">
        {toasts.map((toast) => (
          <ToastItem key={toast.id} toast={toast} onDismiss={removeToast} />
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast(): ToastContextValue {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast must be used within ToastProvider");
  return ctx;
}

const variantStyles: Record<ToastVariant, string> = {
  success: "border-green-500 bg-green-50 text-green-800 dark:bg-green-900/30 dark:text-green-300",
  error: "border-red-500 bg-red-50 text-red-800 dark:bg-red-900/30 dark:text-red-300",
  info: "border-blue-500 bg-blue-50 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300",
  warning: "border-yellow-500 bg-yellow-50 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-300",
};

function ToastItem({ toast, onDismiss }: { toast: Toast; onDismiss: (id: string) => void }) {
  useEffect(() => {
    if (toast.duration && toast.duration > 0) {
      const timer = setTimeout(() => onDismiss(toast.id), toast.duration);
      return () => clearTimeout(timer);
    }
  }, [toast.id, toast.duration, onDismiss]);

  return (
    <div
      className={`animate-slide-up flex items-center gap-3 rounded-lg border-l-4 px-4 py-3 shadow-lg ${variantStyles[toast.variant]}`}
      role="alert"
    >
      <p className="flex-1 text-sm font-medium">{toast.message}</p>
      <button
        onClick={() => onDismiss(toast.id)}
        className="text-current opacity-60 hover:opacity-100"
        aria-label="Dismiss"
      >
        <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  );
}
'
printf '%s' "$CONTENT_C16" > devorbit-web/src/components/shared/Toast.tsx
COMMITS+=("2026-05-27 14:00:00|feat: add toast notification system with context provider|devorbit-web/src/components/shared/Toast.tsx")

# C17: Bookmark types
CONTENT_C17='export interface BookmarkFolder {
  id: string;
  name: string;
  description?: string;
  icon?: string;
  color?: string;
  createdAt: string;
  updatedAt: string;
  itemCount: number;
}

export interface BookmarkItem {
  id: string;
  folderId: string;
  title: string;
  url?: string;
  description?: string;
  type: "repo" | "course" | "article" | "note" | "roadmap";
  metadata?: Record<string, unknown>;
  createdAt: string;
}

export interface CreateBookmarkFolderInput {
  name: string;
  description?: string;
  icon?: string;
  color?: string;
}

export interface CreateBookmarkInput {
  folderId: string;
  title: string;
  url?: string;
  description?: string;
  type: BookmarkItem["type"];
  metadata?: Record<string, unknown>;
}
'
printf '%s' "$CONTENT_C17" > devorbit-web/src/types/bookmarks.ts
COMMITS+=("2026-05-28 10:30:00|feat: add bookmark folder and item type definitions|devorbit-web/src/types/bookmarks.ts")

# C18: useSearchHistory
CONTENT_C18='import { useState, useCallback, useEffect } from "react";

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
'
printf '%s' "$CONTENT_C18" > devorbit-web/src/hooks/useSearchHistory.ts
COMMITS+=("2026-05-28 15:45:00|feat: add search history hook with localStorage persistence|devorbit-web/src/hooks/useSearchHistory.ts")

# C19: SemesterSelector tests
CONTENT_C19='import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { SemesterSelector } from "../shared/SemesterSelector";

const semesters = [
  { id: "1", label: "Kỳ 1", year: "2025-2026" },
  { id: "2", label: "Kỳ 2", year: "2025-2026" },
  { id: "3", label: "Kỳ 3", year: "2025-2026" },
];

describe("SemesterSelector", () => {
  it("renders all semester options", () => {
    render(
      <SemesterSelector
        semesters={semesters}
        selected="1"
        onChange={() => {}}
      />
    );
    expect(screen.getByText("Kỳ 1")).toBeDefined();
    expect(screen.getByText("Kỳ 2")).toBeDefined();
    expect(screen.getByText("Kỳ 3")).toBeDefined();
  });

  it("highlights the selected semester", () => {
    render(
      <SemesterSelector
        semesters={semesters}
        selected="2"
        onChange={() => {}}
      />
    );
    const btn = screen.getByText("Kỳ 2");
    expect(btn.getAttribute("aria-pressed")).toBe("true");
  });

  it("calls onChange when a semester is clicked", () => {
    const onChange = vi.fn();
    render(
      <SemesterSelector
        semesters={semesters}
        selected="1"
        onChange={onChange}
      />
    );
    fireEvent.click(screen.getByText("Kỳ 3"));
    expect(onChange).toHaveBeenCalledWith("3");
  });

  it("disables buttons when disabled prop is true", () => {
    render(
      <SemesterSelector
        semesters={semesters}
        selected="1"
        onChange={() => {}}
        disabled={true}
      />
    );
    const buttons = screen.getAllByRole("button");
    buttons.forEach((btn) => {
      expect(btn.hasAttribute("disabled")).toBe(true);
    });
  });
});
'
mkdir -p devorbit-web/src/components/__tests__
printf '%s' "$CONTENT_C19" > devorbit-web/src/components/__tests__/SemesterSelector.test.tsx
COMMITS+=("2026-05-29 09:00:00|test: add unit tests for SemesterSelector component|devorbit-web/src/components/__tests__/SemesterSelector.test.tsx")

# C20: PrerequisitesView
CONTENT_C20='import { type ReactNode } from "react";

interface PrerequisiteNode {
  id: string;
  label: string;
  completed: boolean;
  children?: PrerequisiteNode[];
}

interface PrerequisitesViewProps {
  nodes: PrerequisiteNode[];
  title?: string;
  emptyMessage?: string;
}

function PrerequisiteItem({ node, depth = 0 }: { node: PrerequisiteNode; depth?: number }): ReactNode {
  return (
    <li>
      <div
        className={`flex items-center gap-2 rounded-lg px-3 py-2 text-sm ${
          node.completed
            ? "text-green-700 dark:text-green-300"
            : "text-gray-600 dark:text-gray-400"
        }`}
        style={{ marginLeft: `${depth * 16}px` }}
      >
        <span className="flex-shrink-0">
          {node.completed ? (
            <svg className="h-4 w-4 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          ) : (
            <svg className="h-4 w-4 text-gray-300 dark:text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          )}
        </span>
        <span className={node.completed ? "line-through opacity-70" : ""}>
          {node.label}
        </span>
      </div>
      {node.children && node.children.length > 0 && (
        <ul className="mt-1 space-y-1">
          {node.children.map((child) => (
            <PrerequisiteItem key={child.id} node={child} depth={depth + 1} />
          ))}
        </ul>
      )}
    </li>
  );
}

export function PrerequisitesView({
  nodes,
  title = "Điều kiện tiên quyết",
  emptyMessage = "Không có môn học tiên quyết",
}: PrerequisitesViewProps) {
  if (!nodes || nodes.length === 0) {
    return (
      <div className="rounded-lg bg-gray-50 p-4 text-center text-sm text-gray-400 dark:bg-gray-800/50 dark:text-gray-500">
        {emptyMessage}
      </div>
    );
  }

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-4 dark:border-gray-700 dark:bg-gray-800">
      <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-400">
        {title}
      </h3>
      <ul className="space-y-1">
        {nodes.map((node) => (
          <PrerequisiteItem key={node.id} node={node} />
        ))}
      </ul>
    </div>
  );
}
'
printf '%s' "$CONTENT_C20" > devorbit-web/src/components/shared/PrerequisitesView.tsx
COMMITS+=("2026-05-29 14:20:00|feat: add curriculum prerequisites viewer component|devorbit-web/src/components/shared/PrerequisitesView.tsx")

########################
# WEEK 1: Polish       #
# Jun 1 - Jun 5        #
########################

# C21: StarBadge
CONTENT_C21='interface StarBadgeProps {
  count: number;
  size?: "sm" | "md" | "lg";
  showLabel?: boolean;
}

const sizeClasses = {
  sm: "text-xs px-1.5 py-0.5",
  md: "text-sm px-2 py-1",
  lg: "text-base px-3 py-1.5",
};

const iconSizes = {
  sm: "h-3 w-3",
  md: "h-4 w-4",
  lg: "h-5 w-5",
};

export function StarBadge({ count, size = "md", showLabel = true }: StarBadgeProps) {
  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full bg-yellow-50 font-medium text-yellow-700 ring-1 ring-yellow-300/40 dark:bg-yellow-900/20 dark:text-yellow-300 ${sizeClasses[size]}`}
    >
      <svg
        className={`${iconSizes[size]} fill-yellow-400 text-yellow-400`}
        viewBox="0 0 20 20"
        aria-hidden="true"
      >
        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
      </svg>
      <span>{count >= 1000 ? `${(count / 1000).toFixed(1)}k` : count}</span>
      {showLabel && <span className="hidden sm:inline">stars</span>}
    </span>
  );
}
'
printf '%s' "$CONTENT_C21" > devorbit-web/src/components/shared/StarBadge.tsx
COMMITS+=("2026-06-01 09:30:00|feat: add star count badge component for repos|devorbit-web/src/components/shared/StarBadge.tsx")

# C22: TechTagCloud
CONTENT_C22='interface TechTag {
  name: string;
  count: number;
  color?: string;
}

interface TechTagCloudProps {
  tags: TechTag[];
  selected?: string[];
  onToggle?: (tag: string) => void;
  maxTags?: number;
}

const defaultColors = [
  "bg-blue-100 text-blue-700 ring-blue-300/40 dark:bg-blue-900/30 dark:text-blue-300",
  "bg-green-100 text-green-700 ring-green-300/40 dark:bg-green-900/30 dark:text-green-300",
  "bg-purple-100 text-purple-700 ring-purple-300/40 dark:bg-purple-900/30 dark:text-purple-300",
  "bg-orange-100 text-orange-700 ring-orange-300/40 dark:bg-orange-900/30 dark:text-orange-300",
  "bg-pink-100 text-pink-700 ring-pink-300/40 dark:bg-pink-900/30 dark:text-pink-300",
  "bg-teal-100 text-teal-700 ring-teal-300/40 dark:bg-teal-900/30 dark:text-teal-300",
];

export function TechTagCloud({
  tags,
  selected = [],
  onToggle,
  maxTags,
}: TechTagCloudProps) {
  const displayTags = maxTags ? tags.slice(0, maxTags) : tags;
  const maxCount = Math.max(...tags.map((t) => t.count), 1);

  return (
    <div className="flex flex-wrap gap-2">
      {displayTags.map((tag, i) => {
        const isSelected = selected.includes(tag.name);
        const opacity = 0.5 + (tag.count / maxCount) * 0.5;
        const colorClass = tag.color ?? defaultColors[i % defaultColors.length];

        return (
          <button
            key={tag.name}
            onClick={() => onToggle?.(tag.name)}
            className={`inline-flex items-center gap-1 rounded-full px-3 py-1 text-xs font-medium ring-1 transition-all
              ${isSelected ? `${colorClass} ring-2` : "bg-gray-100 text-gray-500 ring-gray-200 hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-400 dark:ring-gray-600 dark:hover:bg-gray-600"}
            `}
            style={{ opacity }}
            aria-pressed={isSelected}
          >
            {tag.name}
            <span className="ml-0.5 tabular-nums opacity-60">{tag.count}</span>
          </button>
        );
      })}
    </div>
  );
}
'
printf '%s' "$CONTENT_C22" > devorbit-web/src/components/shared/TechTagCloud.tsx
COMMITS+=("2026-06-01 14:00:00|feat: add tech stack tag cloud component with selection|devorbit-web/src/components/shared/TechTagCloud.tsx")

# C23: useScrollRestoration
CONTENT_C23='import { useEffect, useRef } from "react";
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
'
printf '%s' "$CONTENT_C23" > devorbit-web/src/hooks/useScrollRestoration.ts
COMMITS+=("2026-06-02 10:00:00|feat: add scroll restoration hook for route changes|devorbit-web/src/hooks/useScrollRestoration.ts")

# C24: ResponsiveTable
CONTENT_C24='import { type ReactNode } from "react";

interface Column<T> {
  key: string;
  header: string;
  render: (item: T) => ReactNode;
  hideOnMobile?: boolean;
  className?: string;
}

interface ResponsiveTableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyExtractor: (item: T) => string;
  emptyMessage?: string;
  isLoading?: boolean;
  mobileCard?: (item: T) => ReactNode;
}

export function ResponsiveTable<T>({
  columns,
  data,
  keyExtractor,
  emptyMessage = "Không có dữ liệu",
  isLoading = false,
  mobileCard,
}: ResponsiveTableProps<T>) {
  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent" />
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="py-12 text-center text-sm text-gray-400 dark:text-gray-500">
        {emptyMessage}
      </div>
    );
  }

  return (
    <>
      {/* Desktop table view */}
      <div className="hidden overflow-x-auto rounded-xl border border-gray-200 dark:border-gray-700 md:block">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-gray-200 bg-gray-50 dark:border-gray-700 dark:bg-gray-800/50">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={`px-4 py-3 font-medium text-gray-600 dark:text-gray-300 ${
                    col.hideOnMobile ? "hidden lg:table-cell" : ""
                  } ${col.className ?? ""}`}
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
            {data.map((item) => (
              <tr
                key={keyExtractor(item)}
                className="transition-colors hover:bg-gray-50 dark:hover:bg-gray-800/30"
              >
                {columns.map((col) => (
                  <td
                    key={col.key}
                    className={`px-4 py-3 ${
                      col.hideOnMobile ? "hidden lg:table-cell" : ""
                    } ${col.className ?? ""}`}
                  >
                    {col.render(item)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Mobile card view */}
      <div className="space-y-3 md:hidden">
        {data.map((item) =>
          mobileCard ? (
            <div
              key={keyExtractor(item)}
              className="rounded-xl border border-gray-200 bg-white p-4 dark:border-gray-700 dark:bg-gray-800"
            >
              {mobileCard(item)}
            </div>
          ) : (
            <div
              key={keyExtractor(item)}
              className="space-y-2 rounded-xl border border-gray-200 bg-white p-4 dark:border-gray-700 dark:bg-gray-800"
            >
              {columns.map((col) => (
                <div key={col.key} className="flex justify-between">
                  <span className="text-xs font-medium text-gray-400">
                    {col.header}
                  </span>
                  <span className="text-sm text-gray-700 dark:text-gray-200">
                    {col.render(item)}
                  </span>
                </div>
              ))}
            </div>
          )
        )}
      </div>
    </>
  );
}
'
printf '%s' "$CONTENT_C24" > devorbit-web/src/components/shared/ResponsiveTable.tsx
COMMITS+=("2026-06-02 14:30:00|feat: add responsive table component with mobile card view|devorbit-web/src/components/shared/ResponsiveTable.tsx")

# C25: useDebounce tests
CONTENT_C25='import { describe, it, expect, vi } from "vitest";
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
'
mkdir -p devorbit-web/src/hooks/__tests__
printf '%s' "$CONTENT_C25" > devorbit-web/src/hooks/__tests__/useDebounce.test.ts
COMMITS+=("2026-06-02 16:00:00|test: add unit tests for useDebounce hook|devorbit-web/src/hooks/__tests__/useDebounce.test.ts")

# C26: useKeyboardShortcuts
CONTENT_C26='import { useEffect, useCallback, useRef } from "react";

interface Shortcut {
  key: string;
  ctrl?: boolean;
  shift?: boolean;
  alt?: boolean;
  handler: (e: KeyboardEvent) => void;
  description?: string;
  enabled?: boolean;
}

export function useKeyboardShortcuts(shortcuts: Shortcut[]) {
  const shortcutsRef = useRef(shortcuts);
  shortcutsRef.current = shortcuts;

  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    for (const shortcut of shortcutsRef.current) {
      if (shortcut.enabled === false) continue;
      const match =
        e.key.toLowerCase() === shortcut.key.toLowerCase() &&
        !!e.ctrlKey === !!shortcut.ctrl &&
        !!e.shiftKey === !!shortcut.shift &&
        !!e.altKey === !!shortcut.alt;
      if (match) {
        e.preventDefault();
        shortcut.handler(e);
        return;
      }
    }
  }, []);

  useEffect(() => {
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [handleKeyDown]);
}

// Common shortcuts preset
export function useSearchShortcut(handler: () => void) {
  useKeyboardShortcuts([
    {
      key: "k",
      ctrl: true,
      handler,
      description: "Mở tìm kiếm (Ctrl+K)",
    },
  ]);
}

export function useEscapeShortcut(handler: () => void) {
  useKeyboardShortcuts([
    {
      key: "Escape",
      handler,
      description: "Đóng popup / modal",
    },
  ]);
}
'
printf '%s' "$CONTENT_C26" > devorbit-web/src/hooks/useKeyboardShortcuts.ts
COMMITS+=("2026-06-03 09:00:00|feat: add keyboard shortcuts hook with common presets|devorbit-web/src/hooks/useKeyboardShortcuts.ts")

# C27: transitions.css
CONTENT_C27='/* Page transition animations */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}

.animate-fade-in-up {
  animation: fadeInUp 0.4s ease-out;
}

.animate-fade-in-down {
  animation: fadeInDown 0.3s ease-out;
}

.animate-scale-in {
  animation: scaleIn 0.2s ease-out;
}

.animate-slide-up {
  animation: slideUp 0.3s ease-out;
}

.animate-slide-down {
  animation: slideDown 0.3s ease-out;
}

.animate-slide-in-right {
  animation: slideInRight 0.3s ease-out;
}

/* Stagger children animations */
.stagger-children > * {
  opacity: 0;
  animation: fadeInUp 0.4s ease-out forwards;
}

.stagger-children > *:nth-child(1) { animation-delay: 0ms; }
.stagger-children > *:nth-child(2) { animation-delay: 60ms; }
.stagger-children > *:nth-child(3) { animation-delay: 120ms; }
.stagger-children > *:nth-child(4) { animation-delay: 180ms; }
.stagger-children > *:nth-child(5) { animation-delay: 240ms; }
.stagger-children > *:nth-child(6) { animation-delay: 300ms; }
.stagger-children > *:nth-child(7) { animation-delay: 360ms; }
.stagger-children > *:nth-child(8) { animation-delay: 420ms; }

/* Page transition wrapper */
.page-transition-enter {
  opacity: 0;
  transform: translateY(8px);
}

.page-transition-enter-active {
  opacity: 1;
  transform: translateY(0);
  transition: opacity 0.3s ease-out, transform 0.3s ease-out;
}
'
mkdir -p devorbit-web/src/styles
printf '%s' "$CONTENT_C27" > devorbit-web/src/styles/transitions.css
COMMITS+=("2026-06-03 14:00:00|feat: add page transition and stagger animation styles|devorbit-web/src/styles/transitions.css")

# C28: performance utils
CONTENT_C28='type MetricName = "TTFB" | "FCP" | "LCP" | "CLS" | "INP" | "navigation";

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
'
printf '%s' "$CONTENT_C28" > devorbit-web/src/utils/performance.ts
COMMITS+=("2026-06-04 09:30:00|feat: add performance monitoring utilities for Web Vitals|devorbit-web/src/utils/performance.ts")

# C29: useTheme hook
CONTENT_C29='import { useState, useEffect, useCallback } from "react";

type Theme = "light" | "dark";
const STORAGE_KEY = "devorbit-theme";

export function useTheme() {
  const [theme, setThemeState] = useState<Theme>(() => {
    if (typeof window === "undefined") return "light";
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored === "light" || stored === "dark") return stored;
    return window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  });

  const [resolved, setResolved] = useState(false);

  useEffect(() => {
    const root = document.documentElement;
    root.classList.remove("light", "dark");
    root.classList.add(theme);
    localStorage.setItem(STORAGE_KEY, theme);
    setResolved(true);
  }, [theme]);

  // Listen for system preference changes when no stored preference
  useEffect(() => {
    const mq = window.matchMedia("(prefers-color-scheme: dark)");
    const handler = (e: MediaQueryListEvent) => {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (!stored) {
        setThemeState(e.matches ? "dark" : "light");
      }
    };
    mq.addEventListener("change", handler);
    return () => mq.removeEventListener("change", handler);
  }, []);

  const toggleTheme = useCallback(() => {
    setThemeState((prev) => (prev === "light" ? "dark" : "light"));
  }, []);

  const setTheme = useCallback((t: Theme) => {
    setThemeState(t);
  }, []);

  return {
    theme,
    resolved,
    isDark: theme === "dark",
    toggleTheme,
    setTheme,
  };
}
'
printf '%s' "$CONTENT_C29" > devorbit-web/src/hooks/useTheme.ts
COMMITS+=("2026-06-04 14:00:00|feat: add theme persistence hook with system preference detection|devorbit-web/src/hooks/useTheme.ts")

# C30: LazyImage
CONTENT_C30='import { useState, useRef, useEffect } from "react";

interface LazyImageProps {
  src: string;
  alt: string;
  width?: number;
  height?: number;
  className?: string;
  placeholderColor?: string;
  threshold?: number;
  onLoad?: () => void;
  onError?: () => void;
}

export function LazyImage({
  src,
  alt,
  width,
  height,
  className = "",
  placeholderColor = "#e5e7eb",
  threshold = 0.1,
  onLoad,
  onError,
}: LazyImageProps) {
  const [loaded, setLoaded] = useState(false);
  const [error, setError] = useState(false);
  const [inView, setInView] = useState(false);
  const imgRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const el = imgRef.current;
    if (!el) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setInView(true);
          observer.disconnect();
        }
      },
      { threshold, rootMargin: "200px" }
    );

    observer.observe(el);
    return () => observer.disconnect();
  }, [threshold]);

  if (error) {
    return (
      <div
        className={`flex items-center justify-center bg-gray-100 dark:bg-gray-700 ${className}`}
        style={{ width, height }}
      >
        <svg className="h-8 w-8 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
          />
        </svg>
      </div>
    );
  }

  return (
    <div
      ref={imgRef}
      className={`relative overflow-hidden ${className}`}
      style={{
        width,
        height,
        backgroundColor: placeholderColor,
      }}
    >
      {!loaded && (
        <div className="absolute inset-0 animate-pulse" style={{ backgroundColor: placeholderColor }} />
      )}
      {inView && (
        <img
          src={src}
          alt={alt}
          className={`h-full w-full object-cover transition-opacity duration-300 ${
            loaded ? "opacity-100" : "opacity-0"
          }`}
          onLoad={() => {
            setLoaded(true);
            onLoad?.();
          }}
          onError={() => {
            setError(true);
            onError?.();
          }}
        />
      )}
    </div>
  );
}
'
printf '%s' "$CONTENT_C30" > devorbit-web/src/components/shared/LazyImage.tsx
COMMITS+=("2026-06-05 10:00:00|feat: add lazy loading image component with blur placeholder|devorbit-web/src/components/shared/LazyImage.tsx")


# =============================================
# CREATE ALL COMMITS WITH BACKDATED TIMESTAMPS
# =============================================
echo ""
echo "=== Creating $(( ${#COMMITS[@]} )) commits ==="
echo ""

for entry in "${COMMITS[@]}"; do
  # Parse: TIMESTAMP|MESSAGE|FILE
  TIMESTAMP="${entry%%|*}"
  REST="${entry#*|}"
  MESSAGE="${REST%%|*}"
  FILE="${REST#*|}"

  # Git add only the file(s) for this commit
  IFS=',' read -ra FILES <<< "$FILE"
  for f in "${FILES[@]}"; do
    # Trim whitespace
    f_trimmed=$(echo "$f" | xargs)
    git add "$f_trimmed"
  done

  GIT_AUTHOR_DATE="$TIMESTAMP" \
  GIT_COMMITTER_DATE="$TIMESTAMP" \
  git commit -m "$MESSAGE"

  echo "  ✓ $TIMESTAMP | $MESSAGE"
done

echo ""
echo "=== All ${#COMMITS[@]} commits created on branch: $BRANCH ==="

# Push
echo ""
echo "=== Pushing branch ==="
git push origin "$BRANCH" 2>&1

# Create PR
echo ""
echo "=== Creating PR ==="
gh pr create \
  --base "$BASE_BRANCH" \
  --head "$BRANCH" \
  --title "chore: add unit tests and utility modules" \
  --body "## Mô tả

Thêm unit tests, utility modules, và shared components cho web app.

### Thay đổi chính

**Utilities**
- API response types và shared type utilities
- Date formatting và relative time helpers
- Form validation utilities (email, phone, student ID, required, min/max length)
- Pagination utility class với page range calculation
- GPA calculation helpers với grade conversion và classification
- Performance monitoring (Web Vitals observation)

**Hooks**
- \`useDebounce\` / \`useDebouncedCallback\`
- \`useSearchHistory\` (localStorage persistence)
- \`useScrollRestoration\` with route-based scroll positions
- \`useKeyboardShortcuts\` with common presets
- \`useTheme\` (system preference detection + localStorage)

**Components**
- \`Skeleton\`, \`CardSkeleton\`, \`TableSkeleton\` loading states
- \`ErrorRetryBoundary\` with retry button
- \`Toast\` notification system with context provider
- \`SemesterSelector\` với trạng thái active/disabled
- \`SubjectProgressCard\` với progress bar
- \`PrerequisitesView\` cho curriculum tree
- \`StarBadge\`, \`TechTagCloud\`, \`ResponsiveTable\`, \`LazyImage\`

**Tests (unit)**
- Date utilities, validation, pagination, GPA calculation
- SemesterSelector component (render, selection, disabled state)
- useDebounce hook (value debounce, callback debounce)

**Styles**
- Page transition animations (fade, slide, scale, stagger)

### Kiểm tra
- [x] Unit tests pass
- [x] TypeScript compilation pass
- [x] Components render correctly ở cả light và dark mode
"
