package wikibook.learnandroid.quizquiz.database

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.Room

// (1)
@Dao
interface QuizDAO {
    // (2)
    @Insert
    fun insert(quiz: Quiz): Long
    @Update
    fun update(quiz: Quiz)
    @Delete
    fun delete(quiz: Quiz)

    // (3)
    @Query("SELECT * FROM quiz")
    fun getAll(): List<Quiz>

    // (1)
    @Query("SELECT DISTINCT category FROM quiz")
    fun getCategories(): List<String>

    // (2)
    @Query("SELECT * FROM quiz WHERE category = :category")
    fun getAll(category: String) : List<Quiz>
}

// (1)
@Database(entities=[Quiz::class], version=1)
@TypeConverters(StringListTypeConverter::class)
// (2)
abstract class QuizDatabase : RoomDatabase() {
    // (3)
    abstract fun quizDAO(): QuizDAO

    // (4)
    companion object {
        private var INSTANCE: QuizDatabase? = null

        fun getInstance(context: Context): QuizDatabase {
            if(INSTANCE == null) {
                // 최초에 메서드를 호출되는 시점에는 인스턴스가 생성되지 않았으므로 인스턴스를 생성
                INSTANCE = Room.databaseBuilder(context.applicationContext, QuizDatabase::class.java, "database.db").build()
            }
            return INSTANCE!!
        }
    }
}
