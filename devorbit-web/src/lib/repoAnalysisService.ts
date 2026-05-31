import type { RepoSummary } from '../types/api'
import { buildRepoAiAnalysisSections, type RepoAiAnalysisSection } from './repoAiAnalysis'
import { evaluateRepository, type RepoEvaluationResult } from './repoEvaluation'

export type RepoAnalysisSource = 'rule-based' | 'ai-provider'

export type RepoAnalysisResult = {
  repoId: number
  source: RepoAnalysisSource
  evaluation: RepoEvaluationResult
  sections: RepoAiAnalysisSection[]
  generatedAt: string
  fallbackUsed: boolean
  errorMessage?: string
}

export type RepositoryAnalysisProvider = {
  analyzeRepository(repo: RepoSummary): Promise<RepoAnalysisResult>
}

type AnalyzeRepositoryOptions = {
  provider?: RepositoryAnalysisProvider
}

export async function analyzeRepository(
  repo: RepoSummary,
  options: AnalyzeRepositoryOptions = {},
): Promise<RepoAnalysisResult> {
  if (!options.provider) return buildRuleBasedRepositoryAnalysis(repo)

  try {
    const result = await options.provider.analyzeRepository(repo)
    return result.sections.length > 0
      ? { ...result, evaluation: result.evaluation ?? evaluateRepository(repo) }
      : buildRuleBasedRepositoryAnalysis(repo, 'AI analysis returned no sections.')
  } catch (error) {
    const message = error instanceof Error ? error.message : 'AI analysis failed.'
    return buildRuleBasedRepositoryAnalysis(repo, message)
  }
}

export function buildRuleBasedRepositoryAnalysis(
  repo: RepoSummary,
  errorMessage?: string,
): RepoAnalysisResult {
  return {
    repoId: repo.id,
    source: 'rule-based',
    evaluation: evaluateRepository(repo),
    sections: buildRepoAiAnalysisSections(repo),
    generatedAt: new Date().toISOString(),
    fallbackUsed: Boolean(errorMessage),
    errorMessage,
  }
}
