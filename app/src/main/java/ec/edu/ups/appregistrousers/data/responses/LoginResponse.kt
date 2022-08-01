package ec.edu.ups.appregistrousers.data.responses

import java.util.*
import ec.edu.ups.appregistrousers.data.models.EmpleadoLogin
import ec.edu.ups.appregistrousers.data.models.Login

data class LoginResponse(
    //    val code: Int,
//    val error: Boolean,
//    val message: String

    val error: String,
    val message: String,
//    @SerializedName("body")
    val body: Array<String>
//    val body: Object,
//var users: List<User>,

)