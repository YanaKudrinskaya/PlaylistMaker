package com.yanakudrinskaya.playlistmaker.search.data

import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksResponse
import com.yanakudrinskaya.playlistmaker.search.data.dto.TracksSearchRequest
import com.yanakudrinskaya.playlistmaker.search.data.mapper.DtoToTrackMapper
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) :
    TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.status) {
            ResponseStatus.NO_INTERNET -> Resource.Error("Проверьте подключение к интернету")
            ResponseStatus.SUCCESS -> {
                Resource.Success((response as TracksResponse).results.map{ dto -> DtoToTrackMapper.map(dto)})
            }
            ResponseStatus.BAD_REQUEST -> Resource.Error("Неверный запрос")
            else -> Resource.Error("Ошибка сервера")
        }
    }
}
