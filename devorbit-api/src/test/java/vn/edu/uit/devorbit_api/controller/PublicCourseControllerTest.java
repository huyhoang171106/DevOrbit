package vn.edu.uit.devorbit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.service.CourseArticleService;
import vn.edu.uit.devorbit_api.service.CourseService;
import vn.edu.uit.devorbit_api.service.CourseTutorialService;
import vn.edu.uit.devorbit_api.service.CourseYoutubePlaylistService;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.KnowledgeGraphService;

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

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CourseTutorialService tutorialService;

    @MockitoBean
    private CourseYoutubePlaylistService playlistService;

    @MockitoBean
    private CourseArticleService articleService;

    @MockitoBean
    private KnowledgeGraphService knowledgeGraphService;

    @Test
    void shouldReturnCourseSummaries() throws Exception {
        when(courseService.getActiveCourseSummaries()).thenReturn(List.of(
            new CourseSummaryResponse(1L, "SE101", "Nhap mon CNPM", 0L)
        ));

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("SE101"));
    }

    @Test
    void shouldReturnKnowledgeGraph() throws Exception {
        var graphNodes = List.of(
            new KnowledgeGraphResponse.GraphNode(1L, "Test Course", "T101", 12.0, 0),
            new KnowledgeGraphResponse.GraphNode(2L, "Advanced Test", "T201", 13.5, 1)
        );
        var graphLinks = List.of(
            new KnowledgeGraphResponse.GraphLink(1L, 2L, "PREREQUISITE")
        );
        when(knowledgeGraphService.getGraph()).thenReturn(
            new KnowledgeGraphResponse(graphNodes, graphLinks)
        );

        mockMvc.perform(get("/api/courses/graph"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes[0].code").value("T101"))
            .andExpect(jsonPath("$.nodes[0].level").value(0))
            .andExpect(jsonPath("$.nodes[1].code").value("T201"))
            .andExpect(jsonPath("$.nodes[1].level").value(1))
            .andExpect(jsonPath("$.links[0].type").value("PREREQUISITE"));
    }
}
