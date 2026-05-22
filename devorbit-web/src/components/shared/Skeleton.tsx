import { type HTMLAttributes } from "react";

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
