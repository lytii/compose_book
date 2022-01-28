package com.compose.book.data

import android.content.Context
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Dao
interface BookDao {
    @Insert(onConflict = REPLACE)
    fun saveChapterList(list: List<Chapter>)

    @Query("SELECT * FROM Chapter where (:id) == bookId")
    fun getChapterList(id: Int): List<Chapter>

}


@Database(entities = [Chapter::class], version = 1)
abstract class BookRoomDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}

@Singleton
class BookDB @Inject constructor(context: Context) {
    private val db = Room.databaseBuilder(context, BookRoomDB::class.java, "books")
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.bookDao()
    fun saveChapterList(list: List<Chapter>) {
        dao.saveChapterList(list)
    }
    fun getChapterList(bookId: Int): Maybe<List<Chapter>> {
        return Maybe.fromCallable {
            dao.getChapterList(bookId).sortedBy { it.index }
        }
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
    }
}
