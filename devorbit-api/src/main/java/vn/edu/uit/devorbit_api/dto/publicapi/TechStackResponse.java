package vn.edu.uit.devorbit_api.dto.publicapi;

/**
 * TECH STACK RESPONSE = a single technology name.
 *
 * Simple wrapper used in lists like:
 * - GET /api/tech-stacks → returns all tech stacks
 * - Nested in RepoSummaryResponse.techStacks
 *
 * Example: TechStackResponse(name="React")
 */
public record TechStackResponse(Long id, String name) {}
