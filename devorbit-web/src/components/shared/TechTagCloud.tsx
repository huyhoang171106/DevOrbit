interface TechTag {
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
