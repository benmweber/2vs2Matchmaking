package com.example.spikeball
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.recyclerview_layout.view.*

class MyRecyclerViewerAdapter(private val items : MutableList<Player>, private val context: Context, private val clickListener: (Player) -> Unit) : RecyclerView.Adapter<ViewHolder>(){


    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_layout, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder?.playerType?.text = items[position].mName
        holder.bind(items[position], clickListener)
    }

}
class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
    // Holds the TextView that will add each animal to
    val playerType = view.PlayerType

    fun bind(pl: Player, clickListener: (Player) -> Unit){
        itemView.PlayerType.text = pl.mName
        itemView.setOnClickListener {
            clickListener(pl)
            itemView.PlayerType.isChecked = !itemView.PlayerType.isChecked
        }
    }
}



