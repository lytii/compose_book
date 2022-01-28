package com.compose.book.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compose.book.data.Paragraph

@Composable
fun ChapterCompose(list: List<Paragraph>, onNext: () -> Unit) {
    LazyColumn {
        item { Navigation(onNext) }
        items(list) { s ->
            Column(modifier = Modifier.padding(all = 4.dp)) {
                Text(
                    text = s.text,
                    color = MaterialTheme.colors.primary,
                    style = if (s.isHeader) MaterialTheme.typography.h6 else LocalTextStyle.current,
                )
            }
        }
        item { Navigation(onNext) }
    }
}
