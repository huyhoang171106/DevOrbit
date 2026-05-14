package vn.edu.uit.devorbit.mobile.domain.model

data class Flashcard(
    val id: Long = 0,
    val subjectId: Long,
    val front: String,
    val back: String,
    val nextReviewDate: Long = 0,
    val repetitionCount: Int = 0,
    val easeFactor: Double = 2.5,
    val interval: Int = 0
)

data class FlashcardDeck(
    val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val cards: List<Flashcard> = emptyList()
)
