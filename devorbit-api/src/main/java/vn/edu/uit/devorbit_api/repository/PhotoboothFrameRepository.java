package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.devorbit_api.entity.PhotoboothFrame;
import java.util.Optional;

public interface PhotoboothFrameRepository extends JpaRepository<PhotoboothFrame, Long> {
    Optional<PhotoboothFrame> findByFrameId(String frameId);
    void deleteByFrameId(String frameId);
}
