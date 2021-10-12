package com.codetest.todo.data

import com.codetest.todo.network.ErrorResponse
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("data") var result: Any?,
    @SerializedName("error") var error: ErrorResponse?
)
