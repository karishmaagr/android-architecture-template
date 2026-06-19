package com.example.arch.core.data.repository

import com.example.arch.core.common.result.ApiException
import com.example.arch.core.network.model.ApiResponse
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>,
    ): NetworkResult<T> = try {
        val response = apiCall()
        when {
            response.code() == 204 -> NetworkResult.NotModified
            response.isSuccessful -> {
                val body = response.body()
                val data = body?.data
                if (body?.success == true && data != null) {
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Failure(
                        ApiException.Unknown(response.code(), body?.message ?: "Unknown error"),
                    )
                }
            }
            else -> NetworkResult.Failure(parseHttpError(response))
        }
    } catch (e: IOException) {
        NetworkResult.Failure(ApiException.NetworkError(e.message ?: "Network error. Please check your connection."))
    } catch (e: Exception) {
        NetworkResult.Failure(ApiException.Unknown(-1, e.message ?: "Unexpected error"))
    }

    private fun parseHttpError(response: Response<*>): ApiException {
        val rawError = try { response.errorBody()?.string() } catch (_: Exception) { null }
        val errorMessage = extractMessage(rawError) ?: response.message() ?: "HTTP ${response.code()} error"
        return when (response.code()) {
            400  -> ApiException.BadRequest(errorMessage)
            401  -> ApiException.Unauthorized(errorMessage)
            403  -> ApiException.Forbidden(errorMessage)
            404  -> ApiException.NotFound(errorMessage)
            409  -> ApiException.Conflict(errorMessage)
            422  -> ApiException.ValidationError(errorMessage, extractFieldErrors(rawError))
            429  -> ApiException.TooManyRequests(errorMessage)
            in 500..599 -> ApiException.ServerError(errorMessage)
            else -> ApiException.Unknown(response.code(), errorMessage)
        }
    }

    private fun extractMessage(errorBody: String?): String? = try {
        errorBody?.let { JSONObject(it).optString("message").takeIf { msg -> msg.isNotBlank() } }
    } catch (_: Exception) { null }

    private fun extractFieldErrors(errorBody: String?): Map<String, List<String>> {
        if (errorBody == null) return emptyMap()
        return try {
            val errorsJson = JSONObject(errorBody).optJSONObject("errors") ?: return emptyMap()
            buildMap {
                for (key in errorsJson.keys()) {
                    val arr = errorsJson.optJSONArray(key) ?: continue
                    put(key, (0 until arr.length()).map { arr.getString(it) })
                }
            }
        } catch (_: Exception) { emptyMap() }
    }
}