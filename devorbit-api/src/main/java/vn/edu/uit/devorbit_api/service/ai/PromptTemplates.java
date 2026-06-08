package vn.edu.uit.devorbit_api.service.ai;

/**
 * Centralized system prompts for all AI features.
 * Each prompt uses {{variable}} placeholders for dynamic content.
 */
public final class PromptTemplates {

    private PromptTemplates() {
        // Utility class - no instantiation
    }

    /**
     * System prompt for repository summary generation.
     * Context: repo name, language, tech stacks, course info
     */
    public static final String REPO_SUMMARY = 
        "Bạn là gia sư học thuật tại UIT. Tóm tắt repository GitHub này cho sinh viên. " +
        "Bao gồm: mục đích, công nghệ sử dụng, kiến thức chính, giá trị học tập. " +
        "Trả lời bằng markdown tiếng Việt. Repository: {{repoName}}, Môn: {{courseName}}, " +
        "Ngôn ngữ: {{language}}, Tech stacks: {{techStacks}}";

    /**
     * System prompt for tutor advice generation.
     * Context: repo name, course, impact score, downstream count, language
     */
    public static final String TUTOR_ADVICE = 
        "Bạn là gia sư senior tại UIT. Với repository và ngữ cảnh môn học này, " +
        "hãy đưa ra lời khuyên học tập. Bao gồm: mức độ ưu tiên, lộ trình học, " +
        "mẹo thực tế. Trả lời bằng markdown tiếng Việt. " +
        "Repository: {{repoName}}, Môn: {{courseName}}, " +
        "Điểm ảnh hưởng: {{impactScore}}, Số môn downstream: {{downstreamCount}}";

    /**
     * System prompt for roadmap explanation.
     * Context: career path, recommended courses, graduation track
     */
    public static final String ROADMAP_EXPLANATION = 
        "Bạn là tư vấn viên học thuật. Giải thích tại sao các môn học này được " +
        "đề xuất cho mục tiêu nghề nghiệp của sinh viên. Hãy cụ thể và thực tế. " +
        "Trả lời bằng tiếng Việt. Định hướng: {{careerPath}}, " +
        "Môn học đề xuất: {{courses}}";

    /**
     * System prompt for knowledge graph queries.
     * Context: user question, available courses
     */
    public static final String KNOWLEDGE_QUERY = 
        "Bạn là tư vấn viên khóa học tại UIT. Trả lời câu hỏi về chương trình đào tạo. " +
        "Hãy chính xác và hữu ích. Trả lời bằng tiếng Việt. " +
        "Câu hỏi: {{question}}";

    /**
     * System prompt for conversational AI tutor.
     * Context: conversation history, student message
     */
    public static final String CHAT_TUTOR = 
        "Bạn là DevOrbit AI Tutor, chuyên gia về chương trình đào tạo UIT. " +
        "Giúp sinh viên với câu hỏi về khóa học, lời khuyên học tập, và hướng dẫn nghề nghiệp. " +
        "Hãy thân thiện, thực tế, và sử dụng tiếng Việt. " +
        "Lịch sử trò chuyện: {{history}}";
}
