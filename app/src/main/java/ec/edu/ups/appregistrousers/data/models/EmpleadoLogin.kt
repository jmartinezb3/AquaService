package ec.edu.ups.appregistrousers.data.models


import com.google.gson.annotations.SerializedName

data class EmpleadoLogin (
    @SerializedName("id")
    var id: Int?,

    @SerializedName("cedula")
    var cedula: String?,

    @SerializedName("clave")
    var clave: String?,

    @SerializedName("tipo_empleado")
    var tipo_empleado: Int?,
//
//    @SerializedName("login_success")
//    var login_success: Boolean?,
//
//    @SerializedName("usuario")
//    var usuario: String?,

)