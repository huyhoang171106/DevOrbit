export function cleanText(value: unknown): string | null {
  if (typeof value !== 'string') return null
  const normalized = value.replace(/\s+/g, ' ').trim()
  return normalized ? normalized : null
}

export function normalizeStringList(value: string[] | string | null | undefined): string[] {
  if (!value) return []
  const rawValues = Array.isArray(value) ? value : value.split(/[,;|]/)
  return Array.from(new Set(rawValues.map(cleanText).filter(Boolean) as string[]))
}
