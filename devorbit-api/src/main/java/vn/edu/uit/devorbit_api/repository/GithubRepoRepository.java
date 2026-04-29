package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.GithubRepo;

import java.util.Optional;
import java.util.List;
@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {
    List<GithubRepo> findBySubjectId(String subjectId);
}
