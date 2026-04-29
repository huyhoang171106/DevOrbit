package vn.edu.uit.devorbit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.service.GithubRepoService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicRepoController.class)
class PublicRepoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubRepoService githubRepoService;

    @Test
    void shouldReturnReposByCourse() throws Exception {
        when(githubRepoService.getApprovedReposByCourse(1L)).thenReturn(List.of(
            new RepoSummaryResponse(1L, "My Repo", "A test repo", "https://github.com/test/repo", "Java", 10, List.of())
        ));

        mockMvc.perform(get("/api/courses/1/repos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].displayName").value("My Repo"));
    }
}
