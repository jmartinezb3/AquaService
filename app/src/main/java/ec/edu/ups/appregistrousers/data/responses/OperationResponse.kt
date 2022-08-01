package ec.edu.ups.appregistrousers.data.responses

import com.google.gson.annotations.SerializedName
import ec.edu.ups.appregistrousers.data.models.OperationModel
import ec.edu.ups.appregistrousers.data.models.Tarea


data class OperationResponse(
//    @SerializedName("code")
//    var code: Int,

    @SerializedName("error")
    var error: String, // Boolean

    @SerializedName("message")
    var message: String,

    @SerializedName("body")
    var operation: List<OperationModel>,
)