package ec.edu.ups.appregistrousers.data.responses

import ec.edu.ups.appregistrousers.data.models.User
import java.util.*
import com.google.gson.annotations.SerializedName

data class DefaultResponse(
    //    val code: Int,
//    val error: Boolean,
//    val message: String

    val error: String,
    val message: String,
//    @SerializedName("body")
//    val body: List<Objects>
    val body: Object,
//var users: List<User>,

)