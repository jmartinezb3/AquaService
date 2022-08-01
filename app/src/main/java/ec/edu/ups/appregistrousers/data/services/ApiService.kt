package ec.edu.ups.appregistrousers.data.services

import ec.edu.ups.appregistrousers.data.models.EmpleadoLogin
import ec.edu.ups.appregistrousers.data.models.Tarea
import ec.edu.ups.appregistrousers.data.requests.OcrRequest
import ec.edu.ups.appregistrousers.data.requests.TareaRequest
import ec.edu.ups.appregistrousers.data.requests.UserRequest
import ec.edu.ups.appregistrousers.data.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*


interface ApiService {
    @GET("/")
    fun listUser(): Call<UserResponse>

    @POST("/create")
    fun addUser(@Body request: UserRequest): Call<DefaultResponse>

    @POST("/getClave")
    fun login(@Body request: UserRequest): Call<DefaultResponse>

    // Empleado
    @POST("/empleado/login")
//    fun login_sql(@Body request: EmpleadoLogin): Call<DefaultResponse>
    fun login_sql(@Body request: EmpleadoLogin): Call<LoginResponse>

    // Tarea
    @POST("/tarea/")
    fun get_tareas(@Body request: TareaRequest): Call<TareaResponse>

    @POST("/tarea/")
    fun get_tarea_por_id(@Body request: TareaRequest): Call<TareaResponse>

    @Multipart
    @POST("/upload_test")
//    fun ocr_processing(@Part MultipartBody.Part image): Call<OcrResponse>
    fun ocr_processing(
//        @Part("user_id") id: RequestBody?,
//        @Part("full_name") fullName: RequestBody?,
        @Part image: MultipartBody.Part?,
//        @Part("other") other: RequestBody?
//    ): Call<OcrResponse>
    ): Call<Array<String>>

    @Multipart
    @POST("/tarea/subir_imagen")
    fun subir_imagen(
//        @Part("full_name") fullName: RequestBody?,
        @Part image: MultipartBody.Part?,
//        @Part("other") other: RequestBody?
//    ): Call<OcrResponse>
    ): Call<OcrResponse>

    @PATCH("/tarea/actualizar")
    fun actualizar_tarea(@Body request: TareaRequest): Call<OperationResponse>
}