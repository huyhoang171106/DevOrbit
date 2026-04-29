package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.TechStack;

import java.util.List;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByRepoId(Long repoId);
}
