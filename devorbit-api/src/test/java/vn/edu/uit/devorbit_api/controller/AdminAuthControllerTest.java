package vn.edu.uit.devorbit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.admin.LoginResponse;
import vn.edu.uit.devorbit_api.service.AdminAuthService;
import vn.edu.uit.devorbit_api.service.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuthController.class)
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAuthService adminAuthService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnJwtForValidAdmin() throws Exception {
        when(adminAuthService.login(any())).thenReturn(new LoginResponse("token-value"));

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"admin","password":"admin123"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("token-value"));
    }
}
