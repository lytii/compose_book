package com.compose.book.data

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface BookApi {
    @GET
    fun getChapter(@Url chapterUrl: String): Single<ResponseBody>

    @GET
    fun getChapterList(@Url chapterListUrl: String): Single<ResponseBody>
}