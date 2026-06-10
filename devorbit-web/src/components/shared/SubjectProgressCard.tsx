import { type SubjectProgress } from "../../types/progress";

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
