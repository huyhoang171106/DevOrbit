import { describe, expect, test } from 'vitest'
import { evaluateRepository } from './repoEvaluation'
import { it008Notepad, lab04It007, lab5It008 } from './repoEvaluation.fixtures/sampleRepos'

describe('repo evidence rubric', () => {
  test('Lab04-IT007 is a programming exercise and is not penalized for project-only runtime criteria', () => {
    const result = evaluateRepository(lab04It007)

    expect(result.repoType).toBe('programming_exercise')
    expect(result.recommendationTag).toBe('needs_check')
    expect(result.criteria.find((item) => item.key === 'production_runtime_config')?.applicability).toBe('not_applicable')
    expect(result.criteria.find((item) => item.key === 'relevant_implementation')?.status).toBe('strong')
    expect(result.criteria.find((item) => item.key === 'validation_evidence')?.status).toMatch(/missing|weak/)
    expect(result.warnings.map((warning) => warning.code)).toContain('missing_validation_evidence')
  })

  test('source files do not imply tests, passing builds, or ready-to-use status', () => {
    const result = evaluateRepository(lab04It007)

    expect(result.signals.hasSourceCode).toBe(true)
    expect(result.signals.hasTests).toBe(false)
    expect(result.recommendationTag).not.toBe('ready_to_use')
    expect(result.runReadinessScore).toBeLessThan(80)
  })

  test('Notepad has project manifests but does not automatically become a good project sample', () => {
    const result = evaluateRepository(it008Notepad)

    expect(result.repoType).toBe('project_practice')
    expect(result.signals.hasPackageFile).toBe(true)
    expect(result.recommendationTag).not.toBe('good_project_sample')
    expect(result.recommendationTag).not.toBe('ready_to_use')
    expect(result.warnings.map((warning) => warning.code)).toEqual(expect.arrayContaining([
      'missing_setup_guidance',
      'missing_validation_evidence',
    ]))
  })

  test('.NET Framework 4.7.2 is compatibility evidence and not a failure by itself', () => {
    const result = evaluateRepository(it008Notepad)

    expect(result.signals.techStacks).toContain('.NET Framework 4.7.2')
    expect(result.warnings.some((warning) => warning.severity === 'critical')).toBe(false)
  })

  test('Lab5 gains learning coverage for multiple subprojects but gets repository hygiene warnings', () => {
    const result = evaluateRepository(lab5It008)

    expect(result.repoType).toBe('programming_exercise')
    expect(result.learningUsefulnessScore).toBeGreaterThan(45)
    expect(result.recommendationTag).toMatch(/needs_check|reference_only/)
    expect(result.warnings.find((warning) => warning.code === 'committed_build_artifacts')?.paths).toEqual(
      expect.arrayContaining(['Bai04/.vs/Bai04/v17/.suo', 'Bai04/bin/Debug/Bai04.exe', 'Bai04/obj/Debug/Bai04.csproj.FileListAbsolute.txt']),
    )
  })

  test('not_applicable criteria do not lower normalized denominator', () => {
    const result = evaluateRepository(lab04It007)
    const applicable = result.criteria.filter((item) => item.applicability === 'applicable')
    const manualScore = Math.round(
      (applicable.reduce((total, item) => total + item.score, 0) /
        applicable.reduce((total, item) => total + item.maxScore, 0)) * 100,
    )

    expect(result.criteria.some((item) => item.applicability === 'not_applicable')).toBe(true)
    expect(result.learningUsefulnessScore).toBe(manualScore)
  })

  test('technicalReadinessScore is null when technical evidence is too thin', () => {
    const result = evaluateRepository({
      ...lab04It007,
      displayName: 'unknown-notes',
      description: '',
      primaryLanguage: '',
      techStacks: [],
      courseId: null,
      courseCode: null,
      courseName: null,
      readmeExcerpt: null,
      files: [],
      topics: [],
    } as typeof lab04It007)

    expect(result.recommendationTag).toBe('insufficient_data')
    expect(result.technicalReadinessScore).toBeNull()
  })

  test('README one-liners do not count as full documentation', () => {
    const result = evaluateRepository(lab5It008)

    expect(result.signals.hasReadme).toBe(true)
    expect(result.warnings.map((warning) => warning.code)).toContain('minimal_readme')
    expect(result.criteria.find((item) => item.key === 'assignment_description')?.status).not.toBe('strong')
  })

  test('results are deterministic regardless of file order', () => {
    const original = evaluateRepository(lab5It008)
    const shuffled = evaluateRepository({
      ...lab5It008,
      files: [...(lab5It008.files ?? [])].reverse(),
    } as typeof lab5It008)

    expect(shuffled.repoType).toBe(original.repoType)
    expect(shuffled.learningUsefulnessScore).toBe(original.learningUsefulnessScore)
    expect(shuffled.technicalReadinessScore).toBe(original.technicalReadinessScore)
    expect(shuffled.recommendationTag).toBe(original.recommendationTag)
    expect(shuffled.warnings.map((warning) => warning.code).sort()).toEqual(original.warnings.map((warning) => warning.code).sort())
  })
})
