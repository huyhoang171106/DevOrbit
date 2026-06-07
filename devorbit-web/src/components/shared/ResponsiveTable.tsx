import { type ReactNode } from "react";

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
