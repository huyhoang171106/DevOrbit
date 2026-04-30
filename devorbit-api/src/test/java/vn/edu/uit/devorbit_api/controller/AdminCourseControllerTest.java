package vn.edu.uit.devorbit_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.admin.AdminCourseUpsertRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.service.CourseService;
import vn.edu.uit.devorbit_api.service.JwtService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCourseController.class)
class AdminCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldCreateCourse() throws Exception {
        AdminCourseUpsertRequest request = new AdminCourseUpsertRequest(
            "SE104", "Nhap mon Test", 3, null, null, null, null
        );
        CourseDetailResponse response = new CourseDetailResponse(
            1L, "SE104", "Nhap mon Test", null, null, null, 3, null, List.of()
        );
        when(courseService.createCourse(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code":"SE104","name":"Nhap mon Test","credits":3,"subjectType":"Chuyen nganh"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.code").value("SE104"))
            .andExpect(jsonPath("$.name").value("Nhap mon Test"));
    }

    @Test
    void shouldUpdateCourse() throws Exception {
        AdminCourseUpsertRequest request = new AdminCourseUpsertRequest(
            "SE104", "Nhap mon Test Updated", 3, null, null, null, null
        );
        CourseDetailResponse response = new CourseDetailResponse(
            1L, "SE104", "Nhap mon Test Updated", null, null, null, 3, null, List.of()
        );
        when(courseService.updateCourse(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/admin/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code":"SE104","name":"Nhap mon Test Updated","credits":3,"subjectType":"Chuyen nganh"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Nhap mon Test Updated"));
    }

    @Test
    void shouldDeactivateCourse() throws Exception {
        mockMvc.perform(delete("/api/admin/courses/1"))
            .andExpect(status().isNoContent());
    }
}
