package com.codetest.todo.ui.create

import android.os.Parcelable
import android.provider.SyncStateContract
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codetest.todo.utils.Constants
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "todo")
data class TodoModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @SerializedName("title")
    var title:String,
    @SerializedName("description")
    var description:String?,
    @SerializedName("time")
    var time:String,
    @SerializedName("date")
    var date:Long?,
    @SerializedName("type")
    var type:Int?,
    @SerializedName("status")
    var status:Int = 1
) : Parcelable {
    companion object {
        fun isValidInput(todo:TodoModel?) : Boolean {
            todo ?: return false
            todo.apply {
                if(title.isEmpty()) return false
                if(time.isEmpty()) return false
                if(type != null && type != Constants.TYPE_DAILY && type != Constants.TYPE_WEEKLY) return false
            }
            return true
        }
        fun invalidTitle(title:String?) : Boolean = title.isNullOrEmpty()
        fun invalidType(type:Int?) : Boolean {
            if(type != null && type != Constants.TYPE_DAILY && type != Constants.TYPE_WEEKLY) return true
            return false
        }
        fun invalidTime(time:String?) : Boolean {
            return time.isNullOrEmpty()
        }
        fun invalidDate(date:Long?) : Boolean {
            return date==null
        }
    }
}