package ec.edu.ups.appregistrousers.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import ec.edu.ups.appregistrousers.BuildConfig
import ec.edu.ups.appregistrousers.R
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


class obtener_resultados_ocr : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    // GPS
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42
//    lateinit var binding: ActivityMainBinding

    // API conexión
    lateinit var apiClient: ApiClient

    //    lateinit var shpFoto: ShapeableImageView
    lateinit var shpFoto: ImageView
    lateinit var resultado_1: TextView
    lateinit var resultado_2: TextView
    lateinit var resultado_3: TextView
    lateinit var resultado_4: TextView
//    lateinit var lblLatitud: TextView
//    lateinit var lblLongitud: TextView

    // Cámara
    private val CAMERA_REQUEST = 1888
    private val imageView: ImageView? = null
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var gps_coordenates: String
    private lateinit var gps_lbl_coordenates: String

    private var valIdTarea: Int = 0

    //    private lateinit var valCodVivienda: String
//    private lateinit var valDireccion: String
//    private lateinit var valManzana: String
//    private lateinit var valVilla: String
    private var valCodVivienda: String = ""
    private var valDireccion: String = ""
    private var valManzana: String = ""
    private var valVilla: String = ""

    // for prefs key and email key and pwd key.
    var PREFS_KEY = "prefs_key"
    var ID_TAREA_KEY = "IdTarea_Key"

    // on below line we are creating a variable for shared preferences.
    lateinit var sharedPreferences: SharedPreferences

    // region Codigo de Prueba uCrop

    // Fuente: https://www.youtube.com/watch?v=ExDaogJWGrQ&t=0s

    //    private lateinit var binding: ActivityMainBinding
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            shpFoto.setImageURI(uri)

        }


    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obtener_resultados_ocr)

        apiClient = ApiClient()

        // Fuente: https://stackoverflow.com/questions/2459524/how-can-i-pass-a-bitmap-object-from-one-activity-to-another
        shpFoto = findViewById(R.id.shpFoto)
//        val intent = intent

        // finding progressbar by its id
        progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar


        // get reference to textview
        resultado_1 = findViewById(R.id.txt_obtener_resultados_resultado_1)
        resultado_2 = findViewById(R.id.txt_obtener_resultados_resultado_2)
        resultado_3 = findViewById(R.id.txt_obtener_resultados_resultado_3)
        resultado_4 = findViewById(R.id.txt_obtener_resultados_resultado_4)

// set on-click listener
        resultado_1.setOnClickListener {
            // your code to run when the user clicks on the TextView
            seleccionar_resultado(resultado_1.text.toString())
        }

        resultado_2.setOnClickListener {
            // your code to run when the user clicks on the TextView
            seleccionar_resultado(resultado_2.text.toString())
        }

        resultado_3.setOnClickListener {
            // your code to run when the user clicks on the TextView
            seleccionar_resultado(resultado_3.text.toString())
        }

        resultado_4.setOnClickListener {
            // your code to run when the user clicks on the TextView
            seleccionar_resultado(resultado_4.text.toString())
        }

        valIdTarea = intent.getIntExtra("idTarea", 0)
        valCodVivienda = intent.getStringExtra("cod_vivienda").toString()
        valDireccion = intent.getStringExtra("direccion").toString()
        valManzana = intent.getStringExtra("manzana").toString()
        valVilla = intent.getStringExtra("villa").toString()

        // on below line we are initializing our shared preferences variable.
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

        // on below line we are creating variable for editor
        // of shared prefs and initializing it.
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // on below line we are adding our email and
        // pwd to shared prefs to save them.
        editor.putInt(ID_TAREA_KEY, valIdTarea)

        // on below line we are applying
        // changes to our shared prefs.
        editor.apply()

        Log.d("valIdTarea de sesión", valIdTarea.toString())

        // Genera la creación de abrir la cámara y capturar 1 foto
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            it.resolveActivity(packageManager).also { component ->
                createPhotoFile()
                val photoUri: Uri =
                    FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileprovider", file
                    )
                it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
        }

        // Ejecuta el intent que abre la cámara
        openCamera.launch(intent)

//
//        if (allPermisionsGrantedGPS()) {
//            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//            leer_ubicacion_actual()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION
//                ),
//                PERMISSION_ID
//            )
//        }

        // Si los permisos de GPS no han sido otorgados...
        if (!allPermisionsGrantedGPS()) {
            // Tal vez esta ventana sea la que le pide al usuario los permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }


    }

    // region Variables para abrir cámara y guardar imagen en el teléfono

    private val openCamera =
    // ActivityResultContracts.StartActivityForResult()
    // Es un parámetro que dice que ejecutará el Intent
        // que se le pase como argumento a openCamera.launch(Intent)
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                // TODO Crear objeto uCrop y abrir su ventana
                val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

                val inputUri = file.toUri()
                val outputUri = File.createTempFile("IMG_${System.currentTimeMillis()}_CroppedImg_", ".jpg", dir).toUri()

                val listUri = listOf<Uri>(inputUri,outputUri)

                cropImage.launch(listUri)


                // TODO El método anterior debe sobreescribir
                //  la variable 'file', y debajo debe ser obtenida
                //  la imagen recortada y rotada

                file = File(outputUri.path)

////                val data = result.data!!
//                //val bitmap = data.extras!!.get("data") as Bitmap
//                val bitmap = BitmapFactory.decodeFile(file.toString())
//
//                Log.i("file", file.toString())
//                Log.i("file.name", file.name.toString())
//                Log.i("file.extension", file.extension.toString())
//
//                CoroutineScope(Dispatchers.IO).launch {
//                    proceso_ocr_imagen(file)
//                }
//
////                val baos = ByteArrayOutputStream()
////                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////                val bitmapByteArray = baos.toByteArray()
////                val file = Base64.encodeToString(bitmapByteArray,Base64.DEFAULT)
//
//                // Obtiene los datos del GPS después de haber tomado la foto
//                if (allPermisionsGrantedGPS()) {
//                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//                    leer_ubicacion_actual()
//                } else {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(
//                            android.Manifest.permission.ACCESS_FINE_LOCATION,
//                            android.Manifest.permission.ACCESS_FINE_LOCATION
//                        ),
//                        PERMISSION_ID
//                    )
//                }
//
//                shpFoto.setImageBitmap(bitmap)

            }
        }

    private lateinit var file: File

    //LOS ARCHIVOS SE CREAN EN ESTE CODIGO Y SE GUARDAN
    // EN LA MEMORIA EXTERNA - CARPETA COM.EXAMPLE.UBIACIONGPS/FILES/PICTURES
    private fun createPhotoFile() {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".jpg", dir)

    }

    // endregion

    // region Variables openUCrop

//    private val cropImage =
//        registerForActivityResult(uCropContract) { uri ->
//
//        }
    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {

            val inputUri = input[0] // Archivo Uri que se usa para editar, o sea la imagen en Uri
            val outputUri = input[1] // Imagen de salida

            val options = UCrop.Options()
            options.setFreeStyleCropEnabled(true)
            options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);

            val uCrop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(800, 800)
                .withOptions(options)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return UCrop.getOutput(intent!!)!!
        }

    }

    private val cropImage =
    // ActivityResultContracts.StartActivityForResult()
    // Es un parámetro que dice que ejecutará el Intent
        // que se le pase como argumento a openCamera.launch(Intent)
        registerForActivityResult(uCropContract) { result ->
            // TODO Rssult tiene la foto, hay que actualizar la variable global 'file'
            //  con ese resultado

            val bitmap = BitmapFactory.decodeFile(file.toString())

            Log.i("file", file.toString())
            Log.i("file.name", file.name.toString())
            Log.i("file.extension", file.extension.toString())

            CoroutineScope(Dispatchers.IO).launch {
                proceso_ocr_imagen(file)
            }

//                val baos = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                val bitmapByteArray = baos.toByteArray()
//                val file = Base64.encodeToString(bitmapByteArray,Base64.DEFAULT)

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

            shpFoto.setImageBitmap(bitmap)


        }

    // endregion


    private fun seleccionar_resultado(resultado: String) {

        if (!resultado.isNullOrEmpty()) {
            val intent = Intent(this, UserCreateActivity::class.java)
            intent.putExtra("GPS_coordenates",gps_coordenates)
            intent.putExtra("GPS_lbl_coordenates",gps_lbl_coordenates)
            intent.putExtra("resultado_ocr",resultado)
            intent.putExtra("cod_vivienda", valCodVivienda)
            intent.putExtra("direccion", valDireccion)
            intent.putExtra("manzana", valManzana)
            intent.putExtra("villa", valVilla)
            intent.putExtra("imagen", file.path)
            intent.putExtra("permitir_boton", true)

            startActivity(intent)
            this.finish()
        }

    }

    // region GPS Métodos allPermisionsGrantedGPS


    // region allPermisionsGrantedGPS

    private fun allPermisionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // endregion

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
                            gps_coordenates = location.latitude.toString() +  ";"  + location.longitude.toString()
                            gps_lbl_coordenates = "Lat ${location.latitude} Lon ${location.longitude}"
                            Log.d("gps_coordenates", gps_coordenates)

                            println(gps_coordenates)

                        }
                    }
            } else {
                Toast.makeText(this, "Activar ubicación", Toast.LENGTH_SHORT).show()
                // Es probable que esta linea sea la que muestra al usuario
                // el recuadro que le pide permitir o denegar uso de GPS
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
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

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) }

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

    companion object {
        private val REQUIRED_PERMISSIONS_GPS= arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // endregion


    // endregion


    // region API - Python Process

    private suspend fun proceso_ocr_imagen(file: File) {


//        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("files[]", file.name, requestFile)


        // Fuente: https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated
        // La idea para reparar el error android.view.ViewRootImpl$CalledFromWrongThreadException:
        // Only the original thread that created a view hierarchy can touch its views. vino de:
        // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
        Handler(Looper.getMainLooper()).postDelayed({
            // Fuente: https://stackoverflow.com/questions/4280608/disable-a-whole-activity-from-user-action
            // Bloquea la app para que no se disparen
            // eventos con el toque del usuario
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // Fuente: https://www.geeksforgeeks.org/progressbar-in-kotlin/
            // Before clicking the button the progress bar will invisible
            // so we have to change the visibility of the progress bar to visible
            // setting the progressbar visibility to visible
            progressBar.visibility = View.VISIBLE
        }, 5) // milliseconds

        Log.d("Message","Comenzando procesamiento ocr...")
        // .listUser(): Ejecuta el método / de la BASE_URL que se encuentra en la clase Constants
//        apiClient.getApiService(this).listUser()
        apiClient.getPythonApiService(this)
            .ocr_processing(body)
//            .enqueue(object : Callback<OcrResponse> { // List<OcrResponse>
//            .enqueue(object : Callback<List<OcrResponse>> { // Array<String>
            .enqueue(object : Callback<Array<String>> { //
                override fun onResponse(
                    call: Call<Array<String>>,
                    // Este objeto response es de tipo OcrResponse
                    response: Response<Array<String>>
                ) {
                    /*
                        En este punto, ya se consultaron los datos a la base
                        y se devuelve el cuerpo que devolvió la API de NodeJs.
                        En el cuerpo del response está la propiedad users,
                        que es donde está la lista de usuarios que devolvió NodeJs
                    */
                    val resultados_ocr = response.body()
//                    val resultados_ocr = response.body()?.tasks
                    Log.i("response",response.toString())


                    if (resultados_ocr != null) {
//
//                        for(element in resultados_ocr){
//                            Log.d("resultados_ocr element",element)
//                        }

                        resultado_1.setText(resultados_ocr[0])
                        resultado_2.setText(resultados_ocr[1])
                        resultado_3.setText(resultados_ocr[2])
                        resultado_4.setText(resultados_ocr[3])

                        // Fuente: https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated
                        Handler(Looper.getMainLooper()).postDelayed({

                            // setting the visibility of the progressbar to invisible
                            // or you can use View.GONE instead of invisible
                            // View.GONE will remove the progressbar
                            progressBar.visibility = View.INVISIBLE


                            // Anything you want to start after 2s
                            // Fuente: https://stackoverflow.com/questions/4280608/disable-a-whole-activity-from-user-action
                            // Vuelve la ventana a la normalidad
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }, 2000)

                    } else {
                        MyMessage.toast(applicationContext, "No existen opciones")
                        val intent = Intent(applicationContext, UserCreateActivity::class.java)
                        intent.putExtra("cod_vivienda", valCodVivienda)
                        intent.putExtra("direccion", valDireccion)
                        intent.putExtra("manzana", valManzana)
                        intent.putExtra("villa", valVilla)
//                        intent.putExtra("permitir_boton", true)

                        startActivity(intent)
                        finish()
                    }
                }

                // override fun onFailure(call: Call<OcrResponse>, t: Throwable) {
//                override fun onFailure(call: Call<List<OcrResponse>>, t: Throwable) {
                override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                    MyMessage.toast(applicationContext, t.toString())
                    Log.i("Metodo OCR error:",t.toString())

                    val intent = Intent(applicationContext, UserCreateActivity::class.java)
                    intent.putExtra("cod_vivienda", valCodVivienda)
                    intent.putExtra("direccion", valDireccion)
                    intent.putExtra("manzana", valManzana)
                    intent.putExtra("villa", valVilla)

                    startActivity(intent)
                    finish()

//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            });
    }

    // endregion

// TODO Descomentar esto después, no se si se usa para algo en esta clase

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            val imageBitmap = data?.extras?.get("data") as Bitmap
////            binding.shpFoto.setImageBitmap(imageBitmap)
//            shpFoto.setImageBitmap(imageBitmap)
//        }
//    }

}