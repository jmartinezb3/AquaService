package ec.edu.ups.appregistrousers.data.requests

import com.google.gson.annotations.SerializedName


data class TareaRequest(
    /**
     * .input('id_tarea', tarea.id_tarea) // 0: INSERTAR, 1: ACTUALIZAR
    .input('cedula', tarea.cedula)
    // Fuente Buffer.from(tarea.imagen, 'binary'): https://stackoverflow.com/questions/34383938/how-to-insert-binary-data-into-sql-server-using-node-mssql
    .input('imagen', sql.VarBinary(sql.MAX), Buffer.from(tarea.imagen, 'binary')) //  VarBinary(Max)
    // En web: image/jpg, image/png, image/jpeg
    // En Android: ???
    .input('imagen_tipo', tarea.imagen_tipo ? tarea.imagen_tipo : "")
    .input('cod_medidor', tarea.cod_medidor)
    .input('gps', tarea.gps)
    .input('estado', parseInt(tarea.estado))
     */
    @SerializedName("id_tarea")
    var idTarea: Int,

    @SerializedName("parametro_busqueda")
    var cedula: String,

    @SerializedName("path")
    var Imagen_path: String,

    @SerializedName("imagen_tipo")
    var extension: String,

    // Preguntar bien el tipo de dato que debe tener
    @SerializedName("cod_medidor")
    var cod_medidor: String?,

    @SerializedName("gps")
    var gps: String?,

    @SerializedName("tipo_empleado")
    var tipo_empleado: Int,

    // El signo ? es para decir que esta variable puede ser null
    // o sea, que a veces no se usará y se pondrá null como valor
    @SerializedName("estado")
    var estado: Int?,
)