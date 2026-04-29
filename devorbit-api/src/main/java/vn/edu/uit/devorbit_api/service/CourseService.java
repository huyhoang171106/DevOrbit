package vn.edu.uit.devorbit_api.service;


import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final TechStackRepository techStackRepository;
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    public Course getCourseByMaMH(String maMH) {
        return courseRepository.findByMaMH(maMH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học có mã: " + maMH));
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học có id: " + id));
        return new CourseDetailResponse(
                course.getId(),
                course.getMaMH(),
                course.getTenMH(),
                course.getDescription(),
                course.getLt(),
                course.getTh(),
                getReposForCourse(course.getId())
        );
    }
    private List<RepoSummaryResponse> getReposForCourse(Long courseId) {
        return githubRepoRepository.findByCourseIdAndActiveTrue(courseId).stream()
                .map(repo -> new RepoSummaryResponse(
                        repo.getId(),
                        repo.getDisplayName(),
                        repo.getDescription(),
                        repo.getGithubUrl(),
                        repo.getPrimaryLanguage(),
                        repo.getStars(),
                        getTechStacksForRepo(repo.getId())
                ))
                .toList();
    }
    private List<TechStackResponse> getTechStacksForRepo(Long repoId) {
        return techStackRepository.findByRepoId(repoId).stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
    }
}
