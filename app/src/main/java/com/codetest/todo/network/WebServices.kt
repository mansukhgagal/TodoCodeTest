package com.codetest.todo.network

import com.codetest.todo.data.ApiResponse
import com.codetest.todo.ui.login.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@JvmSuppressWildcards
interface WebServices {
    @POST("login")
    suspend fun doLogin(@Body body: UserData): Response<UserData>
}