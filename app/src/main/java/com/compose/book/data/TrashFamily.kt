package com.compose.book.data

import androidx.annotation.VisibleForTesting
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber

object TrashFamily {
    val demoChapterUrl = "https://novelfull.com/trash-of-the-counts-family/chapter-715-are-you-sure-that-youre-a-god-2.html"

    private val name = this.javaClass.simpleName
    val chapterListUrl =
        "https://novelfull.com/ajax-chapter-option?novelId=835&currentChapterId=1091829"
    private val allUrl =
        "https://novelfull.com/ajax-chapter-option?novelId=835&currentChapterId=1091829"

    fun parseChapterListUrls(doc: Document): List<Chapter> =
        doc.select("option")
            .mapIndexed { index, it ->
                Chapter(
                    chapterTitle = it.text(),
                    chapterUrl = it.attr("value"),
                    index = index,
                    bookId = name.hashCode()
                )
            }

    fun parseChapterListUrls(body: ResponseBody): List<Chapter> =
        parseChapterListUrls(Jsoup.parse(body.string()))


    fun parseChapter(responseBody: ResponseBody, chapter: Chapter): Chapter {
        return Jsoup.parse(responseBody.string())
            .addParagraphsTo(chapter)
    }

    @VisibleForTesting
    fun Document.addParagraphsTo(chapter: Chapter): Chapter {
        val list = select("#chapter-content :not(script):not(div)")
            .filter { it.childNodeSize() > 0 }
            .mapIndexed { index, element ->
                Paragraph(
                    index = index,
                    chapterId = chapter.chapterId,
                    text = element.toString(),
                    isHeader = element.tagName() == "h4"
                )
            }

        val nav = select(".btn-group")
        val next = nav.select("#next_chap").attr("href")
        val prev = nav.select("#prev_chap").attr("href")

        return Chapter(
            index = chapter.index,
            chapterTitle = list[0].toString(),
            chapterUrl = chapterListUrl,
            bookId = bookId
        ).apply {
            nextChapterUrl = next
            prevChapterUrl = prev
            paragraphs = list
        }
    }

    fun parseChapter(index: Int, responseBody: ResponseBody): List<Paragraph> {
        return Jsoup.parse(responseBody.string())
            .parseParagraphs()
    }

    fun Document.parseParagraphs(): List<Paragraph> {
        val title = select("meta[name=title]")[0].attr("content")
        return select("#chapter-content :not(script):not(div)")
            .filter { it.childNodeSize() > 0 }
            .mapIndexed { index, element ->
                Paragraph(
                    index = index,
                    chapterId = 638,
                    text = element.text(),
                    isHeader = element.tagName() == "h4"
                )
            }

    }

    const val bookId = 666
}