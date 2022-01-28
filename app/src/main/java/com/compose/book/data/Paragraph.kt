package com.compose.book.data

data class Paragraph constructor(
    val index: Int,
    val text: String,
    val chapterId: Int,
    val isHeader: Boolean
)