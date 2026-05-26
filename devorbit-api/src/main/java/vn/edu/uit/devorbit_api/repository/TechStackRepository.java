package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.TechStack;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    Optional<TechStack> findByNameIgnoreCase(String name);

    @Query("SELECT t FROM TechStack t ORDER BY t.name")
    List<TechStack> findAllDistinctOrderByName();

    @Query("SELECT t FROM GithubRepo r JOIN r.techStacks t WHERE r.id = :repoId ORDER BY t.name")
    List<TechStack> findByRepoIdFromJoinTable(@Param("repoId") Long repoId);

    @Query("SELECT t FROM TechStack t WHERE t.repo.id = :repoId ORDER BY t.name")
    List<TechStack> findByRepoId(Long repoId);

    @Query("SELECT t.name FROM GithubRepo r JOIN r.techStacks t GROUP BY t.name ORDER BY COUNT(r) DESC")
    List<String> findTop10TechStacksByUsage();

    void deleteByRepoId(Long repoId);
}
