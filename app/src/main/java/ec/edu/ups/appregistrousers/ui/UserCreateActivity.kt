package ec.edu.ups.appregistrousers.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import ec.edu.ups.appregistrousers.R
import ec.edu.ups.appregistrousers.data.requests.TareaRequest
import ec.edu.ups.appregistrousers.data.responses.OcrResponse
import ec.edu.ups.appregistrousers.data.responses.OperationResponse
import ec.edu.ups.appregistrousers.data.responses.TareaResponse
import ec.edu.ups.appregistrousers.data.services.ApiClient
import ec.edu.ups.appregistrousers.util.MyMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class UserCreateActivity : AppCompatActivity() {
    lateinit var txtCodVivienda: EditText // txtApellido
    lateinit var txtDireccion: EditText
    lateinit var txtManzana: EditText
    lateinit var txtVilla: EditText

    lateinit var txtMedidor: EditText
    lateinit var txtGPS: EditText

    lateinit var btnAbrirCamara: ImageButton
    lateinit var btnRegistrarMedida: Button
    lateinit var apiClient: ApiClient

    private val CAMERA_REQUEST = 1888
    private val imageView: ImageView? = null
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val REQUEST_IMAGE_CAPTURE = 1

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var valIdTarea: Int = 0
    private var valCodVivienda: String = ""
    private var valDireccion: String = ""
    private var valManzana: String = ""
    private var valVilla: String = ""
    private var valCodMedidor: String = ""
    private var valGpsCoordenates: String = ""

    private lateinit var file: File

    // Variables de sesión
    // on below line we are creating a variable
    // for prefs key and email key and pwd key.
    var PREFS_KEY = "prefs_key"
    var PREFS_TAREA_KEY = "prefs_tarea_key"
    var CEDULA_KEY = "Cedula_key"
    var ID_TAREA_KEY = "IdTarea_Key"

    var COD_VIVIENDA_KEY = "CodVivienda_Key"
    var DIRECCION_KEY = "Direccion_Key"
    var MANZANA_KEY = "Manzana_Key"
    var VILLA_KEY = "Villa_Key"


    // on below line we are creating a variable for shared preferences.
    lateinit var sharedPreferences: SharedPreferences
    lateinit var tareaSharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ingresar_medicion)
        apiClient = ApiClient()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // on below line we are initializing our shared preferences variable.
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        tareaSharedPreferences = getSharedPreferences(PREFS_TAREA_KEY, Context.MODE_PRIVATE)

        txtCodVivienda = findViewById(R.id.txt_put_registromed_codvivienda)
        txtDireccion = findViewById(R.id.txt_put_registromed_direccion)
        txtManzana = findViewById(R.id.txt_put_registromed_manzana)
        txtVilla = findViewById(R.id.txt_put_registromed_villa)

        txtMedidor = findViewById(R.id.txt_put_registromed_lectura)
        txtGPS = findViewById(R.id.txt_put_registromed_coordenada)

        btnAbrirCamara = findViewById(R.id.btn_registromed_camara)
        btnRegistrarMedida = findViewById(R.id.btn_registromed_registrarmedida)

//        btnRegistrarMedida.setOnClickListener(View.OnClickListener() {
//            CoroutineScope(Dispatchers.IO).launch {
//                subirImagen()
//            }
//        })



        btnAbrirCamara.setOnClickListener(View.OnClickListener() {
            val intent = Intent(this, obtener_resultados_ocr::class.java)
            intent.putExtra("idTarea", valIdTarea)
            intent.putExtra("cod_vivienda", valCodVivienda)
            intent.putExtra("direccion", valDireccion)
            intent.putExtra("manzana", valManzana)
            intent.putExtra("villa", valVilla)

            startActivity(intent)

        })

        // Fuente: https://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android
        // You can be pretty confident that the intent will not be null here.
        val intent = intent

        // Get the extras (if there are any)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("GPS_lbl_coordenates")) {
                val gps = extras.getString("GPS_lbl_coordenates")
                txtGPS.setText(gps)
            }
        }

        validar_boton_registrar()

        validar_existencia_datos()

        if (validar_recargar_datos()) {
            val cargar_datos = extras?.getBoolean("cargar_datos")

            if (cargar_datos == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    cargarDatos()
                }
            }
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            cargarDatos()
//        }


    }

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                val value = it.data?.getStringExtra("input")
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
//            shpFoto.setImageBitmap(imageBitmap)

            val intent = Intent(this, obtener_resultados_ocr::class.java)
            intent.putExtra("imagen_bitmap", imageBitmap)
            startActivity(intent)

        }
    }

    // region Método: validar_recargar_datos

    private fun validar_recargar_datos(): Boolean {
        // Fuente: https://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android
        // You can be pretty confident that the intent will not be null here.
        val intent = intent

        // Get the extras (if there are any)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("cargar_datos")) {
//                val cargar_datos = extras.getBoolean("cargar_datos")
                return true
            }
        }

        return false
    }

    // endregion

    // region Método: validar_existencia_datos

    private fun validar_existencia_datos() {
        // Fuente: https://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android
        // You can be pretty confident that the intent will not be null here.
        val intent = intent

        // Get the extras (if there are any)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("idTarea")) {
                valIdTarea = intent.getIntExtra("idTarea", 0)
            } else {
                valIdTarea = sharedPreferences.getInt(ID_TAREA_KEY, 0)
            }
            if (extras.containsKey("cod_vivienda")) {
                valCodVivienda = intent.getStringExtra("cod_vivienda").toString()
                txtCodVivienda.setText(valCodVivienda)
            } else {
                valCodVivienda = tareaSharedPreferences.getInt(COD_VIVIENDA_KEY, 0).toString()
            }
            if (extras.containsKey("direccion")) {
                valDireccion = intent.getStringExtra("direccion").toString()
                txtDireccion.setText(valDireccion)
            } else {
                valDireccion = tareaSharedPreferences.getString(DIRECCION_KEY, "").toString()
            }
            if (extras.containsKey("manzana")) {
                valManzana = intent.getStringExtra("manzana").toString()
                txtManzana.setText(valManzana)
            } else {
                valManzana = tareaSharedPreferences.getString(MANZANA_KEY, "").toString()
            }
            if (extras.containsKey("villa")) {
                valVilla = intent.getStringExtra("villa").toString()
                txtVilla.setText(valVilla)
            } else {
                valVilla = tareaSharedPreferences.getString(VILLA_KEY, "").toString()
            }
            if (extras.containsKey("resultado_ocr")) {
                valCodMedidor = intent.getStringExtra("resultado_ocr").toString()
                txtMedidor.setText(valCodMedidor)
            }
            if (extras.containsKey("imagen")) {
                var path = intent.getStringExtra("imagen").toString()
                Log.d("path UserCreateActivity", path)
                file = File(path)
                Log.d("file UserCreateActivity", file.toString())

            }
            if (extras.containsKey("GPS_coordenates")) {
                valGpsCoordenates = intent.getStringExtra("GPS_coordenates").toString()
            }

        }

    }

    // endregion

    // region Método: validar_boton_registrar

    private fun validar_boton_registrar() {
        // Fuente: https://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android
        // You can be pretty confident that the intent will not be null here.
        val intent = intent

        // Get the extras (if there are any)
        val extras = intent.extras
        if (extras != null) {

            val idTarea : Int = sharedPreferences.getInt(ID_TAREA_KEY, 0)

            Log.d("idTarea de sesion en metodo validar_boton_registrar", idTarea.toString())

            if (extras.containsKey("permitir_boton") && idTarea > 0) {
                val activar = intent.getBooleanExtra("permitir_boton", false)

                Log.d("activar botón", activar.toString())

                // Fuente: https://stackoverflow.com/questions/55645273/how-to-disable-a-button-in-kotlin
                btnRegistrarMedida.isEnabled = activar
                btnRegistrarMedida.isClickable = activar

                btnRegistrarMedida.setOnClickListener(View.OnClickListener() {
                    CoroutineScope(Dispatchers.IO).launch {
                        subirImagen()
                    }
                })

            }

        }

    }

    // endregion

    private suspend fun cargarDatos() {

        // on below line we are getting the data from
        // email and setting it in email variable.
        valIdTarea = sharedPreferences.getInt(ID_TAREA_KEY, 0)

//        valIdTarea = intent.getIntExtra("idTarea", 0)
        val idTarea = valIdTarea
        val estado = intent.getIntExtra("estado", 0)

        Log.d("idTarea:", idTarea.toString())
        Log.d("estado:", estado.toString())

        // .listUser(): Ejecuta el método / de la BASE_URL que se encuentra en la clase Constants
//        apiClient.getApiService(this).listUser()
        apiClient.getApiService(this)
            .get_tarea_por_id(TareaRequest(idTarea, "cedula",
                "", "", "","", 0, null))
            .enqueue(object : Callback<TareaResponse> {
                override fun onResponse(
                    call: Call<TareaResponse>,
                    // Este objeto response es de tipo UserResponse
                    response: Response<TareaResponse>
                ) {
                    //Log.i("Proyecto",response.body().toString())
                    /*
                        En este punto, ya se consultaron los datos a la base
                        y se devuelve el cuerpo que devolvió la API de NodeJs.
                        En el cuerpo del response está la propiedad users,
                        que es donde está la lista de usuarios que devolvió NodeJs
                    */
                    val listaTareas = response.body()?.tasks
                    //Log.i("Proyecto",Gson().toJson(listaUsuario))
                    Log.i("Proyecto lista tareas",listaTareas.toString())
//                    Log.i("Proyecto Gson lista tareas", Gson().toJson(listaTareas))

                    if (listaTareas != null) {
                        valCodVivienda = listaTareas.get(0).cod_vivienda.toString()
                        txtCodVivienda.setText(valCodVivienda)
                        valDireccion = listaTareas.get(0).direccion.toString()
                        txtDireccion.setText(valDireccion)
                        valManzana = listaTareas.get(0).manzana.toString()
                        txtManzana.setText(valManzana)
                        valVilla = listaTareas.get(0).villa.toString()
                        txtVilla.setText(valVilla)

                        val estado = listaTareas.get(0).estado

                        // TODO Traer datos de Medidor y GPS a los textboxes
                        if(estado == 1) {
                            valCodMedidor = listaTareas.get(0).cod_medidor.toString()
                            txtMedidor.setText(valCodMedidor)
                            valGpsCoordenates = listaTareas.get(0).gps.toString()
                            txtGPS.setText(valGpsCoordenates)
                        }


                        // on below line we are creating variable for editor
                        // of shared prefs and initializing it.
                        val editor: SharedPreferences.Editor = tareaSharedPreferences.edit()

                        // on below line we are adding our email and
                        // pwd to shared prefs to save them.
                        editor.putInt(COD_VIVIENDA_KEY, Integer.parseInt(valCodVivienda))
                        editor.putString(DIRECCION_KEY, valDireccion)
                        editor.putString(MANZANA_KEY, valManzana)
                        editor.putString(VILLA_KEY, valVilla)

                        // on below line we are applying
                        // changes to our shared prefs.
                        editor.apply()

                        // Oculta botón guardar si la tarea está completa
                        if(estado == 1) {
                            btnAbrirCamara.setVisibility(View.GONE);
                            btnRegistrarMedida.setVisibility(View.GONE);
                        }

                    } else {
                        MyMessage.toast(applicationContext, "No existen registro")
                    }
                }

                override fun onFailure(call: Call<TareaResponse>, t: Throwable) {
                    MyMessage.toast(applicationContext, t.toString())
                    //Log.i("Proyecto",t.toString())
                }

            });
    }

    // region Actualizar Datos: subirImagen

    private suspend fun subirImagen() {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("imagen", file.name, requestFile)
        Log.i("body",body.toString())
        Log.i("file",file.toString())
        Log.i("file.name",file.name.toString())
        Log.i("file.extension",file.extension.toString())

        // .listUser(): Ejecuta el método / de la BASE_URL que se encuentra en la clase Constants
        apiClient.getApiService(this)
            .subir_imagen(body)
            .enqueue(object : Callback<OcrResponse> {
                override fun onResponse(
                    call: Call<OcrResponse>,
                    // Este objeto response es de tipo UserResponse
                    response: Response<OcrResponse>
                ) {
                    //Log.i("Proyecto",response.body().toString())
                    /*
                        En este punto, ya se consultaron los datos a la base
                        y se devuelve el cuerpo que devolvió la API de NodeJs.
                        En el cuerpo del response está la propiedad users,
                        que es donde está la lista de usuarios que devolvió NodeJs
                    */
                    val res_body = response.body()
                    val res_body_path = response.body()?.image_data
                    val existe_error = response.body()?.error.toString().toBoolean()
                    //Log.i("Proyecto",Gson().toJson(listaUsuario))
                    Log.i("res_body",res_body.toString())
                    Log.i("res_body_path",res_body_path.toString())
                    Log.i("existe_error",existe_error.toString())
//                    Log.i("Proyecto Gson lista tareas", Gson().toJson(listaTareas))

                    if (response.body() != null && !existe_error) {
                        // NodeJs image path
                        val path = res_body_path?.path.toString()
                        Log.i("path antes de enviar a completar tarea",path)

                        CoroutineScope(Dispatchers.IO).launch {
                            registrarMedicion(path, file.extension)
                        }

                    } else {
                        MyMessage.toast(applicationContext, "No existen registro")
                    }
                }

                override fun onFailure(call: Call<OcrResponse>, t: Throwable) {
                    MyMessage.toast(applicationContext, t.toString())
                    //Log.i("Proyecto",t.toString())
                }

            });
    }

    // endregion

    private suspend fun registrarMedicion(path:String, extension:String) {

        // on below line we are getting the data from
        // email and setting it in email variable.
        val cedula = sharedPreferences.getString(CEDULA_KEY, null)!!

        var tarea_id = sharedPreferences.getInt(ID_TAREA_KEY, 0)

        val task = TareaRequest(tarea_id, cedula, path, extension,
            valCodMedidor, valGpsCoordenates, 0, 1)

        Log.i("tarea que se envía a registrar",task.toString())

        // .listUser(): Ejecuta el método / de la BASE_URL que se encuentra en la clase Constants
        apiClient.getApiService(this)
            .actualizar_tarea(task)
            .enqueue(object : Callback<OperationResponse> {
                override fun onResponse(
                    call: Call<OperationResponse>,
                    // Este objeto response es de tipo UserResponse
                    response: Response<OperationResponse>
                ) {
                    /*
                        En este punto, ya se consultaron los datos a la base
                        y se devuelve el cuerpo que devolvió la API de NodeJs.
                        En el cuerpo del response está la propiedad users,
                        que es donde está la lista de usuarios que devolvió NodeJs
                    */
                    val existe_error = response.body()?.error.toString().toBoolean()
                    val operation_body = response.body()?.operation
                    //Log.i("Proyecto",Gson().toJson(listaUsuario))
                    Log.i("existe_error",existe_error.toString())
                    Log.i("body ultima función",operation_body.toString())
//                    Log.i("Proyecto Gson lista tareas", Gson().toJson(listaTareas))


                    if (operation_body != null) {
                        val IdTarea = operation_body.get(0).id_tarea
                        if (response.body() != null && !existe_error
                            && IdTarea > 0) {
                            Log.wtf("Mensaje desde wtf","Ventana cerrándose sin opción a regresar desde botón atrás")

                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()



                        } else {
                            MyMessage.toast(applicationContext, "No existen registro")
                        }
                    }
                }

                override fun onFailure(call: Call<OperationResponse>, t: Throwable) {
                    MyMessage.toast(applicationContext, t.toString())
                    //Log.i("Proyecto",t.toString())
                }

            });
    }

    fun regresar(view: View){
        finish()
    }
}