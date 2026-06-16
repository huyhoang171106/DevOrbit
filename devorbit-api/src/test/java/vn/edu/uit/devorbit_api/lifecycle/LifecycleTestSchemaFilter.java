package vn.edu.uit.devorbit_api.lifecycle;

import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;

/**
 * SchemaFilter that excludes knowledge_chunks table from DDL generation.
 * This prevents H2 from failing on the vector(4096) column type.
 */
public class LifecycleTestSchemaFilter implements SchemaFilterProvider {

    private final SchemaFilter excludeKnowledgeChunks = new SchemaFilter() {
        @Override
        public boolean includeNamespace(Namespace namespace) {
            return true;
        }

        @Override
        public boolean includeTable(Table table) {
            return !"knowledge_chunks".equals(table.getName());
        }

        @Override
        public boolean includeSequence(Sequence sequence) {
            return true;
        }
    };

    @Override
    public SchemaFilter getCreateFilter() {
        return excludeKnowledgeChunks;
    }

    @Override
    public SchemaFilter getDropFilter() {
        return excludeKnowledgeChunks;
    }

    @Override
    public SchemaFilter getTruncatorFilter() {
        return excludeKnowledgeChunks;
    }

    @Override
    public SchemaFilter getMigrateFilter() {
        return excludeKnowledgeChunks;
    }

    @Override
    public SchemaFilter getValidateFilter() {
        return excludeKnowledgeChunks;
    }
}
