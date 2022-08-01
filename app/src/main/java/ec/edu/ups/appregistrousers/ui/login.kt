package ec.edu.ups.appregistrousers.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.method.KeyListener
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import ec.edu.ups.appregistrousers.R
import ec.edu.ups.appregistrousers.data.models.EmpleadoLogin
import ec.edu.ups.appregistrousers.data.responses.LoginResponse
import ec.edu.ups.appregistrousers.data.services.ApiClient
import ec.edu.ups.appregistrousers.util.MyMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class login : AppCompatActivity() {

    lateinit var apiClient: ApiClient
    lateinit var txtcedula: EditText
    lateinit var txtClave: EditText
    lateinit var btn_ingresar: Button

    // GPS
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    // on below line we are creating
    // a variable for shared preferences.
    lateinit var sharedPreferences: SharedPreferences

    // on below line we are creating a variable
    // for prefs key and email key and pwd key.
    var PREFS_KEY = "prefs_key"
    var CEDULA_KEY = "Cedula_key"

    var valCedula = "ced"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        apiClient = ApiClient()
        // Variables del anterior
//        txtcedula = findViewById(R.id.txt_login_usuario)
//        txtClave = findViewById(R.id.txt_login_clave)
//        btn_ingresar = findViewById(R.id.btn_ingresar)

        txtcedula = findViewById(R.id.txt_put_login_cedula)
        txtClave = findViewById(R.id.txt_put_login_clave)
        btn_ingresar = findViewById(R.id.btn_login_ingresar)
        btn_ingresar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getLogin()
            }
        }

        // on below line we are initializing our shared preferences.
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

        // on below line we are getting data from
        // our shared prefs and setting it to email.
        valCedula = sharedPreferences.getString(CEDULA_KEY, "").toString()


        // Obtiene los datos del GPS después de haber tomado la foto
        if (allPermisionsGrantedGPS()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            leer_ubicacion_actual()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }

        // Si no tiene permiso de usar la cámara para tomar fotos...
        if (!checkCameraPermission()) {
            // Fuente: https://stackoverflow.com/questions/43042725/revoked-permission-android-permission-camera
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivity(intent)

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    MediaStore.ACTION_IMAGE_CAPTURE,
                ),
                PERMISSION_ID
            )
        }


    }

    private suspend fun getLogin() {
        val cedula = txtcedula.text.toString().trim()
        val clave = txtClave.getText().toString().trim()
        val tipo_empleado = 0


        withContext(Dispatchers.Main) {
            setEnableStatusLoginElements(false)
        }


        if (cedula.isEmpty()) {
            withContext(Dispatchers.Main) {
                txtcedula.error = "Ingrese su cédula"
                txtcedula.requestFocus()
            }
            return
        }
        if (clave.isEmpty()) {
            withContext(Dispatchers.Main) {
                txtClave.error = "Ingrese su clave"
                txtClave.requestFocus()
            }
            return
        }


        //Utilizar el Apirest
        apiClient.getApiService(this)
            .login_sql(EmpleadoLogin(1, cedula, clave, tipo_empleado))
//            .login_sql(EmpleadoLogin(1, cedula, clave, tipo_empleado,
//                null, null))
//            .enqueue(object : Callback<DefaultResponse> {
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
//                    call: Call<DefaultResponse>,
//                    response: Response<DefaultResponse>
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    //Log.i("etiqueta","Aqui me encuentro")
                    val resp = response
                    val defaultResponse = response.body()
                    var response_body = defaultResponse?.body
                    val login_success = response_body?.get(0).toBoolean()
//
//                    if (response_body != null) {
//                        for (prop in response_body::class.members) {
//                            println("${prop.name}")
//                        }
////                        Log.i("response_body[]", response_body["d"].toString())
//
//                    }

//                    println(resp)
                    Log.i("defaultResponse", defaultResponse.toString())
                    Log.i("response_body?.get(0)", response_body?.get(0).toBoolean().toString())

                    if (defaultResponse != null) {
                        if (response.code() == 200 && login_success) {
//                        if (response.code() == 200 && defaultResponse.error == false) {
                            val message = defaultResponse.message
//                            val message = defaultResponse.message+ "\n" + "" +
//                                    "Login success?: " + login_success
                            Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()


                            // on below line we are creating variable for editor
                            // of shared prefs and initializing it.
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()

                            // on below line we are adding our email and
                            // pwd to shared prefs to save them.
                            editor.putString(CEDULA_KEY, cedula)

                            // on below line we are applying
                            // changes to our shared prefs.
                            editor.apply()

                            setEnableStatusLoginElements(true)


                            // Fuente: https://stackoverflow.com/questions/65815020/kotlin-intent-none-of-the-following-functions-can-be-called-with-the-arguments
                            val intent = Intent(this@login, MainActivity::class.java)
                            intent.putExtra("cedula",cedula)
                            intent.putExtra("tipo_empleado",tipo_empleado)
                            startActivity(intent)
                            return
                        }
//                        MyMessage.toast(applicationContext, defaultResponse.message +
//                                " - Usuario o contraseña incorrectos")
                        Toast.makeText(applicationContext,defaultResponse.message +
                                " - Usuario o contraseña incorrectos",Toast.LENGTH_LONG).show()

                        setEnableStatusLoginElements(true)

                        return
                    }
                    MyMessage.toast(applicationContext, "Error al procesar")
                }
//                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    MyMessage.toast(applicationContext, t.toString())

                    setEnableStatusLoginElements(true)

                }
            });//End enqueue
    }

    fun setEnableStatusLoginElements(activado: Boolean) {
        // Fuente: https://stackoverflow.com/questions/9470171/edittext-non-editable
        txtcedula.setEnabled(activado);
        txtClave.setEnabled(activado);

        // Fuente: https://stackoverflow.com/questions/55645273/how-to-disable-a-button-in-kotlin
        btn_ingresar.isEnabled = activado
        btn_ingresar.isClickable = activado
    }

    // region leer_ubicacion_actual

    private fun leer_ubicacion_actual() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
//                            gps_coordenates = "Latitud: " + location.latitude.toString() + " " +  "Longitud: "  + location.longitude.toString()
                            var gps_coordenates = "lat " + location.latitude.toString() +  "lon "  + location.longitude.toString()
                            Log.d("gps_coordenates:", gps_coordenates)

                            //binding.lblLongitud.text = location.longitude.toString()
                        }
                    }
            } else {
                Toast.makeText(this, "Activar ubicación", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                this.finish()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }
    }

    // endregion

    // region Permisos

    // region GPS: Metodo checkPermissions

    private fun checkPermissions():Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // endregion

    // region Camera: Método checkCameraPermission

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, MediaStore.ACTION_IMAGE_CAPTURE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            false
        } else true
    }

    // endregion


    // endregion


    // region GPS: Método isLocationEnabled

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) }

    // endregion

    // region allPermisionsGrantedGPS

    private fun allPermisionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS_GPS= arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // endregion


    // region requestNewLocationData

    private fun requestNewLocationData() {
        var mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
            mLocationCallBack, Looper.myLooper())
    }

    private val mLocationCallBack = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation : Location = locationResult.lastLocation
//            binding.lblLatitud.text = "Latitud: " + mLastLocation.latitude.toString() + " " + "Longitud: " + mLastLocation.longitude.toString()
//            lblLatitud.text = "Latitud: " + mLastLocation.latitude.toString() + " " + "Longitud: " + mLastLocation.longitude.toString()
            //.lblLongitud.text = mLastLocation.longitude.toString()
        }
    }

    // endregion





}