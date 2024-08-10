package com.example.prestapp.data.remote

import com.example.prestapp.data.remote.dtos.*
import retrofit2.Response
import retrofit2.http.*

interface PrestAppApi {

    // Cliente Endpoints
    @GET("api/Clientes")
    suspend fun getClientes(): Response<List<ClienteDto>>

    @GET("api/Clientes/{id}")
    suspend fun getCliente(@Path("id") id: Int): Response<ClienteDto>

    @POST("api/Clientes")
    suspend fun postCliente(@Body clienteDto: ClienteDto): Response<ClienteDto>

    @PUT("api/Clientes/{id}")
    suspend fun putCliente(@Path("id") id: Int, @Body clienteDto: ClienteDto): Response<ClienteDto>

    @DELETE("api/Clientes/{id}")
    suspend fun deleteCliente(@Path("id") id: Int): Response<Unit>

    @GET("api/Clientes/cedula/{cedula}")
    suspend fun getClienteByCedula(@Path("cedula") cedula: String): Response<ClienteDto>

    // Prestamo Endpoints
    @GET("api/Prestamos")
    suspend fun getPrestamos(): Response<List<PrestamoDto>>

    @GET("api/Prestamos/{id}")
    suspend fun getPrestamo(@Path("id") id: Int): Response<PrestamoDto>

    @POST("api/Prestamos")
    suspend fun postPrestamo(@Body prestamo: PrestamoDto): Response<PrestamoDto>

    @PUT("api/Prestamos/{id}")
    suspend fun putPrestamo(@Path("id") id: Int, @Body prestamo: PrestamoDto): Response<PrestamoDto>

    @DELETE("api/Prestamos/{id}")
    suspend fun deletePrestamo(@Path("id") id: Int): Response<Unit>

    // Pago Endpoints
    @GET("api/Pagos")
    suspend fun getPagos(): Response<List<PagoDto>>

    @GET("api/Pagos/{id}")
    suspend fun getPago(@Path("id") id: Int): Response<PagoDto>

    @POST("api/Pagos")
    suspend fun postPago(@Body pago: PagoDto): Response<PagoDto>

    @PUT("api/Pagos/{id}")
    suspend fun putPago(@Path("id") id: Int, @Body pago: PagoDto): Response<PagoDto>

    @DELETE("api/Pagos/{id}")
    suspend fun deletePago(@Path("id") id: Int): Response<Unit>

    // HistorialCobros Endpoints
    @GET("api/HistorialCobros")
    suspend fun getHistorialCobros(): List<HistorialCobrosDto>

    @GET("api/HistorialCobros/{id}")
    suspend fun getHistorialCobro(@Path("id") id: Int): HistorialCobrosDto

    @POST("api/HistorialCobros")
    suspend fun postHistorialCobro(@Body historialCobros: HistorialCobrosDto): HistorialCobrosDto

    @PUT("api/HistorialCobros/{id}")
    suspend fun putHistorialCobro(@Path("id") id: Int, @Body historialCobros: HistorialCobrosDto): HistorialCobrosDto

    @DELETE("api/HistorialCobros/{id}")
    suspend fun deleteHistorialCobro(@Path("id") id: Int): Response<Unit>

    // User Endpoints
    @POST("api/Users/register")
    suspend fun registerUser(@Body user: UserDto): AuthResponseDto

    @POST("api/Users/login")
    suspend fun loginUser(@Body login: LoginDto): AuthResponseDto

    // Ruta Endpoints
    @GET("api/Rutas")
    suspend fun getRutas(): Response<List<RutaDto>>

    @GET("api/Rutas/{id}")
    suspend fun getRuta(@Path("id") id: Int): RutaDto

    @POST("api/Rutas")
    suspend fun postRuta(@Body rutaDto: RutaDto): Response<RutaDto>

    @PUT("api/Rutas/{id}")
    suspend fun putRuta(@Path("id") id: Int, @Body rutaDto: RutaDto): Response<RutaDto>

    @DELETE("api/Rutas/{id}")
    suspend fun deleteRuta(@Path("id") id: Int): Response<Unit>
}
