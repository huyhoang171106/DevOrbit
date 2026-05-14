package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
import vn.edu.uit.devorbit.mobile.domain.repository.BookmarkRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor() : BookmarkRepository {

    private val bookmarks = mutableListOf<Bookmark>()

    override fun getAllBookmarks(): Flow<List<Bookmark>> = flow { emit(bookmarks.toList()) }

    override suspend fun addBookmark(bookmark: Bookmark) {
        if (bookmarks.none { it.targetType == bookmark.targetType && it.targetId == bookmark.targetId }) {
            bookmarks.add(bookmark.copy(id = (bookmarks.maxOfOrNull { it.id } ?: 0) + 1))
        }
    }

    override suspend fun removeBookmark(id: Long) {
        bookmarks.removeAll { it.id == id }
    }

    override suspend fun isBookmarked(targetType: String, targetId: Long): Boolean =
        bookmarks.any { it.targetType == targetType && it.targetId == targetId }
}
