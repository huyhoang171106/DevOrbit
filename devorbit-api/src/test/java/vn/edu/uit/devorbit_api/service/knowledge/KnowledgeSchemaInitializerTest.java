package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeSchemaInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void ensureEmbeddingColumn_preservesExistingVector4096Column() throws Exception {
        mockEmbeddingColumnType("vector(4096)");
        KnowledgeSchemaInitializer initializer = new KnowledgeSchemaInitializer(jdbcTemplate);

        initializer.ensureEmbeddingColumn();

        verify(jdbcTemplate, never()).execute(contains("ALTER COLUMN embedding TYPE"));
        verify(jdbcTemplate, never()).execute(eq("DROP INDEX IF EXISTS idx_knowledge_chunks_embedding"));
    }

    @Test
    void ensureEmbeddingColumn_addsMissingEmbeddingColumn() throws Exception {
        mockMissingEmbeddingColumn();
        KnowledgeSchemaInitializer initializer = new KnowledgeSchemaInitializer(jdbcTemplate);

        initializer.ensureEmbeddingColumn();

        verify(jdbcTemplate).execute(contains("ADD COLUMN embedding extensions.vector(4096)"));
    }

    @Test
    void ensureEmbeddingColumn_resetsOnlyIncompatibleVectorColumn() throws Exception {
        mockEmbeddingColumnType("vector(1536)");
        KnowledgeSchemaInitializer initializer = new KnowledgeSchemaInitializer(jdbcTemplate);

        initializer.ensureEmbeddingColumn();

        verify(jdbcTemplate).execute(eq("DROP INDEX IF EXISTS idx_knowledge_chunks_embedding"));
        verify(jdbcTemplate).execute(contains("ALTER COLUMN embedding TYPE extensions.vector(4096)"));
    }

    private void mockEmbeddingColumnType(String type) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn(type);
        when(jdbcTemplate.query(any(String.class), any(ResultSetExtractor.class)))
                .thenAnswer(invocation -> invocation.<ResultSetExtractor<String>>getArgument(1).extractData(rs));
    }

    private void mockMissingEmbeddingColumn() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(jdbcTemplate.query(any(String.class), any(ResultSetExtractor.class)))
                .thenAnswer(invocation -> invocation.<ResultSetExtractor<String>>getArgument(1).extractData(rs));
    }
}
