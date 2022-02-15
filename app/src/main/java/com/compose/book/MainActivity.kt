package com.compose.book

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.compose.book.data.BookApi
import com.compose.book.data.BookDB
import com.compose.book.data.Paragraph
import com.compose.book.data.TrashFamily
import com.compose.book.di.DaggerActivityComponent
import com.compose.book.ui.composable.ChapterCompose
import com.compose.book.ui.theme.ComposeBookTheme
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var db: BookDB

    @Inject
    lateinit var api: BookApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = DaggerActivityComponent.builder()
            .bindContext(this)
            .build()
        component.inject(this)
        networkParagraphs()
    }

    @SuppressLint("CheckResult")
    fun networkParagraphs() {
//        getPreferences(MODE_PRIVATE).edit().putInt(INDEX, 714).apply()
        // get current chapter index
        val currentIndex = getPreferences(MODE_PRIVATE).getInt(INDEX, 33)
        Log.d("main","networkParagraphs: $currentIndex")
        setChapter(currentIndex)
    }

    fun onNextClicked() {
        Log.d("main","onNextClicked: ")
        val currentIndex = getPreferences(MODE_PRIVATE).getInt(INDEX, 33) + 1
        getPreferences(MODE_PRIVATE).edit().putInt(INDEX, currentIndex).apply()
        setChapter(currentIndex)
    }

    @SuppressLint("CheckResult")
    fun setChapter(currentIndex: Int) {
        Log.d("main","setChapter: $currentIndex")
        // get list of chapters
        val getList = db
            .getChapterList(TrashFamily.bookId)
            .filter { it.isNotEmpty() }
            .switchIfEmpty(
                api.getChapter(TrashFamily.chapterListUrl)
                    .map { TrashFamily.parseChapterListUrls(it) }
                    .doAfterSuccess { db.saveChapterList(it) }
            )

        // display current chapter
        getList
            .map { it[currentIndex] }
            .flatMap { api.getChapter(it.chapterUrl) }
            .map { TrashFamily.parseChapter(currentIndex, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ p ->
                setContent {
                    ComposeBookTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(color = MaterialTheme.colors.background) {
                            val scrollState = rememberLazyListState()
                            val coroutineScope = rememberCoroutineScope()
                            coroutineScope.launch { scrollState.scrollToItem(0) }
                            ChapterCompose(list = p, scrollState) { onNextClicked() }
                        }
                    }
                }
            },
                { e -> Timber.e(e, "networkParagraphs") })
    }
}

private const val INDEX = "INDEX"

var i = 0
val paragraphs = listOf(
    "1234567890".toParagraph(i++),
    "asdfjkl".toParagraph(i++),
    "abcdefghijk".toParagraph(i++),
    "a1a1a2a3".toParagraph(i++)
)

fun String.toParagraph(index: Int) = Paragraph(
    text = this,
    index = index,
    isHeader = index == 0,
    chapterId = index
)

@Preview(
    name = "light mode",
    showBackground = true
)
@Preview(
    name = "dark mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun DefaultPreview() {
    ComposeBookTheme {
        Column {
            ChapterCompose(list = paragraphs, rememberLazyListState(), {})
        }
    }
}