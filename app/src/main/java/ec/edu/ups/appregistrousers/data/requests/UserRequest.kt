package ec.edu.ups.appregistrousers.data.requests

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("name")
    var name: String,

    @SerializedName("lastname")
    var lastname: String,

    @SerializedName("email")
    var email: String,
)