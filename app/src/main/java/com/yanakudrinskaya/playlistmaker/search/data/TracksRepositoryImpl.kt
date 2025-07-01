package com.yanakudrinskaya.playlistmaker.search.data

import android.content.Context
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksResponse
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksSearchRequest
import com.yanakudrinskaya.playlistmaker.search.data.mapper.DtoToTrackMapper
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context
) :
    TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.status) {
            ResponseStatus.NO_INTERNET -> emit(Resource.Error(context.getString(R.string.no_internet)))
            ResponseStatus.SUCCESS -> {
                emit(Resource.Success((response as TracksResponse).results.map { dto ->
                    DtoToTrackMapper.map(
                        dto
                    )
                }))
            }

            ResponseStatus.BAD_REQUEST -> emit(Resource.Error(context.getString(R.string.bad_request)))
            else -> emit(Resource.Error(context.getString(R.string.server_error)))
        }
    }
}
