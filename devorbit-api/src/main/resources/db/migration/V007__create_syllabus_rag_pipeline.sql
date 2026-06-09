-- ================================================================
-- V007: Syllabus RAG pipeline schema
-- ================================================================

CREATE TABLE IF NOT EXISTS knowledge_sources (
    id UUID PRIMARY KEY,
    source_type VARCHAR(100) NOT NULL,
    title TEXT,
    file_name TEXT,
    file_path TEXT,
    url TEXT,
    content_hash TEXT NOT NULL,
    trust_level VARCHAR(50) NOT NULL DEFAULT 'OFFICIAL',
    status VARCHAR(50) NOT NULL,
    raw_text TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS course_syllabus (
    id UUID PRIMARY KEY,
    source_id UUID NOT NULL REFERENCES knowledge_sources(id) ON DELETE CASCADE,
    course_code VARCHAR(50) NOT NULL,
    course_name_vi TEXT,
    course_name_en TEXT,
    credits INT,
    theory_hours INT,
    practice_hours INT,
    self_study_hours INT,
    prerequisite TEXT,
    previous_course TEXT,
    department TEXT,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS course_objectives (
    id UUID PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    outcome_refs JSONB
);

CREATE TABLE IF NOT EXISTS course_outcomes (
    id UUID PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    outcome_code VARCHAR(50) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS course_sessions (
    id UUID PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    source_id UUID NOT NULL REFERENCES knowledge_sources(id) ON DELETE CASCADE,
    session_no VARCHAR(50),
    session_type VARCHAR(50) NOT NULL,
    topic TEXT NOT NULL,
    activities TEXT,
    assessment_component TEXT
);

CREATE TABLE IF NOT EXISTS course_assessments (
    id UUID PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    component_code VARCHAR(50) NOT NULL,
    description TEXT,
    weight_percent INT
);

CREATE TABLE IF NOT EXISTS course_references (
    id UUID PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    reference_text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS course_tools (
    id UUID PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    tool_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS knowledge_chunks (
    id UUID PRIMARY KEY,
    source_id UUID NOT NULL REFERENCES knowledge_sources(id) ON DELETE CASCADE,
    course_code VARCHAR(50),
    chunk_index INT NOT NULL,
    section_title TEXT,
    chunk_text TEXT NOT NULL,
    metadata_json JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Indices for faster lookup
CREATE INDEX IF NOT EXISTS idx_knowledge_sources_hash ON knowledge_sources(content_hash);
CREATE INDEX IF NOT EXISTS idx_course_syllabus_code ON course_syllabus(course_code);
CREATE INDEX IF NOT EXISTS idx_course_objectives_code ON course_objectives(course_code);
CREATE INDEX IF NOT EXISTS idx_course_outcomes_code ON course_outcomes(course_code);
CREATE INDEX IF NOT EXISTS idx_course_sessions_code ON course_sessions(course_code);
CREATE INDEX IF NOT EXISTS idx_course_assessments_code ON course_assessments(course_code);
CREATE INDEX IF NOT EXISTS idx_course_references_code ON course_references(course_code);
CREATE INDEX IF NOT EXISTS idx_course_tools_code ON course_tools(course_code);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_code ON knowledge_chunks(course_code);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_source ON knowledge_chunks(source_id);
