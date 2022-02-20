package com.compose.book.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    val chapterTitle: String,
    val chapterUrl: String,
    val bookId: Int,
    val chapterIndex: Int,
    @PrimaryKey
    val chapterId: Int = chapterUrl.hashCode(),
    var isCached: Boolean = false
) {
    @Ignore
    var paragraphs: List<Paragraph> = emptyList()
    var prevChapterUrl: String? = null
    var nextChapterUrl: String? = null


    var scrollPos = 0

    companion object {
        val emptyChapter = Chapter("", "", 0, 0)
    }

    override fun toString(): String {
        return "Chapter(" +
                "index='$chapterIndex',"+
                "chapterTitle='$chapterTitle', " +
                "chapterUrl='$chapterUrl', " +
                "chapterId=$chapterId, " +
                "paragraphs=${paragraphs.map { '\n' + it.text }}, "+
                "isCached=$isCached"
    }

    fun overallString(): String {
        return "Chapter(chapterId=$chapterId, " +
                "chapterTitle='$chapterTitle', " +
                "chapterUrl='$chapterUrl', " +
                "paragraphSize=${paragraphs.size}, " +
                "prevChapterUrl=$prevChapterUrl, " +
                "nextChapterUrl=$nextChapterUrl, " +
                "isCached=$isCached"
    }
}

