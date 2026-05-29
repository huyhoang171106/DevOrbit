import { type ReactNode } from "react";

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
