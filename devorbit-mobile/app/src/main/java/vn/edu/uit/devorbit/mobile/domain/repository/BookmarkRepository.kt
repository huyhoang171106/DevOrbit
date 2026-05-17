package vn.edu.uit.devorbit.mobile.domain.repository

import kotlinx.coroutines.flow.Flow

data class Bookmark(val id: Long, val targetType: String, val targetId: Long, val title: String)

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun removeBookmark(id: Long)
    suspend fun isBookmarked(targetType: String, targetId: Long): Boolean
}
