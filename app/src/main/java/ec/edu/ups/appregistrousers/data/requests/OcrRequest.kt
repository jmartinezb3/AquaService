package ec.edu.ups.appregistrousers.data.requests

import com.google.gson.annotations.SerializedName
import java.io.File


data class OcrRequest(
//    @SerializedName("id_tarea")
//    var idTarea: Int,

    @SerializedName("files[]")
    var imagen: File, // File, String

)