package vn.edu.uit.devorbit_api.service;


import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.entity.Course;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    public Course getCourseByMaMH(String maMH) {
        return courseRepository.findByMaMH(maMH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học có mã: " + maMH));
    }
}
