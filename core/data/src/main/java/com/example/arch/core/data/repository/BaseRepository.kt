package com.example.arch.core.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.network.model.ApiResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Result<T> =
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data
                if (body?.success == true && data != null) {
                    Result.Success(data)
                } else {
                    Result.Error(Exception(body?.message ?: "Unknown error"))
                }
            } else {
                Result.Error(HttpException(response), "HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            Result.Error(e, "Network error. Please check your connection.")
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
}
