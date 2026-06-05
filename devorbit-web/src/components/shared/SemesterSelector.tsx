import { useId } from "react";

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
  const isSelectDisabled = disabled;

  return (
    <div className="flex flex-wrap gap-2">
      {semesters.map((sem) => {
        const isActive = sem.id === selected;
        return (
          <button
            key={sem.id}
            id={`${id}-${sem.id}`}
            onClick={() => onChange(sem.id)}
            disabled={isSelectDisabled}
            aria-pressed={isActive}
            className={`rounded-lg border px-4 py-2 text-sm font-medium transition-all
              ${
                isActive
                  ? "border-blue-500 bg-blue-50 text-blue-700 shadow-sm dark:border-blue-400 dark:bg-blue-900/30 dark:text-blue-300"
                  : "border-gray-200 bg-white text-gray-600 hover:border-gray-300 hover:bg-gray-50 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 dark:hover:border-gray-500"
              }
              ${isSelectDisabled ? "cursor-not-allowed opacity-50" : "cursor-pointer"}
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
