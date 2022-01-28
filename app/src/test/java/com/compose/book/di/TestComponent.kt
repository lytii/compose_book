package com.compose.book.di

import com.compose.book.TrashCountUnitTest
import com.compose.book.data.BookApi
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface TestComponent {
    fun api(): BookApi

    fun inject(test: TrashCountUnitTest)
}