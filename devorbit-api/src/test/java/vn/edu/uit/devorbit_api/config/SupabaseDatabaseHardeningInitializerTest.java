package vn.edu.uit.devorbit_api.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupabaseDatabaseHardeningInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void isPostgres_returnsTrueWhenServerVersionIsAvailable() {
        when(jdbcTemplate.queryForObject("SELECT current_setting('server_version')", String.class))
                .thenReturn("17.6");

        SupabaseDatabaseHardeningInitializer initializer = new SupabaseDatabaseHardeningInitializer(jdbcTemplate);

        assertThat(initializer.isPostgres()).isTrue();
    }

    @Test
    void isPostgres_returnsFalseWhenServerVersionCannotBeRead() {
        when(jdbcTemplate.queryForObject("SELECT current_setting('server_version')", String.class))
                .thenThrow(new IllegalStateException("not postgres"));

        SupabaseDatabaseHardeningInitializer initializer = new SupabaseDatabaseHardeningInitializer(jdbcTemplate);

        assertThat(initializer.isPostgres()).isFalse();
    }

    @Test
    void initialize_appliesExtensionAndPolicyHardeningOnPostgres() {
        when(jdbcTemplate.queryForObject("SELECT current_setting('server_version')", String.class))
                .thenReturn("17.6");
        when(jdbcTemplate.queryForObject(eq("SELECT to_regclass(?) IS NOT NULL"), eq(Boolean.class), any(String.class)))
                .thenReturn(false);

        SupabaseDatabaseHardeningInitializer initializer = new SupabaseDatabaseHardeningInitializer(jdbcTemplate);

        initializer.initialize();

        verify(jdbcTemplate).execute("CREATE SCHEMA IF NOT EXISTS extensions");
        verify(jdbcTemplate).execute("CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA extensions");
        verify(jdbcTemplate).execute(org.mockito.ArgumentMatchers.contains("ALTER EXTENSION vector SET SCHEMA extensions"));
        verify(jdbcTemplate).execute(org.mockito.ArgumentMatchers.contains("Deny direct API access"));
    }
}
