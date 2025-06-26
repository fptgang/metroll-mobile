package com.vidz.data.flow

import android.util.Log
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
                if (netWorkException.code() == 401) {
                    emit(Result.ServerError.Token("Token expired"))
                } else if (netWorkException.code() == 400) {
                    emit(Result.ServerError.MissingParam("Missing parameter"))
                } else if (netWorkException.code() == 403) {
                    emit(Result.ServerError.RequiredLogin("Login required"))
                } else if (netWorkException.code() == 404) {
                    emit(Result.ServerError.RequiredVip("VIP required"))
                } else if (netWorkException.code() == 402) {
                    emit(Result.ServerError.NotEnoughCredit("Not enough credit"))
                } else {
                    emit(Result.ServerError.General(netWorkException.message()))
                }
            } catch (exception: Exception) {
                // Handle any other unexpected exceptions
                emit(Result.ServerError.General("Unexpected error: ${exception.message}"))
            }
        }.flowOn(Dispatchers.IO)
    }
}
