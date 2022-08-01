package ec.edu.ups.appregistrousers.data.models

import com.google.gson.annotations.SerializedName


data class ImageModel(

    @SerializedName("error")
    var error: Boolean,

    @SerializedName("path")
    var path: String,

    @SerializedName("msg_error")
    var msg_error: String,


    )