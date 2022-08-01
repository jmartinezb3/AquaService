package ec.edu.ups.appregistrousers.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("lastname")
    var lastname: String,

    @SerializedName("email")
    var email: String,

)