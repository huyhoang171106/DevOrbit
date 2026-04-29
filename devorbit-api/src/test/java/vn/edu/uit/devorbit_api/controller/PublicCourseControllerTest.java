package vn.edu.uit.devorbit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.service.CourseService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicCourseController.class)
class PublicCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @Test
    void shouldReturnCourseSummaries() throws Exception {
        when(courseService.getActiveCourseSummaries()).thenReturn(List.of(
            new CourseSummaryResponse(1L, "SE101", "Nhap mon CNPM")
        ));

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("SE101"));
    }
}
