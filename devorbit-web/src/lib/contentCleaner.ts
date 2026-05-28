/**
 * Cleans AI-generated content for frontend display:
 * - Strips emoji icons
 * - Removes bold markers (**text** → text)
 * - Removes markdown heading markers (#, ##, ### → plain text)
 * - Collapses excessive whitespace
 */
export function cleanAiContent(text: string): string {
  if (!text) return text

  let result = text

  // 1. Strip emoji characters (common Unicode emoji ranges)
  result = result.replace(
    /[\u{1F000}-\u{1FFFF}\u{2600}-\u{27BF}\u{2300}-\u{23FF}\u{2700}-\u{27BF}\u{2B50}\u{2934}\u{2935}\u{25AA}\u{25AB}\u{25FB}\u{25FC}\u{25FD}\u{25FE}\u{25B6}\u{25C0}\u{FE00}-\u{FE0F}\u{200D}]/gu,
    ''
  )

  // 2. Remove markdown bold markers (**text**)
  result = result.replace(/\*\*(.*?)\*\*/g, '$1')

  // 3. Remove markdown heading markers (#, ##, etc.)
  result = result.replace(/^#{1,6}\s+/gm, '')

  // 4. Remove markdown code backticks (`text`)
  result = result.replace(/`([^`]+)`/g, '$1')

  // 5. Collapse multiple consecutive newlines into max 2
  result = result.replace(/\n{3,}/g, '\n\n')

  // 6. Trim leading/trailing whitespace per line
  result = result
    .split('\n')
    .map((line) => line.trim())
    .join('\n')

  // 7. Remove empty lines at start/end
  result = result.trim()

  return result
}
