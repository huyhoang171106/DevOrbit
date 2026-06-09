-- ================================================================
-- V008: Add source_id to child tables for source-scoped ownership
-- ================================================================
-- Problem: Re-ingesting one source deletes facts from ALL sources
-- for that courseCode. Fix: add source_id FK so we can delete per-source.

-- course_objectives
ALTER TABLE course_objectives
    ADD COLUMN source_id UUID REFERENCES knowledge_sources(id) ON DELETE CASCADE;

UPDATE course_objectives co
   SET source_id = cs.source_id
  FROM course_syllabus cs
 WHERE co.course_code = cs.course_code
   AND co.source_id IS NULL;

ALTER TABLE course_objectives
    ALTER COLUMN source_id SET NOT NULL;

-- course_outcomes
ALTER TABLE course_outcomes
    ADD COLUMN source_id UUID REFERENCES knowledge_sources(id) ON DELETE CASCADE;

UPDATE course_outcomes co
   SET source_id = cs.source_id
  FROM course_syllabus cs
 WHERE co.course_code = cs.course_code
   AND co.source_id IS NULL;

ALTER TABLE course_outcomes
    ALTER COLUMN source_id SET NOT NULL;

-- course_assessments
ALTER TABLE course_assessments
    ADD COLUMN source_id UUID REFERENCES knowledge_sources(id) ON DELETE CASCADE;

UPDATE course_assessments ca
   SET source_id = cs.source_id
  FROM course_syllabus cs
 WHERE ca.course_code = cs.course_code
   AND ca.source_id IS NULL;

ALTER TABLE course_assessments
    ALTER COLUMN source_id SET NOT NULL;

-- course_references
ALTER TABLE course_references
    ADD COLUMN source_id UUID REFERENCES knowledge_sources(id) ON DELETE CASCADE;

UPDATE course_references cr
   SET source_id = cs.source_id
  FROM course_syllabus cs
 WHERE cr.course_code = cs.course_code
   AND cr.source_id IS NULL;

ALTER TABLE course_references
    ALTER COLUMN source_id SET NOT NULL;

-- course_tools
ALTER TABLE course_tools
    ADD COLUMN source_id UUID REFERENCES knowledge_sources(id) ON DELETE CASCADE;

UPDATE course_tools ct
   SET source_id = cs.source_id
  FROM course_syllabus cs
 WHERE ct.course_code = cs.course_code
   AND ct.source_id IS NULL;

ALTER TABLE course_tools
    ALTER COLUMN source_id SET NOT NULL;

-- Indexes for source-scoped queries
CREATE INDEX IF NOT EXISTS idx_course_objectives_source ON course_objectives(source_id);
CREATE INDEX IF NOT EXISTS idx_course_outcomes_source ON course_outcomes(source_id);
CREATE INDEX IF NOT EXISTS idx_course_assessments_source ON course_assessments(source_id);
CREATE INDEX IF NOT EXISTS idx_course_references_source ON course_references(source_id);
CREATE INDEX IF NOT EXISTS idx_course_tools_source ON course_tools(source_id);

-- knowledge_chunks: add page fields for citation metadata
ALTER TABLE knowledge_chunks
    ADD COLUMN page_from INT,
    ADD COLUMN page_to INT;
