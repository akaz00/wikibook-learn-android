package wikibook.learnandroid.quizquiz.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

// 문자열 리스트 변환 클래스 정의
class StringListTypeConverter {
    // (1)
    @TypeConverter
    fun stringListToString(stringList: List<String>?): String? {
        return stringList?.joinToString(",")
    }

    // (2)
    @TypeConverter
    fun stringToStringList(string: String?): List<String>? {
        return string?.split(",")?.toList()
    }
}

@Entity(tableName = "quiz")
data class Quiz(
    var type: String?, var question: String?, var answer: String?, var category: String?,
    // (2)
    @TypeConverters(StringListTypeConverter::class)
    var guesses: List<String>? = null,
    // (3)
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(question)
        parcel.writeString(answer)
        parcel.writeString(category)
        parcel.writeStringList(guesses)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quiz> {
        override fun createFromParcel(parcel: Parcel): Quiz {
            return Quiz(parcel)
        }

        override fun newArray(size: Int): Array<Quiz?> {
            return arrayOfNulls(size)
        }
    }
}
