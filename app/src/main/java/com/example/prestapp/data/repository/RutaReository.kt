package com.example.prestapp.data.repository

import com.example.prestapp.data.local.dao.RutaDao
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.data.remote.PrestAppApi
import com.example.prestapp.data.remote.dtos.RutaDto
import com.example.prestapp.data.remote.dtos.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RutaRepository @Inject constructor(
    private val rutaDao: RutaDao,
    private val prestAppApi: PrestAppApi
) {
    fun getRutas(): Flow<Resource<List<RutaEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val localRutas = rutaDao.getRutas().first()
            emit(Resource.Success(localRutas))
            val remoteRutas = prestAppApi.getRutas()
            rutaDao.deleteAllRutas()
            rutaDao.insertRutas(remoteRutas.map { it.toEntity() })
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun getRutaById(rutaId: Int): Flow<RutaEntity> {
        return rutaDao.getRutaById(rutaId)
    }


    suspend fun addRuta(rutaDto: RutaDto) {
        val remoteRuta = prestAppApi.postRuta(rutaDto)
        rutaDao.insertRuta(remoteRuta.toEntity())
    }

    suspend fun updateRuta(rutaDto: RutaDto) {
        prestAppApi.putRuta(rutaDto.rutaID, rutaDto)
        rutaDao.updateRuta(rutaDto.toEntity())
    }

    suspend fun deleteRuta(rutaId: Int) {
        prestAppApi.deleteRuta(rutaId)
        rutaDao.deleteRutaById(rutaId)
    }
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
