package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.Otp;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);

    void deleteByEmail(String email);
}