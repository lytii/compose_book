package com.compose.book

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.book.data.BookApi
import com.compose.book.data.BookDB
import com.compose.book.data.Paragraph
import com.compose.book.data.TrashFamily
import com.compose.book.di.DaggerActivityComponent
import com.compose.book.ui.composable.ChapterCompose
import com.compose.book.ui.composable.Navigation
import com.compose.book.ui.theme.ComposeBookTheme
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val DEFAULT_INDEX = 88
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var db: BookDB

    @Inject
    lateinit var api: BookApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
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
        val currentIndex = getPreferences(MODE_PRIVATE).getInt(INDEX, DEFAULT_INDEX)
        Timber.d("networkParagraphs: $currentIndex")
        setChapter(currentIndex)
    }

    fun onNextClicked() {
        Timber.d("onNextClicked: ")
        val currentIndex = getPreferences(MODE_PRIVATE).getInt(INDEX, DEFAULT_INDEX) + 1
        getPreferences(MODE_PRIVATE).edit().putInt(INDEX, currentIndex).apply()
        setChapter(currentIndex)
    }

    @SuppressLint("CheckResult")
    fun setChapter(currentIndex: Int) {
        Timber.d("setChapter: $currentIndex")
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
                setContent { MyApp(p) { onNextClicked() } }
            },
                { e -> Timber.e(e, "networkParagraphs") })
    }
}

@Composable
fun MyApp(p: List<Paragraph>, onNextClicked: () -> Unit) {
    ComposeBookTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            val scrollState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            Content(p, scrollState) {
                coroutineScope.launch { scrollState.scrollToItem(0) }
                onNextClicked()
            }
        }
    }
}

@Composable
fun Content(list: List<Paragraph>, scrollState: LazyListState, onNextClicked: () -> Unit) {
    LazyColumn(state = scrollState) {
        item { Navigation(onNextClicked) }
        items(list) { s ->
            Column(modifier = Modifier.padding(all = 4.dp)) {
                Text(
                    text = s.text,
                    color = MaterialTheme.colors.primary,
                    style = if (s.isHeader) MaterialTheme.typography.h6 else LocalTextStyle.current,
                )
            }
        }
        item { Navigation(onNextClicked) }
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

val LazyListState.isScrolledToEnd get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
