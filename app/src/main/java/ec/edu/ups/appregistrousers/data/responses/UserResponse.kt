package ec.edu.ups.appregistrousers.data.responses

import com.google.gson.annotations.SerializedName
import ec.edu.ups.appregistrousers.data.models.User

data class UserResponse(
    @SerializedName("code")
    var code: Int,

    @SerializedName("error")
    var error: Boolean,

    @SerializedName("message")
    var message: String,

    @SerializedName("users")
    var users: List<User>,
)