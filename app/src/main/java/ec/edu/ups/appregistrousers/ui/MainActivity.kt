package ec.edu.ups.appregistrousers.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import ec.edu.ups.appregistrousers.R
import ec.edu.ups.appregistrousers.data.adapters.RecyclerAdapter
import ec.edu.ups.appregistrousers.data.models.User
import ec.edu.ups.appregistrousers.data.requests.TareaRequest
import ec.edu.ups.appregistrousers.data.responses.TareaResponse
import ec.edu.ups.appregistrousers.data.responses.UserResponse
import ec.edu.ups.appregistrousers.data.services.ApiClient
import ec.edu.ups.appregistrousers.util.MyMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var apiClient: ApiClient
    var dataList = ArrayList<User>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerAdapter

    private var cedula: String = ""
    private var tipo_empleado: Int = 0
    private var estado: Int? = null

    // on below line we are creating a variable
    // for prefs key and email key and pwd key.
    var PREFS_KEY = "prefs_key"
    var CEDULA_KEY = "Cedula_key"

    // on below line we are creating a variable for shared preferences.
    lateinit var sharedPreferences: SharedPreferences

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            cargarDatos()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        apiClient = ApiClient()
        recyclerView = findViewById(R.id.recycler_users)

//        val cedula:String = intent.getStringExtra("cedula").toString()
        tipo_empleado = intent.getIntExtra("tipo_empleado", 0)
        println(tipo_empleado)

        // on below line we are initializing our shared preferences variable.
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        cedula = sharedPreferences.getString(CEDULA_KEY, "").toString()
        println(cedula)


    }

    private suspend fun cargarDatos() {

        // .listUser(): Ejecuta el método / de la BASE_URL que se encuentra en la clase Constants
//        apiClient.getApiService(this).listUser()
        apiClient.getApiService(this)
            .get_tareas(TareaRequest(0, cedula, "", "",
                "","", tipo_empleado, estado))
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
                    Log.i("Proyecto Gson lista tareas",Gson().toJson(listaTareas))

                    if (listaTareas != null) {
                        recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            // Aquí envia la lista de usuarios a que se listen en el recycler
                            adapter = RecyclerAdapter(listaTareas)
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

    fun menuHome(item: MenuItem) {
        when (item.getItemId()) {
            // Revisar en res/menu/botton_nav_home.xml, había un
            // botón debajo del de salir que era el navigation_create_user,
            // y tenía de texto "Nuevo"
//            R.id.navigation_create_user -> {
//                Toast.makeText(this, "Crear Nuevo Usuario", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, UserCreateActivity::class.java).apply {
//                    putExtra("nombre", "miguel quiroz")
//                }
//                startActivity(intent)
//
//            }
            R.id.navigation_exit -> {
                Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show()

                // on below line we are creating a variable for
                // editor of shared preferences and initializing it.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                // on below line we are clearing our editor.
                editor.clear()

                // on below line we are applying changes which are cleared.
                editor.apply()

                val intent = Intent(this, login::class.java)
                startActivity(intent)
            }
        }
    }
}