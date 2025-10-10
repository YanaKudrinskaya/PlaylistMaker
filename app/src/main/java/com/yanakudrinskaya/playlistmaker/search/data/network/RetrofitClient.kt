package com.yanakudrinskaya.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.yanakudrinskaya.playlistmaker.search.data.NetworkClient
import com.yanakudrinskaya.playlistmaker.search.data.dto.Response
import com.yanakudrinskaya.playlistmaker.search.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitClient(
    private val iTunesService: iTunesApi,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
            if (!isConnected()) Response().apply { status = ResponseStatus.NO_INTERNET }

            if(dto !is TracksSearchRequest)
                return Response().apply { status = ResponseStatus.BAD_REQUEST }

            return withContext(Dispatchers.IO) {
                try {
                    val response = iTunesService.getTrackList(dto.expression)
                    response.apply { status = ResponseStatus.SUCCESS }
                } catch (e: Throwable) {
                    Response().apply { status = ResponseStatus.SERVER_ERROR }
                }
            }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
