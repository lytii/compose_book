package com.compose.book

import com.compose.book.data.TrashFamily
import com.compose.book.di.DaggerTestComponent
import org.junit.Test

class TrashParseChapterListTest {
    val component = DaggerTestComponent.create()
    val api = component.api()

    @Test
    fun allList() {
        val allUrl =
            "https://novelfull.com/ajax-chapter-option?novelId=835&currentChapterId=1091829"
        api.getChapter(allUrl)
            .map { TrashFamily.parseChapterListUrls(it) }
            .subscribe { s ->
                println(s)
            }
    }
}