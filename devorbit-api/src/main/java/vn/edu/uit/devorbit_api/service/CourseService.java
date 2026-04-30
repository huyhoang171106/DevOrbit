package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.dto.admin.AdminCourseUpsertRequest;
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
        return mapToDetail(course);
    }

    public CourseDetailResponse createCourse(AdminCourseUpsertRequest request) {
        Course course = Course.builder()
                .maMH(request.code())
                .tenMH(request.name())
                .soTC(request.credits() != null ? request.credits() : 0)
                .lt(request.lectureHours() != null ? request.lectureHours() : 0)
                .th(request.practiceHours() != null ? request.practiceHours() : 0)
                .loaiMonHoc(request.subjectType())
                .description(request.description())
                .active(true)
                .build();
        return mapToDetail(courseRepository.save(course));
    }

    public CourseDetailResponse updateCourse(Long id, AdminCourseUpsertRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        course.setTenMH(request.name());
        course.setSoTC(request.credits() != null ? request.credits() : 0);
        course.setLt(request.lectureHours() != null ? request.lectureHours() : 0);
        course.setTh(request.practiceHours() != null ? request.practiceHours() : 0);
        course.setLoaiMonHoc(request.subjectType());
        course.setDescription(request.description());
        return mapToDetail(courseRepository.save(course));
    }

    public void deactivateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        course.setActive(false);
        courseRepository.save(course);
    }

    private CourseDetailResponse mapToDetail(Course course) {
        return new CourseDetailResponse(
                course.getId(),
                course.getMaMH(),
                course.getTenMH(),
                course.getDescription(),
                course.getLt(),
                course.getTh(),
                course.getSoTC(),
                course.getLoaiMonHoc(),
                githubRepoService.getApprovedReposByCourse(course.getId())
        );
    }
}
