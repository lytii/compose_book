package com.compose.book.di

import android.content.Context
import com.compose.book.MainActivity
import com.compose.book.data.BookApi
import com.compose.book.data.BookDB
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class])
interface ActivityComponent {
    fun api(): BookApi
    fun db(): BookDB
    fun context(): Context

    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): ActivityComponent
    }
}
