package ec.edu.ups.appregistrousers.data.adapters

//import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.ups.appregistrousers.R
import ec.edu.ups.appregistrousers.data.models.Tarea
import ec.edu.ups.appregistrousers.ui.UserCreateActivity


open class MyViewHolderUser(itemview: View) : RecyclerView.ViewHolder(itemview) {
    lateinit var direccion: TextView
    lateinit var estado: TextView

    // Variables de sesión
    // on below line we are creating a variable
    // for prefs key and email key and pwd key.
    var PREFS_KEY = "prefs_key"
    var ID_TAREA_KEY = "IdTarea_Key"

    // on below line we are creating a variable for shared preferences.
    lateinit var sharedPreferences: SharedPreferences

    fun bind(task: Tarea) {


        direccion = itemView.findViewById(R.id.direccion)
        estado = itemView.findViewById(R.id.estado)

        var lblDireccionTexto = "Dirección: "
        var lblEstadoTexto = "Estado: "

        direccion.text = lblDireccionTexto + task.direccion
//        estado.text= task.estado == 0 ? "Pendiente" : "Completada"
        estado.text = if (task.estado == 0) lblEstadoTexto + "Pendiente" else lblEstadoTexto + "Completada"

        // Fuente: https://stackoverflow.com/questions/23517879/set-background-color-programmatically
        // Pendiente
        if(task.estado == 0) {
            itemView.setBackgroundColor(Color.parseColor("#ffa31a"));
        }
        // Completada
        if(task.estado == 1) {
            itemView.setBackgroundColor(Color.parseColor("#4dff4d"));
        }
        // Caso anormal
        if(task.estado > 1 || task.estado < 0) {
            itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        itemView.setOnClickListener {
//            Toast.makeText(itemView.context, task.direccion.toString(),Toast.LENGTH_SHORT).show()

            // on below line we are initializing our shared preferences variable.
            sharedPreferences = itemView.context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

            // on below line we are creating variable for editor
            // of shared prefs and initializing it.
            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            // on below line we are adding our email and
            // pwd to shared prefs to save them.
            editor.putInt(ID_TAREA_KEY, task.idTarea)

            // on below line we are applying
            // changes to our shared prefs.
            editor.apply()

            val idTarea_Sesion : Int = sharedPreferences.getInt(ID_TAREA_KEY, 0)
            Log.d("idTarea_Sesion", idTarea_Sesion.toString())

            val intent = Intent(itemView.context, UserCreateActivity::class.java)
            intent.putExtra("idTarea",task.idTarea)
            intent.putExtra("estado",task.estado)
            intent.putExtra("cargar_datos",true)

            itemView.context.startActivity(intent)

        }

    }

}