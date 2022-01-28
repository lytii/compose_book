package com.compose.book

import com.compose.book.data.Chapter
import com.compose.book.data.TrashFamily
import com.compose.book.data.TrashFamily.addParagraphsTo
import com.compose.book.data.TrashFamily.parseParagraphs
import com.compose.book.di.DaggerTestComponent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TrashCountUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testNetworkChapterParse() {
        val component = DaggerTestComponent.create()
        val api = component.api()

        val url = TrashFamily.demoChapterUrl
        val list = api.getChapter(url)
            .map { TrashFamily.parseChapter(it) }
            .blockingGet()
        println(list.joinToString("\n"))
    }

    @Test
    fun testChapterParse() {
        val file: File = getFileFromPath("trash_chapter.json")

        val chapter = Chapter(
            chapterTitle = "title",
            chapterUrl = "url",
            bookId = TrashFamily.bookId,
            index = 637
        )
        val parse: Document = Jsoup.parse(file, "UTF-8")

        val chapterWithParagraph = parse.addParagraphsTo(chapter)
        assert(chapterWithParagraph.prevChapterUrl?.contains("637") == true)
        assert(chapterWithParagraph.nextChapterUrl?.contains("639") == true)
        assert(chapterWithParagraph.paragraphs.size == 275) {
            "${chapterWithParagraph.paragraphs.size} expected 275"
        }
        println(chapterWithParagraph)
    }

    @Test
    fun testParagraphsParse() {
        val file: File = getFileFromPath("trash_chapter.json")

        val chapter = Chapter(
            chapterTitle = "title",
            chapterUrl = "url",
            bookId = TrashFamily.bookId,
            index = 637
        )
        val parse: Document = Jsoup.parse(file, "UTF-8")

        val chapterWithParagraph = parse.parseParagraphs()
//        assert(chapterWithParagraph.prevChapterUrl?.contains("637") == true)
//        assert(chapterWithParagraph.nextChapterUrl?.contains("639") == true)
//        assert(chapterWithParagraph.paragraphs.size == 275) {
//            "${chapterWithParagraph.paragraphs.size} expected 275"
//        }
        println(chapterWithParagraph)
    }
}

fun Any.getFileFromPath(fileName: String): File {
    val classLoader = this.javaClass.classLoader
    val resource: URL = classLoader!!.getResource(fileName)
    return File(resource.path)
}