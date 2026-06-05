// @vitest-environment jsdom
import "@testing-library/jest-dom/vitest";
import { afterEach, describe, it, expect, vi } from "vitest";
import { cleanup, render, screen, fireEvent } from "@testing-library/react";
import { SemesterSelector } from "../shared/SemesterSelector";

afterEach(cleanup);

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
      expect(btn).toBeDisabled();
    });
  });
});
