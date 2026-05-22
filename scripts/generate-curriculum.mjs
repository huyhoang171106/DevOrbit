import fs from 'fs';
import path from 'path';

const content = fs.readFileSync(
  path.resolve('daa.uit.edu.vn_danh-muc-mon-hoc-dai-hoc.2026-05-11T14_18_08.991Z.md'),
  'utf-8'
);
const lines = content.split('\n');

// Parse all rows from the second occurrence of header
let started = false;
let count = 0;
const seRows = [];

for (const line of lines) {
  if (!started) {
    if (line.startsWith('| S') && line.includes('Mã MH')) { count++; if (count===2) started=true; }
    continue;
  }
  if (line.trim() === '') break;
  if (!line.startsWith('|') || !line.match(/^\|\s*\d+/)) continue;
  
  const parts = line.split('|').map(p => p.trim());
  const cols = parts.slice(1, -1);
  if (cols.length < 10) continue;
  
  const code = cols[1];
  if (!code.startsWith('SE')) continue;
  
  const nameVI = cols[2] || '';
  const equiv = cols[8] || '';
  const prereq = cols[9] || '';  // Mã môn học tiên quyết
  const before = cols[10] || ''; // Mã môn học trước
  const theory = parseInt(cols[11]) || 0;
  const practice = parseInt(cols[12]) || 0;
  
  seRows.push({ code, nameVI, equiv, prereq, before, theory, practice });
}

// Generate the TypeScript content
let output = `// Auto-generated from DAA course catalog (daa.uit.edu.vn)
// Generated on ${new Date().toISOString().split('T')[0]}
// WARNING: Do not edit manually — run scripts/generate-curriculum.mjs to regenerate

/** Real course names from DAA catalog */
export const COURSE_NAMES: Record<string, string> = {\n`;

seRows.forEach(r => {
  const cleanName = r.nameVI.replace(/&nbsp;/g, ' ').replace(/&#39;/g, "'");
  output += `  '${r.code}': '${cleanName}',\n`;
});
output += `};\n\n`;

// Credits
output += `/** Real credits (theory + practice) from DAA catalog */\n`;
output += `export const CREDITS: Record<string, number> = {\n`;
seRows.forEach(r => {
  const totalCredits = r.theory + r.practice;
  if (totalCredits > 0) {
    output += `  '${r.code}': ${totalCredits},\n`;
  }
});
output += `};\n\n`;

// Before (mã môn học trước) — recommended background knowledge
output += `/** Môn học trước (recommended background knowledge) from DAA catalog */\n`;
output += `const BEFORE: Record<string, string[]> = {\n`;
seRows.forEach(r => {
  if (r.before) {
    const codes = r.before.split('<br>').filter(c => c.trim());
    if (codes.length > 0) {
      output += `  '${r.code}': [${codes.map(c => "'" + c.trim() + "'").join(', ')}],\n`;
    }
  }
});
output += `};\n\n`;

// Tiên quyết (strict prerequisites) — courses that must be taken first
output += `/** Môn tiên quyết (strict prerequisites) from DAA catalog */\n`;
output += `const PREREQS: Record<string, string[]> = {\n`;
seRows.forEach(r => {
  if (r.prereq) {
    const codes = r.prereq.split('<br>').filter(c => c.trim());
    if (codes.length > 0) {
      output += `  '${r.code}': [${codes.map(c => "'" + c.trim() + "'").join(', ')}],\n`;
    }
  }
});
output += `};\n\n`;

// === Fixed semester assignments (from KTPM curriculum JSON) ===
// These are per-program, not from DAA catalog
output += `/** Fixed semester assignments (KTPM curriculum) */
const FIXED_SEMESTER: Record<string, number> = {
  'IT001': 1,
  'IT012': 1,
  'MA003': 1,
  'MA006': 1,
  'SE005': 1,
  'ME001': 1,
  'IT002': 2,
  'IT003': 2,
  'MA004': 2,
  'MA005': 2,
  'IT008': 3,
  'IT004': 3,
  'IT005': 3,
  'IT007': 3,
  'SE104': 4,
  'TCN1': 4,
  'SE100': 5,
  'TCN2': 5,
  'PE0231': 5,
  'SE503': 6,
  'TCN3': 6,
  'PE0232': 6,
  'SE502': 7,
  'TCN4': 7,
  'SE505': 8,
  'SE506': 8,
  'CDTN': 8,
  'SE507': 8,
};

export function getFixedSemester(code: string): number | undefined {
  return FIXED_SEMESTER[code];
}

export function getFixedSemesterCodes(): string[] {
  return Object.keys(FIXED_SEMESTER);
}

/** Get real credits for a course code */\n`;
output += `export function getCurriculumCredits(code: string): number | undefined {\n`;
output += `  return CREDITS[code];\n`;
output += `}\n\n`;

output += `/** Get real prerequisite (môn tiên quyết) codes */\n`;
output += `export function getCurriculumPrereqs(code: string): string[] {\n`;
output += `  return PREREQS[code] ?? [];\n`;
output += `}\n\n`;

output += `/** Get recommended background (môn trước) codes */\n`;
output += `export function getCurriculumBefore(code: string): string[] {\n`;
output += `  return BEFORE[code] ?? [];\n`;
output += `}\n\n`;

output += `/** Get course display name */\n`;
output += `export function getCourseName(code: string): string {\n`;
output += `  return COURSE_NAMES[code] ?? code;\n`;
output += `}\n\n`;

output += `/** Get all SE-program course codes */\n`;
output += `export function getAllCourseCodes(): string[] {\n`;
output += `  return Object.keys(COURSE_NAMES);\n`;
output += `}\n`;

// Write
const target = 'devorbit-web/src/components/student/curriculumData.ts';
fs.writeFileSync(path.resolve(target), output, 'utf-8');
console.log(`Written to ${target}`);
console.log(`SE courses: ${seRows.length}`);

// Validate key courses
const keys = ['SE301', 'SE401', 'SE201', 'CS101', 'SE100', 'SE104'];
keys.forEach(k => {
  const found = seRows.find(r => r.code === k);
  console.log(`  ${k}: ${found ? found.nameVI : 'NOT FOUND (correctly excluded)'}`);
});
