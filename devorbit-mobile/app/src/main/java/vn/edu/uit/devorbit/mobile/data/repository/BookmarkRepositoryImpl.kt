package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.edu.uit.devorbit.mobile.data.remote.dto.StudentBookmarkRequest
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
import vn.edu.uit.devorbit.mobile.domain.repository.BookmarkRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> = flow {
        emit(apiService.getBookmarks().map {
            Bookmark(
                id = it.id,
                targetType = it.targetType,
                targetId = it.targetId,
                title = it.title
            )
        })
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        apiService.addBookmark(
            StudentBookmarkRequest(
                targetType = bookmark.targetType,
                targetId = bookmark.targetId,
                title = bookmark.title,
                url = ""
            )
        )
    }

    override suspend fun removeBookmark(id: Long) {
        apiService.deleteBookmark(id)
    }

    override suspend fun isBookmarked(targetType: String, targetId: Long): Boolean {
        return apiService.getBookmarks().any { it.targetType == targetType && it.targetId == targetId }
    }
}
