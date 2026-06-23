package vn.edu.uit.devorbit.admin.ui.theme

/**
 * Shared Vietnamese label mappers for backend enum values.
 * Wire values (keys) stay as backend codes; only display labels are Vietnamese.
 */

/** Mapped from [vn.edu.uit.devorbit.api.entity.Course.subjectType] */
fun subjectTypeLabel(raw: String?): String = when (raw) {
    "DAI_CUONG" -> "Đại cương"
    "CO_SO" -> "Cơ sở"
    "CHUYEN_NGANH" -> "Chuyên ngành"
    "CO_ngan_hang" -> "Cơ sở ngành"
    "MAU" -> "Mẫu"
    null, "" -> "Chưa phân loại"
    else -> raw
}

/** Mapped from [vn.edu.uit.devorbit.api.entity.CourseRelationship.relationType] */
fun relationTypeLabel(raw: String?): String = when (raw) {
    "PREREQUISITE" -> "Tiên quyết"
    "COMPLEMENTARY" -> "Bổ trợ"
    "COREQUISITE" -> "Đồng điều kiện"
    null, "" -> "Không rõ"
    else -> raw
}

/** Mapped from course status values */
fun courseStatusLabel(raw: String?): String = when (raw) {
    "ACTIVE" -> "Đang mở"
    "INACTIVE" -> "Tạm đóng"
    "DRAFT" -> "Bản nháp"
    null, "" -> "Chưa xác định"
    else -> raw
}

/** Repo candidate / approval status */
fun repoStatusLabel(raw: String?): String = when (raw) {
    "APPROVED" -> "Đã duyệt"
    "PENDING" -> "Chờ duyệt"
    "REJECTED" -> "Đã từ chối"
    null, "" -> "Chờ duyệt"
    else -> raw
}

/** Snippet count label */
fun snippetCountLabel(count: Int): String = when (count) {
    0 -> "Chưa có đoạn mã"
    1 -> "1 đoạn mã"
    else -> "$count đoạn mã"
}

/** Mapped from note target type (COURSE / REPO) */
fun noteTargetTypeLabel(raw: String?): String = when (raw) {
    "COURSE" -> "Môn học"
    "REPO" -> "Kho lưu trữ"
    else -> "Chung"
}

/** Mapped from chat channel type */
fun channelTypeLabel(raw: String?): String = when (raw) {
    "GENERAL" -> "Chung"
    "COURSE" -> "Môn học"
    "TECH_STACK" -> "Công nghệ"
    null, "" -> "Chưa rõ"
    else -> raw
}

/** Mapped from submission status */
fun submissionStatusLabel(raw: String?): String = when (raw) {
    "APPROVED" -> "Đã duyệt"
    "PENDING" -> "Chờ duyệt"
    "REJECTED" -> "Đã từ chối"
    "UNDER_REVIEW" -> "Đang xem xét"
    null, "" -> "Chưa rõ"
    else -> raw
}
