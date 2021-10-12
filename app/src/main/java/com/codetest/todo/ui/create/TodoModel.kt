package com.codetest.todo.ui.create

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var type:Int?
) : Parcelable