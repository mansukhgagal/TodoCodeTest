package com.codetest.todo.ui.login

import com.codetest.todo.data.ApiResponse
import com.codetest.todo.network.ErrorResponse
import com.codetest.todo.network.WebServices
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class LoginRepository @Inject constructor(private val api: WebServices) {

    @Throws(Exception::class)
    suspend fun doLogin(userData:UserData): ApiResponse {
        return try {
            val response = api.doLogin(userData)
            if (response.isSuccessful) {
                val userDataResponse: UserData? = response.body()
                ApiResponse(userDataResponse, null)
            } else {
                val error = ErrorResponse.getErrorData(response.errorBody())
                error?.errorCode = response.code()
                ApiResponse(null, error)
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }
}