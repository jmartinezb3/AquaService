package ec.edu.ups.appregistrousers.data.models

import com.google.gson.annotations.SerializedName


data class Tarea(
    @SerializedName("IdTarea")
    var idTarea: Int,

    @SerializedName("Cod_Vivienda")
    var cod_vivienda: Int,

    @SerializedName("Direccion")
    var direccion: String,

    @SerializedName("Manzana")
    var manzana: String,

    @SerializedName("Villa")
    var villa: String,

    @SerializedName("Cod_Medidor")
    var cod_medidor: String?,

    // Preguntar bien el tipo de dato que debe tener
    @SerializedName("GPS")
    var gps: String?,

    // Preguntar bien el tipo de dato que debe tener
    @SerializedName("FechaSubida")
    var fechaSubida: String?,

    @SerializedName("TareaEstado")
    var estado: Int,

    @SerializedName("IdEmpleado")
    var idEmpleado: Int,

    @SerializedName("Cedula")
    var cedula: String,

    @SerializedName("Nombre")
    var nombre: String,



)