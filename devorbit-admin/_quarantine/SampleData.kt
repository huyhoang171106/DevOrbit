package vn.edu.uit.devorbit.admin.data

import vn.edu.uit.devorbit.admin.components.ResponseSectionType
import vn.edu.uit.devorbit.admin.components.SourceData
import vn.edu.uit.devorbit.admin.design.OrbitColors

// ═══════════════════════════════════════════════════════════════════════════════
// Sample Data Provider
// ═══════════════════════════════════════════════════════════════════════════════
//
// All sample instances use OrbitColors values for consistency with the design
// system. Dates are ISO-8601 relative to 2026-06-23 (today).
//
// ═══════════════════════════════════════════════════════════════════════════════

object SampleData {

    // ── Resources: Đại số tuyến tính ────────────────────────────────────────────
    private val daiSoTuyenTinhResources1: List<Resource> = listOf(
        Resource(
            id = "res-mth101-1-1",
            title = "Matrix Operations — 3Blue1Brown",
            type = "video",
            url = "https://youtube.com/watch?v=fNk_zzaMoSs",
            source = "3Blue1Brown",
            relevance = 0.95f,
        ),
        Resource(
            id = "res-mth101-1-2",
            title = "Đại số tuyến tính — Bài 1: Ma trận",
            type = "video",
            url = "https://youtube.com/...",
            source = "Bài giảng UIT",
            relevance = 0.90f,
        ),
    )

    private val daiSoTuyenTinhResources2: List<Resource> = listOf(
        Resource(
            id = "res-mth101-2-1",
            title = "Giải hệ phương trình bằng phương pháp Gauss",
            type = "article",
            url = "https://example.com/gauss-elimination",
            source = "Khan Academy",
            relevance = 0.85f,
        ),
    )

    private val daiSoTuyenTinhResources3: List<Resource> = listOf(
        Resource(
            id = "res-mth101-3-1",
            title = "Không gian vector — Toán cao cấp A1",
            type = "documentation",
            url = "https://example.com/vector-spaces",
            source = "Giáo trình UIT",
            relevance = 0.92f,
        ),
    )

    // ── Resources: Giải tích 1 ──────────────────────────────────────────────────
    private val giaiTich1Resources1: List<Resource> = listOf(
        Resource(
            id = "res-mth102-1-1",
            title = "Giới hạn hàm số — Bài giảng",
            type = "video",
            url = "https://youtube.com/...",
            source = "Bài giảng UIT",
            relevance = 0.88f,
        ),
    )

    private val giaiTich1Resources2: List<Resource> = listOf(
        Resource(
            id = "res-mth102-2-1",
            title = "Derivatives — Khan Academy",
            type = "article",
            url = "https://khanacademy.org/...",
            source = "Khan Academy",
            relevance = 0.80f,
        ),
        Resource(
            id = "res-mth102-2-2",
            title = "Bài tập đạo hàm có lời giải",
            type = "documentation",
            url = "https://example.com/derivative-exercises",
            source = "Giáo trình UIT",
            relevance = 0.75f,
        ),
    )

    private val giaiTich1Resources3: List<Resource> = listOf(
        Resource(
            id = "res-mth102-3-1",
            title = "Tích phân từng phần và đổi biến",
            type = "video",
            url = "https://youtube.com/...",
            source = "Bài giảng UIT",
            relevance = 0.85f,
        ),
    )

    // ── Resources: OOP ──────────────────────────────────────────────────────────
    private val oopResources1: List<Resource> = listOf(
        Resource(
            id = "res-csc201-1-1",
            title = "Java Classes and Objects — Oracle Docs",
            type = "documentation",
            url = "https://docs.oracle.com/javase/tutorial/java/javaOO/",
            source = "Oracle",
            relevance = 0.95f,
        ),
    )

    private val oopResources2: List<Resource> = listOf(
        Resource(
            id = "res-csc201-2-1",
            title = "Java Inheritance Tutorial",
            type = "article",
            url = "https://example.com/java-inheritance",
            source = "Codecademy",
            relevance = 0.82f,
        ),
    )

    private val oopResources3: List<Resource> = listOf(
        Resource(
            id = "res-csc201-3-1",
            title = "Exceptions and Generics — Baeldung",
            type = "article",
            url = "https://baeldung.com/java-exceptions-generics",
            source = "Baeldung",
            relevance = 0.78f,
        ),
    )

    // ── Resources: DSA ──────────────────────────────────────────────────────────
    private val dsaResources1: List<Resource> = listOf(
        Resource(
            id = "res-csc202-1-1",
            title = "Big O Notation Explained — FreeCodeCamp",
            type = "video",
            url = "https://youtube.com/...",
            source = "FreeCodeCamp",
            relevance = 0.90f,
        ),
    )

    private val dsaResources2: List<Resource> = listOf(
        Resource(
            id = "res-csc202-2-1",
            title = "Linked Lists — GeeksforGeeks",
            type = "article",
            url = "https://geeksforgeeks.org/data-structures/linked-list/",
            source = "GeeksforGeeks",
            relevance = 0.85f,
        ),
    )

    private val dsaResources3: List<Resource> = listOf(
        Resource(
            id = "res-csc202-3-1",
            title = "Binary Search Tree — Visualgo",
            type = "quiz",
            url = "https://visualgo.net/en/bst",
            source = "VisuAlgo",
            relevance = 0.80f,
        ),
    )

    // ── Resources: CSDL ─────────────────────────────────────────────────────────
    private val csdlResources1: List<Resource> = listOf(
        Resource(
            id = "res-csc301-1-1",
            title = "Relational Model — Stanford DB Class",
            type = "video",
            url = "https://youtube.com/...",
            source = "Stanford",
            relevance = 0.88f,
        ),
    )

    private val csdlResources2: List<Resource> = listOf(
        Resource(
            id = "res-csc301-2-1",
            title = "SQL Tutorial — W3Schools",
            type = "documentation",
            url = "https://w3schools.com/sql/",
            source = "W3Schools",
            relevance = 0.92f,
        ),
    )

    // ── Subject Modules: Đại số tuyến tính ──────────────────────────────────────
    private val daiSoTuyenTinhModules: List<SubjectModule> = listOf(
        SubjectModule(
            id = "mth101-m1",
            title = "Chương 1: Ma trận và định thức",
            description = "Ma trận, các phép toán ma trận, định thức và tính chất, ma trận nghịch đảo.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = daiSoTuyenTinhResources1,
        ),
        SubjectModule(
            id = "mth101-m2",
            title = "Chương 2: Hệ phương trình tuyến tính",
            description = "Phương pháp Gauss, hệ Cramer, hệ thuần nhất, ứng dụng.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = daiSoTuyenTinhResources2,
        ),
        SubjectModule(
            id = "mth101-m3",
            title = "Chương 3: Không gian vector",
            description = "Không gian vector, độc lập tuyến tính, cơ sở, số chiều, toạ độ.",
            state = ModuleState.Current,
            progress = 0.45f,
            resources = daiSoTuyenTinhResources3,
        ),
        SubjectModule(
            id = "mth101-m4",
            title = "Chương 4: Ánh xạ tuyến tính",
            description = "Ánh xạ tuyến tính, nhân và ảnh, ma trận biểu diễn, trị riêng — vector riêng.",
            state = ModuleState.Locked,
            progress = 0.0f,
            resources = emptyList(),
        ),
    )

    // ── Subject Modules: Giải tích 1 ────────────────────────────────────────────
    private val giaiTich1Modules: List<SubjectModule> = listOf(
        SubjectModule(
            id = "mth102-m1",
            title = "Chương 1: Giới hạn và liên tục",
            description = "Giới hạn hàm số, vô cùng lớn — vô cùng bé, hàm số liên tục.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = giaiTich1Resources1,
        ),
        SubjectModule(
            id = "mth102-m2",
            title = "Chương 2: Đạo hàm và vi phân",
            description = "Đạo hàm, vi phân, quy tắc L'Hospital, khảo sát hàm số.",
            state = ModuleState.Current,
            progress = 0.70f,
            resources = giaiTich1Resources2,
        ),
        SubjectModule(
            id = "mth102-m3",
            title = "Chương 3: Tích phân",
            description = "Tích phân xác định, tích phân suy rộng, ứng dụng tính diện tích và thể tích.",
            state = ModuleState.Available,
            progress = 0.0f,
            resources = giaiTich1Resources3,
        ),
    )

    // ── Subject Modules: OOP ────────────────────────────────────────────────────
    private val oopModules: List<SubjectModule> = listOf(
        SubjectModule(
            id = "csc201-m1",
            title = "Chương 1: Tổng quan OOP",
            description = "Lớp, đối tượng, thuộc tính, phương thức, constructor, overloading.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = oopResources1,
        ),
        SubjectModule(
            id = "csc201-m2",
            title = "Chương 2: Kế thừa và Đa hình",
            description = "Kế thừa, ghi đè phương thức, lớp trừu tượng, interface.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = oopResources2,
        ),
        SubjectModule(
            id = "csc201-m3",
            title = "Chương 3: Ngoại lệ và Generic",
            description = "Xử lý ngoại lệ checked/unchecked, generic class và method.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = oopResources3,
        ),
        SubjectModule(
            id = "csc201-m4",
            title = "Chương 4: Design Patterns",
            description = "Singleton, Factory, Observer, Strategy — áp dụng vào bài tập lớn.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = emptyList(),
        ),
    )

    // ── Subject Modules: DSA ────────────────────────────────────────────────────
    private val dsaModules: List<SubjectModule> = listOf(
        SubjectModule(
            id = "csc202-m1",
            title = "Chương 1: Phân tích độ phức tạp",
            description = "Big-O notation, phân tích thuật toán đệ quy, master theorem.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = dsaResources1,
        ),
        SubjectModule(
            id = "csc202-m2",
            title = "Chương 2: Danh sách liên kết và Ngăn xếp",
            description = "Singly/doubly linked list, stack, queue, ứng dụng.",
            state = ModuleState.Current,
            progress = 0.50f,
            resources = dsaResources2,
        ),
        SubjectModule(
            id = "csc202-m3",
            title = "Chương 3: Cây nhị phân",
            description = "Cây nhị phân tìm kiếm, AVL, heap, các phép duyệt cây.",
            state = ModuleState.Available,
            progress = 0.0f,
            resources = dsaResources3,
        ),
    )

    // ── Subject Modules: CSDL ───────────────────────────────────────────────────
    private val csdlModules: List<SubjectModule> = listOf(
        SubjectModule(
            id = "csc301-m1",
            title = "Chương 1: Mô hình quan hệ",
            description = "Quan hệ, bộ, thuộc tính, khoá, ràng buộc toàn vẹn, đại số quan hệ.",
            state = ModuleState.Completed,
            progress = 1.0f,
            resources = csdlResources1,
        ),
        SubjectModule(
            id = "csc301-m2",
            title = "Chương 2: SQL",
            description = "DDL, DML, truy vấn cơ bản, truy vấn lồng, JOIN, GROUP BY, HAVING.",
            state = ModuleState.Current,
            progress = 0.20f,
            resources = csdlResources2,
        ),
        SubjectModule(
            id = "csc301-m3",
            title = "Chương 3: Chuẩn hoá",
            description = "Phụ thuộc hàm, dạng chuẩn 1NF, 2NF, 3NF, BCNF, phân rã bảo toàn.",
            state = ModuleState.Available,
            progress = 0.0f,
            resources = emptyList(),
        ),
    )

    // ── Subjects ─────────────────────────────────────────────────────────────────
    val subjects: List<Subject> by lazy { listOf(
        Subject(
            id = "subj-1",
            code = "MTH101",
            title = "Đại số tuyến tính",
            description = "Ma trận, định thức, hệ phương trình tuyến tính, không gian vector, ánh xạ tuyến tính, trị riêng và vector riêng.",
            credits = 4,
            difficulty = "Trung bình",
            progress = 0.85f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartBlue,
            nextTask = "Bài tập chương 4: Không gian vector",
            estimatedTime = "2h 30m",
            semester = "HK1 Năm 1",
            isBookmarked = true,
            modules = daiSoTuyenTinhModules,
        ),
        Subject(
            id = "subj-2",
            code = "MTH102",
            title = "Giải tích 1",
            description = "Giới hạn, đạo hàm, tích phân hàm một biến, khảo sát hàm số và ứng dụng.",
            credits = 4,
            difficulty = "Khó",
            progress = 0.60f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartCyan,
            nextTask = "Ôn tập giữa kỳ: Tích phân từng phần",
            estimatedTime = "3h",
            semester = "HK1 Năm 1",
            isBookmarked = false,
            modules = giaiTich1Modules,
        ),
        Subject(
            id = "subj-3",
            code = "CSC201",
            title = "Lập trình hướng đối tượng",
            description = "Lớp, đối tượng, kế thừa, đa hình, trừu tượng, ngoại lệ, generic, design patterns cơ bản với Java.",
            credits = 3,
            difficulty = "Trung bình",
            progress = 0.95f,
            status = SubjectStatus.Completed,
            color = OrbitColors.ChartGreen,
            nextTask = "",
            estimatedTime = "—",
            semester = "HK2 Năm 1",
            isBookmarked = false,
            modules = oopModules,
        ),
        Subject(
            id = "subj-4",
            code = "CSC202",
            title = "Cấu trúc dữ liệu và Giải thuật",
            description = "Mảng, danh sách liên kết, ngăn xếp, hàng đợi, cây, đồ thị, sắp xếp, tìm kiếm, phân tích độ phức tạp.",
            credits = 3,
            difficulty = "Khó",
            progress = 0.35f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartOrange,
            nextTask = "Cây nhị phân: triển khai BST",
            estimatedTime = "2h",
            semester = "HK2 Năm 1",
            isBookmarked = true,
            modules = dsaModules,
        ),
        Subject(
            id = "subj-5",
            code = "CSC301",
            title = "Cơ sở dữ liệu",
            description = "Mô hình quan hệ, đại số quan hệ, SQL, chuẩn hoá, giao dịch, tối ưu truy vấn, thiết kế CSDL.",
            credits = 3,
            difficulty = "Trung bình",
            progress = 0.15f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartYellow,
            nextTask = "Bài tập SQL: Truy vấn nhiều bảng",
            estimatedTime = "1h 30m",
            semester = "HK1 Năm 2",
            isBookmarked = false,
            modules = csdlModules,
        ),
    )
    }

    // ── Current Week Study Sessions (2026-06-22 to 2026-06-28) ──────────────────

    /** Week starting Monday 2026-06-22. Adjusted: Monday is 22nd. */
    val currentWeekSessions: List<StudySession> = listOf(
        // Monday 22/6
        StudySession("ses-1", "Ma trận & định thức (ôn)", "subj-1", "2026-06-22", "08:00", "1h 30m", true, OrbitColors.ChartBlue),
        StudySession("ses-2", "Giới hạn hàm số — bài tập", "subj-2", "2026-06-22", "10:00", "1h", true, OrbitColors.ChartCyan),
        // Tuesday 23/6 (today)
        StudySession("ses-3", "Không gian vector — lý thuyết", "subj-1", "2026-06-23", "08:00", "1h 30m", true, OrbitColors.ChartBlue),
        StudySession("ses-4", "Đạo hàm — ôn giữa kỳ", "subj-2", "2026-06-23", "10:00", "1h", false, OrbitColors.ChartCyan),
        StudySession("ses-5", "Danh sách liên kết — triển khai", "subj-4", "2026-06-23", "14:00", "2h", false, OrbitColors.ChartOrange),
        // Wednesday 24/6
        StudySession("ses-6", "SQL — truy vấn cơ bản", "subj-5", "2026-06-24", "08:00", "1h 30m", false, OrbitColors.ChartYellow),
        StudySession("ses-7", "Stack & Queue — bài tập", "subj-4", "2026-06-24", "10:00", "1h", false, OrbitColors.ChartOrange),
        // Thursday 25/6
        StudySession("ses-8", "Ánh xạ tuyến tính — khởi động", "subj-1", "2026-06-25", "08:00", "1h", false, OrbitColors.ChartBlue),
        StudySession("ses-9", "Tích phân — công thức cơ bản", "subj-2", "2026-06-25", "09:30", "1h 30m", false, OrbitColors.ChartCyan),
        // Friday 26/6
        StudySession("ses-10", "Cây nhị phân — BST", "subj-4", "2026-06-26", "08:00", "2h", false, OrbitColors.ChartOrange),
        StudySession("ses-11", "SQL JOIN — thực hành", "subj-5", "2026-06-26", "14:00", "1h 30m", false, OrbitColors.ChartYellow),
        // Saturday 27/6
        StudySession("ses-12", "Ôn tập cuối tuần — Đại số", "subj-1", "2026-06-27", "08:00", "2h", false, OrbitColors.ChartBlue),
        StudySession("ses-13", "Ôn tập cuối tuần — Giải tích", "subj-2", "2026-06-27", "10:30", "2h", false, OrbitColors.ChartCyan),
        // Sunday 28/6
        StudySession("ses-14", "Nghỉ / Tự học", "--", "2026-06-28", "--", "—", false, OrbitColors.ChartMuted),
    )

    // ── Study Plan with 3 phases ───────────────────────────────────────────────
    val sampleStudyPlan: StudyPlan = StudyPlan(
        id = "plan-1",
        title = "Chuẩn bị thi giữa kỳ HK2",
        startDate = "2026-06-16",
        endDate = "2026-07-14",
        phases = listOf(
            PlanPhase(
                id = "phase-1",
                title = "Ôn tập nền tảng",
                description = "Củng cố kiến thức Đại số tuyến tính và Giải tích 1 — chương 1 và 2.",
                startDay = 0,
                endDay = 6,
                modules = listOf("mth101-m1", "mth101-m2", "mth102-m1"),
                completed = true,
            ),
            PlanPhase(
                id = "phase-2",
                title = "Chuyên sâu DSA và SQL",
                description = "Hoàn thành DSA chương 2-3, CSDL chương 2, luyện tập truy vấn SQL.",
                startDay = 7,
                endDay = 20,
                modules = listOf("csc202-m2", "csc202-m3-partial", "csc301-m2"),
                completed = false,
            ),
            PlanPhase(
                id = "phase-3",
                title = "Tổng ôn và thi thử",
                description = "Giải đề thi các năm, tổng kết toàn bộ môn đang học, hoàn thành đồ án nhỏ.",
                startDay = 21,
                endDay = 28,
                modules = listOf("mth101-m3", "mth102-m2", "csc202-m3", "csc301-m2"),
                completed = false,
            ),
        ),
        sessions = currentWeekSessions,
    )

    // ── Tutor Conversation ──────────────────────────────────────────────────────
    val sampleTutorConversation: List<TutorMessage> = listOf(
        TutorMessage(
            id = "msg-1",
            role = MessageRole.User,
            mode = TutorMode.Explain,
            content = "Giải thích giúp mình khái niệm không gian vector và cách kiểm tra một tập hợp có phải không gian vector không.",
            sections = emptyList(),
            sources = emptyList(),
        ),
        TutorMessage(
            id = "msg-2",
            role = MessageRole.AI,
            mode = TutorMode.Explain,
            content = """
                **Không gian vector** là một tập hợp V trang bị hai phép toán — cộng vector và nhân vô hướng — thoả mãn 8 tiên đề.

                ### Điều kiện kiểm tra
                Cho tập V ⊆ ℝⁿ với phép cộng và nhân vô hướng thông thường, kiểm tra:
                1. **Đóng kín với phép cộng:** ∀ u, v ∈ V, u + v ∈ V
                2. **Đóng kín với nhân vô hướng:** ∀ v ∈ V, ∀ c ∈ ℝ, c·v ∈ V
                3. **Chứa vector không:** 0 ∈ V
                4. **Phần tử đối:** ∀ v ∈ V, -v ∈ V
            """.trimIndent(),
            sections = listOf(
                ResponseSection(ResponseSectionType.KeyConcept, "Định nghĩa hình thức", """
                    Một không gian vector trên trường ℝ là một bộ (V, +, ·) thoả mãn:
                    • (V, +) là nhóm Abel — giao hoán, kết hợp, có phần tử trung hoà, có phần tử đối.
                    • Phép nhân vô hướng phân phối với phép cộng vector và cộng vô hướng.
                """.trimIndent()),
                ResponseSection(ResponseSectionType.Example, "Ví dụ: ℝ²", """
                    Xét V = ℝ². Với (x₁, y₁) + (x₂, y₂) = (x₁+x₂, y₁+y₂) và c·(x, y) = (cx, cy):
                    • ℝ² đóng kín, có (0,0), phần tử đối là (-x, -y).
                    → ℝ² là không gian vector trên ℝ.
                """.trimIndent()),
                ResponseSection(ResponseSectionType.NextAction, "Bài tập đề xuất", """
                    Kiểm tra xem tập đường thẳng y = 2x trong ℝ² có phải không gian vector không.
                """.trimIndent()),
            ),
            sources = listOf(
                SourceData("Đại số tuyến tính — Chương 3", "documentation", "Giáo trình UIT", 0.95f),
                SourceData("3Blue1Brown — Vector spaces", "video", "youtube.com", 0.90f, "https://youtube.com/watch?v=fNk_zzaMoSs"),
            ),
        ),
        TutorMessage(
            id = "msg-3",
            role = MessageRole.User,
            mode = TutorMode.Practice,
            content = "Cho mình bài tập về phép biến đổi Gauss.",
            sections = emptyList(),
            sources = emptyList(),
        ),
        TutorMessage(
            id = "msg-4",
            role = MessageRole.AI,
            mode = TutorMode.Practice,
            content = """
                ### Bài tập: Giải hệ phương trình bằng Gauss

                Giải hệ sau bằng phương pháp khử Gauss:
                ⎧  x + 2y -  z = 1
                ⎨ 2x + 3y +  z = 4
                ⎩ 3x +  y + 2z = 7

                **Hướng dẫn:** Lập ma trận mở rộng, đưa về dạng bậc thang, giải ngược từ dưới lên.
            """.trimIndent(),
            sections = listOf(
                ResponseSection(ResponseSectionType.Warning, "Lưu ý", """
                    Khi biến đổi, nếu một hàng trở thành toàn 0 với hệ số tự do ≠ 0 thì hệ vô nghiệm.
                """.trimIndent()),
            ),
            sources = listOf(
                SourceData("Bài tập Đại số tuyến tính — UIT", "documentation", "Giáo trình UIT", 0.88f),
            ),
        ),
    )

    // ── Profile ────────────────────────────────────────────────────────────────
    val sampleProfile: UserProfile = UserProfile(
        name = "Nguyễn Văn An",
        email = "an.nguyen@student.uit.edu.vn",
        avatar = "",
        level = 7,
        xp = 2450,
        streak = 5,
        totalHours = 128.5f,
        joinDate = "2025-09-01",
        goals = listOf(
            "Hoàn thành DSA trước tháng 8",
            "Đạt GPA 3.5+ học kỳ này",
            "Làm đồ án Java Spring Boot",
        ),
        achievements = listOf(
            Achievement("ach-1", "Chăm chỉ", "Hoàn thành 7 ngày học liên tiếp", "streak", true, 1.0f, "2026-06-18T10:00:00"),
            Achievement("ach-2", "OOP Master", "Hoàn thành Lập trình HĐT với điểm A", "code", true, 1.0f, "2026-05-20T14:30:00"),
            Achievement("ach-3", "SQL Cơ bản", "Viết 20 truy vấn SQL đúng", "database", false, 0.65f, null),
            Achievement("ach-4", "Giải thuật", "Giải 10 bài DSA trên LeetCode", "algorithm", false, 0.30f, null),
            Achievement("ach-5", "Cần cù", "Đạt 100 giờ học trên nền tảng", "clock", true, 1.0f, "2026-06-01T09:00:00"),
            Achievement("ach-6", "Toán học", "Hoàn thành Đại số tuyến tính với điểm B+ trở lên", "calculator", false, 0.85f, null),
            Achievement("ach-7", "Nhóm trưởng", "Dẫn dắt nhóm đạt điểm đồ án OOP", "users", true, 1.0f, "2026-05-10T16:00:00"),
        ),
    )

    // ── Subject Distribution ────────────────────────────────────────────────────
    val sampleSubjectDistribution: List<SubjectDistribution> = listOf(
        SubjectDistribution("Đại số tuyến tính", 34.0f, OrbitColors.ChartBlue),
        SubjectDistribution("Giải tích 1", 28.5f, OrbitColors.ChartCyan),
        SubjectDistribution("Lập trình HĐT", 32.0f, OrbitColors.ChartGreen),
        SubjectDistribution("CTDL và Giải thuật", 18.0f, OrbitColors.ChartOrange),
        SubjectDistribution("Cơ sở dữ liệu", 16.0f, OrbitColors.ChartYellow),
    )

    // ── Weekly Stats (last 6 weeks) ─────────────────────────────────────────────
    val sampleWeeklyStats: List<WeeklyStats> = listOf(
        WeeklyStats("2026-05-18", 8.5f, 0.60f, 5),
        WeeklyStats("2026-05-25", 12.0f, 0.75f, 8),
        WeeklyStats("2026-06-01", 10.5f, 0.70f, 7),
        WeeklyStats("2026-06-08", 14.0f, 0.85f, 10),
        WeeklyStats("2026-06-15", 11.5f, 0.80f, 8),
        WeeklyStats("2026-06-22", 5.5f, 0.50f, 3),  // current week (in progress)
    )

    // ── Convenience: TimelineDay mapping for StudyTimeline component ────────────
    // The StudyTimeline component expects List<TimelineDay>. Use this helper
    // to build it from currentWeekSessions.
    fun buildTimelineDays(): List<vn.edu.uit.devorbit.admin.components.TimelineDay> {
        val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        val dayDates = listOf(22, 23, 24, 25, 26, 27, 28)
        // today marker: 23rd
        return dayLabels.mapIndexed { index, label ->
            val date = dayDates[index]
            val sessionsForDay = currentWeekSessions.filter { ses ->
                val sesDay = ses.date.substringAfterLast("-").toIntOrNull()
                sesDay == date
            }
            vn.edu.uit.devorbit.admin.components.TimelineDay(
                dayOfWeek = label,
                date = date,
                isToday = date == 23,
                sessions = sessionsForDay.map { ses ->
                    vn.edu.uit.devorbit.admin.components.TimelineSession(
                        id = ses.id,
                        title = ses.title,
                        duration = ses.duration,
                        completed = ses.completed,
                        subject = subjects.find { it.id == ses.subjectId }?.code ?: ses.subjectId,
                        color = ses.color,
                    )
                },
            )
        }
    }
}
