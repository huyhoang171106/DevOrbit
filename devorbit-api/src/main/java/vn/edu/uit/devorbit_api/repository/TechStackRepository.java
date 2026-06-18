package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.TechStack;

import java.util.List;
import java.util.Optional;

/**
 * TECH STACK REPOSITORY = data access for technology tags.
 *
 * ⚠️ TWO WAYS TO FIND TECH STACKS BY REPO:
 *    findByRepoIdFromJoinTable() — queries the ManyToMany join table (NEW way)
 *    findByRepoId()             — queries the legacy repo_id column (OLD way)
 *
 * Prefer findByRepoIdFromJoinTable() for new code.
 */
@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    /** Find by name, case-insensitive. Used to avoid creating duplicate stacks. */
    Optional<TechStack> findByNameIgnoreCase(String name);

    /** All tech stacks sorted alphabetically. */
    @Query("SELECT t FROM TechStack t ORDER BY t.name")
    List<TechStack> findAllDistinctOrderByName();

    /**
     * [NEW] Find tech stacks for a repo via the ManyToMany join table.
     * JOINs GithubRepo → repo_tech_stacks → TechStack.
     */
    @Query("SELECT t FROM GithubRepo r JOIN r.techStacks t WHERE r.id = :repoId ORDER BY t.name")
    List<TechStack> findByRepoIdFromJoinTable(@Param("repoId") Long repoId);

    /**
     * [LEGACY] Find tech stacks via the old repo_id foreign key.
     * Only works for old data. Do not use for new repos.
     */
    @Query("SELECT t FROM TechStack t WHERE t.repo.id = :repoId ORDER BY t.name")
    List<TechStack> findByRepoId(Long repoId);

    /**
     * Find the 10 most popular tech stack names (by repo count).
     * Used for the "popular technologies" display on the frontend.
     * JOINs GithubRepo → repo_tech_stacks → TechStack and counts repos per stack.
     */
    @Query("SELECT t.name FROM GithubRepo r JOIN r.techStacks t GROUP BY t.name ORDER BY COUNT(r) DESC")
    List<String> findTop10TechStacksByUsage();

    /** [LEGACY] Delete tech stacks by old repo_id. */
    void deleteByRepoId(Long repoId);
}
