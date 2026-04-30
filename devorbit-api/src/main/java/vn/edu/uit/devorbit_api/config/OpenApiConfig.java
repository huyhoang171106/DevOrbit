package vn.edu.uit.devorbit_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevOrbit API")
                        .version("1.0.0")
                        .description("REST API for DevOrbit - GitHub repository management and candidate tracking system")
                        .contact(new Contact()
                                .name("DevOrbit Team")
                                .email("devorbit@uit.edu.vn")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("/").description("Production / Docker")
                ));
    }
}