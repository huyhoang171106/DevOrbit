package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.StudentUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentUserRepository extends JpaRepository<StudentUser, Long> {
    Optional<StudentUser> findByStudentCode(String studentCode);
    Optional<StudentUser> findByEmail(String email);
    List<StudentUser> findTop10ByOrderByIdDesc();
    List<StudentUser> findAllByOrderByIdDesc();
    List<StudentUser> findByStudentCodeContainingOrFullNameContainingOrEmailContaining(
        String studentCode, String fullName, String email);
}