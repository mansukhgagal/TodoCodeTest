package com.codetest.todo.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

data class ErrorResponse(
    @SerializedName("error")
    var errorMessage: String?,
    @SerializedName("errorCode")
    var errorCode: Int = -1
) {
    companion object {
        fun getErrorData(responseBody: ResponseBody?): ErrorResponse? {
            responseBody?.let {
                val gson = Gson()
                val errorString = it.string()
                return gson.fromJson(errorString, ErrorResponse::class.java)
            }
            return null
        }
    }
}
