interface Column<T> {
  key: string
  header: string
  render: (item: T) => React.ReactNode
  className?: string
}

interface AdminTableProps<T> {
  columns: Column<T>[]
  data: T[]
  keyExtractor: (item: T) => string | number
  emptyMessage?: string
}

export function AdminTable<T>({ columns, data, keyExtractor, emptyMessage = 'Không có dữ liệu' }: AdminTableProps<T>) {
  if (data.length === 0) {
    return (
      <div className="orbit-card p-10 text-center">
        <p className="text-ink-secondary">{emptyMessage}</p>
      </div>
    )
  }

  return (
    <div className="orbit-card overflow-hidden p-0">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border">
            {columns.map((col) => (
              <th key={col.key} className={`px-5 py-4 text-center text-[10px] font-black uppercase tracking-[0.2em] text-orbit-text bg-orbit-surface/50 ${col.className || ''}`}>
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((item, i) => (
            <tr key={keyExtractor(item)} className={`border-b border-orbit-border/50 hover:bg-orbit-accent/5 transition-colors duration-150 ${i === data.length - 1 ? 'border-b-0' : ''}`}>
              {columns.map((col) => (
                <td key={col.key} className={`px-5 py-4 text-sm text-ink-primary ${col.className || ''}`}>
                  {col.render(item)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
