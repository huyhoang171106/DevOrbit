package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final GithubRepoService githubRepoService;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseByMaMH(String maMH) {
        return courseRepository.findByMaMH(maMH)
                .orElseThrow(() -> new NotFoundException("Course not found with code: " + maMH));
    }

    public List<CourseSummaryResponse> getActiveCourseSummaries() {
        return courseRepository.findByActiveTrue().stream()
                .map(course -> new CourseSummaryResponse(
                        course.getId(),
                        course.getMaMH(),
                        course.getTenMH()))
                .toList();
    }

    public CourseDetailResponse getCourseDetail(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
        return new CourseDetailResponse(
                course.getId(),
                course.getMaMH(),
                course.getTenMH(),
                course.getDescription(),
                course.getLt(),
                course.getTh(),
                githubRepoService.getApprovedReposByCourse(course.getId())
        );
    }
}
