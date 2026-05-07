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

    public List<CourseSummaryResponse> getActiveCourseSummaries() {
        return courseRepository.findAll().stream()
                .map(course -> new CourseSummaryResponse(
                        course.getId(),
                        course.getMaMH(),
                        course.getTenMH()))
                .toList();
    }

    public List<CourseSummaryResponse> getAllCourseSummaries() {
        return courseRepository.findAll().stream()
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

    public CourseDetailResponse getAdminCourseDetail(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
        return mapToDetail(course);
    }

    public CourseDetailResponse createCourse(AdminCourseUpsertRequest request) {
        Course course = Course.builder()
                .maMH(request.code())
                .tenMH(request.name())
                .tenMH_EN(request.nameEn())
                .soTC(request.credits() != null ? request.credits() : 0)
                .lt(request.lectureHours() != null ? request.lectureHours() : 0)
                .th(request.practiceHours() != null ? request.practiceHours() : 0)
                .loaiMonHoc(request.subjectType())
                .isOpen(request.isOpen() != null ? request.isOpen() : true)
                .managementUnit(request.managementUnit())
                .maMH_Old(request.codeOld())
                .equivalentMH(request.equivalentMH())
                .prerequisiteMH(request.prerequisiteMH())
                .previousMH(request.previousMH())
                .build();
        return mapToDetail(courseRepository.save(course));
    }

    public CourseDetailResponse updateCourse(Long id, AdminCourseUpsertRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        course.setTenMH(request.name());
        course.setTenMH_EN(request.nameEn());
        course.setSoTC(request.credits() != null ? request.credits() : 0);
        course.setLt(request.lectureHours() != null ? request.lectureHours() : 0);
        course.setTh(request.practiceHours() != null ? request.practiceHours() : 0);
        course.setLoaiMonHoc(request.subjectType());
        course.setOpen(request.isOpen() != null ? request.isOpen() : true);
        course.setManagementUnit(request.managementUnit());
        course.setMaMH_Old(request.codeOld());
        course.setEquivalentMH(request.equivalentMH());
        course.setPrerequisiteMH(request.prerequisiteMH());
        course.setPreviousMH(request.previousMH());
        return mapToDetail(courseRepository.save(course));
    }

    public void deactivateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        courseRepository.delete(course);
    }

    private CourseDetailResponse mapToDetail(Course course) {
        return new CourseDetailResponse(
                course.getId(),
                course.getMaMH(),
                course.getTenMH(),
                course.getTenMH_EN(),
                null,
                course.getLt(),
                course.getTh(),
                course.getSoTC(),
                course.getLoaiMonHoc(),
                course.isOpen(),
                course.getManagementUnit(),
                course.getMaMH_Old(),
                course.getEquivalentMH(),
                course.getPrerequisiteMH(),
                course.getPreviousMH(),
                githubRepoService.getApprovedReposByCourse(course.getId())
        );
    }
}
