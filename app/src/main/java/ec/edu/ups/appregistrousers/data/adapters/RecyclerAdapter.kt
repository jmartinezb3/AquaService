package ec.edu.ups.appregistrousers.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ec.edu.ups.appregistrousers.R
import ec.edu.ups.appregistrousers.data.models.Tarea
import ec.edu.ups.appregistrousers.data.models.User

// ORIGINAL
//class RecyclerAdapter(val userlist: List<User>): RecyclerView.Adapter<MyViewHolderUser>() {
class RecyclerAdapter(val userlist: List<Tarea>): RecyclerView.Adapter<MyViewHolderUser>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderUser {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_users,parent,false)
        val viewHolder = MyViewHolderUser(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolderUser, position: Int) {
        userlist?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return userlist?.size!!
    }

}