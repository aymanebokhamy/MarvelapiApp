package com.example.marvelapi_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject

class CharacterAdapter(private val characters: JSONArray) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters.getJSONObject(position)
        holder.bind(character)
    }

    override fun getItemCount(): Int = characters.length()

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        private val textView1: TextView = view.findViewById(R.id.textView1)
        private val textView2: TextView = view.findViewById(R.id.textView2)

        fun bind(character: JSONObject) {
            val name = character.getString("name")
            val description = character.getString("description")
            val thumbnail = character.getJSONObject("thumbnail")
            val imageUrl = "${thumbnail.getString("path")}.${thumbnail.getString("extension")}"

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(imageView)

            textView1.text = name
            if (description.isEmpty()) {
                textView2.text = "Description not available"
            } else {
                textView2.text = description
            }

        }
    }
}
