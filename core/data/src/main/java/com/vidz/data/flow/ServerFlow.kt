package com.vidz.data.flow

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.vidz.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.net.ConnectException

class ServerFlow<T, R>(
    private val getData: suspend () -> T,
    private val convert: (T) -> R,
) : IFlow<R> {

    private val moshi = Moshi.Builder().build()
    private val errorAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    )

    override fun execute(): Flow<Result<R>> {
        return flow {
            emit(Result.Init)
            try {
                val startTime = System.currentTimeMillis()
                val data = getData()
                val duration = System.currentTimeMillis() - startTime
                emit(Result.Success(convert(data)))
                Log.d("ServerFlow", "Data fetched successfully in ${duration}ms")
            } catch (connectException: ConnectException) {
                // Handle network connectivity issues
                emit(Result.ServerError.Internet("No internet connection. Please check your network and try again."))
            } catch (nullPointerException: NullPointerException) {
                // Handle cases where response.body() returns null (usually due to error responses)
                emit(Result.ServerError.General("Request failed: Invalid credentials or server error"))
            } catch (netWorkException: HttpException) {
                val errorMessage = extractErrorMessage(netWorkException)
                
                when (netWorkException.code()) {
                    401 -> emit(Result.ServerError.Token(errorMessage ?: "Token expired"))
                    400 -> emit(Result.ServerError.MissingParam(errorMessage ?: "Bad request"))
                    403 -> emit(Result.ServerError.RequiredLogin(errorMessage ?: "Login required"))
                    404 -> emit(Result.ServerError.RequiredVip(errorMessage ?: "Resource not found"))
                    402 -> emit(Result.ServerError.NotEnoughCredit(errorMessage ?: "Not enough credit"))
                    else -> emit(Result.ServerError.General(errorMessage ?: netWorkException.message()))
                }
            } catch (exception: Exception) {
                // Handle any other unexpected exceptions
                emit(Result.ServerError.General("Unexpected error: ${exception.message}"))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun extractErrorMessage(httpException: HttpException): String? {
        return try {
            val errorBody = httpException.response()?.errorBody()?.string()
            if (errorBody != null) {
                // Try to parse as JSON and extract error message using Moshi
                val errorMap = errorAdapter.fromJson(errorBody)
                
                // Common error message fields to check
                val errorMessage = errorMap?.get("error")?.toString()
                    ?: errorMap?.get("message")?.toString()
                    ?: errorMap?.get("detail")?.toString()
                    ?: errorMap?.get("details")?.toString()
                
                errorMessage
            } else {
                null
            }
        } catch (e: Exception) {
            // If JSON parsing fails, return null to use default error message
            Log.w("ServerFlow", "Failed to parse error response: ${e.message}")
            null
        }
    }
}
