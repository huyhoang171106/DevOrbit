package vn.edu.uit.devorbit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.admin.CandidateReviewRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RepoCandidateService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminRepoCandidateController.class)
class AdminRepoCandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepoCandidateService repoCandidateService;

    @MockitoBean
    private JwtService jwtService;

    private RepoCandidateResponse makeCandidateResponse(Long id, String status) {
        return new RepoCandidateResponse(
            id, "owner", "repo", "https://github.com/owner/repo", status,
            null, null, null, 0, 0, null, null, null, null, null, null, null
        );
    }

    @Test
    void shouldApproveCandidate() throws Exception {
        when(repoCandidateService.approveCandidate(eq(10L), any()))
            .thenReturn(makeCandidateResponse(10L, "APPROVED"));

        mockMvc.perform(post("/api/admin/repo-candidates/10/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"description":"Cleaned up","techStacks":["java","spring-boot"]}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldListPendingCandidates() throws Exception {
        when(repoCandidateService.getPendingCandidates(eq("all")))
            .thenReturn(List.of(makeCandidateResponse(10L, "NEW")));

        mockMvc.perform(get("/api/admin/repo-candidates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("NEW"));
    }

    @Test
    void shouldRejectCandidate() throws Exception {
        when(repoCandidateService.rejectCandidate(eq(10L)))
            .thenReturn(makeCandidateResponse(10L, "REJECTED"));

        mockMvc.perform(post("/api/admin/repo-candidates/10/reject"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REJECTED"));
    }
}
