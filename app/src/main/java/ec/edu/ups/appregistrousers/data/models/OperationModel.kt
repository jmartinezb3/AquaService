package ec.edu.ups.appregistrousers.data.models

import com.google.gson.annotations.SerializedName

data class OperationModel (

    @SerializedName("MsgOperacion")
    var message: String, // Boolean

    @SerializedName("IdTarea")
    var id_tarea: Int,
)