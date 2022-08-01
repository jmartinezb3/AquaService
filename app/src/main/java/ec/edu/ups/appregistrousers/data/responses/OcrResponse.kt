package ec.edu.ups.appregistrousers.data.responses

import com.google.gson.annotations.SerializedName
import ec.edu.ups.appregistrousers.data.models.ImageModel

data class OcrResponse(

//    @SerializedName("body")
//    val results: String
//    val results: Array<String>
//    val results: Object,

//    @SerializedName("error")
//    var error: Boolean,
//
//    @SerializedName("path")
//    var path: String,
//
//    @SerializedName("msg_error")
//    var msg_error: String,

    /*
    * Desde el server viene este Json
    {
        "error": "",
        "body": {
            "path": "uploads/Medidor de agua - WhatsApp Image 2022-07-25 at 5.40.10 PM (2).jpeg",
            "msg_error": "",
            "error": false
        },
        "message": "Operación realizada con éxito"
    }
    * */

    @SerializedName("error")
    var error: String, // Boolean

    @SerializedName("message")
    var message: String,

    @SerializedName("body")
//    var image_data: List<ImageModel>,
//    var image_data: Array<ImageModel>,
    var image_data: ImageModel,

    )