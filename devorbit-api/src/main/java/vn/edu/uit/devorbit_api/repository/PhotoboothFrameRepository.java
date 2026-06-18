package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.devorbit_api.entity.PhotoboothFrame;
import java.util.Optional;

/**
 * PHOTOBOOTH FRAME REPOSITORY = data access for AI photobooth frame templates.
 *
 * Frames define how photobooth photos look (slot positions, overlays, filters).
 * frameId is a human-readable identifier (e.g., "graduation-2024").
 */
public interface PhotoboothFrameRepository extends JpaRepository<PhotoboothFrame, Long> {

    /** Find a frame by its human-readable identifier. */
    Optional<PhotoboothFrame> findByFrameId(String frameId);

    /** Delete a frame by its identifier. */
    void deleteByFrameId(String frameId);
}
