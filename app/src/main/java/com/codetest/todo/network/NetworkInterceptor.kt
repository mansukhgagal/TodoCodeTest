package com.codetest.todo.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NetworkInterceptor : Interceptor {
    /**
     * Interceptor class for setting of the headers for every request
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()


        val currentToken = "dsg"//get token from secure place

        //Add common headers
        val requestBuilder = request.newBuilder()
        addAuthHeader(requestBuilder, currentToken)

        request = requestBuilder.build() //overwrite old request
        val response = chain.proceed(request)  //perform request, here original request will be executed

        when (response.code) {
            401 -> { //if unauthorized
                synchronized(TAG) { //perform all 401 in sync blocks, to avoid multiply token updates

                }
            }
            411->{
                //logout
            }
            else -> {

            }
        }
        return response
    }

    private fun addAuthHeader(builder: Request.Builder, header: String) {
        builder.addHeader("authToken", header)
    }

    private fun regenerateToken(): Int {
        //
        return 200
    }

    companion object {
        private const val TAG = "NetworkInterceptor"
    }
}