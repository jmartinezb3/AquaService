package ec.edu.ups.appregistrousers.data.models

import com.google.gson.annotations.SerializedName

data class Login (

    @SerializedName("login_success")
    var login_success: Boolean,

    @SerializedName("usuario")
    var usuario: String,

)


