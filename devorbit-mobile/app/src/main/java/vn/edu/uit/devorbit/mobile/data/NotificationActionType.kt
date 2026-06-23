package vn.edu.uit.devorbit.mobile.data

enum class NotificationActionType {
    GROUP_PLAN_INVITE,
    GROUP_PLAN_RESPONSE,
    GROUP_TASK_ADDED,
    GROUP_TASK_DELETE_REQUEST,
    GROUP_TASK_DELETE_APPROVED,
    GROUP_PLAN_DELETE_REQUEST,
    GROUP_PLAN_DELETE_APPROVED,
    OTHER;

    companion object {
        fun fromType(type: String): NotificationActionType {
            return try {
                valueOf(type)
            } catch (_: IllegalArgumentException) {
                OTHER
            }
        }
    }
}
