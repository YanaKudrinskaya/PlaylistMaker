package com.yanakudrinskaya.playlistmaker.search.data

import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksResponse
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksSearchRequest
import com.yanakudrinskaya.playlistmaker.search.data.mapper.DtoToTrackMapper
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) :
    TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.status) {
            ResponseStatus.NO_INTERNET -> emit(Resource.Error("Проверьте подключение к интернету"))
            ResponseStatus.SUCCESS -> {
                emit(Resource.Success((response as TracksResponse).results.map{ dto -> DtoToTrackMapper.map(dto)}))
            }
            ResponseStatus.BAD_REQUEST -> emit(Resource.Error("Неверный запрос"))
            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }
}
