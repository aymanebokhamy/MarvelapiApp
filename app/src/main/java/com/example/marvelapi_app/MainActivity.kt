package com.example.marvelapi_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var buttonRequest: Button
    private var currentCharacterIndex = 0
    private lateinit var characters: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)
        buttonRequest = findViewById(R.id.buttonRequest)

        fetchCharacters()

        buttonRequest.setOnClickListener {
            currentCharacterIndex++
            if (currentCharacterIndex >= characters.length()) {
                currentCharacterIndex = 0
            }
            setCharacterDetails(currentCharacterIndex)
        }
    }

    private fun setCharacterDetails(index: Int) {
        val character = characters.getJSONObject(index)
        val name = character.getString("name")
        val description = character.getString("description")
        val thumbnail = character.getJSONObject("thumbnail")
        val imageUrl = "${thumbnail.getString("path")}.${thumbnail.getString("extension")}"
        Log.d("ImageUrl", imageUrl)

        Glide.with(this@MainActivity)
            .load(imageUrl)
            .into(imageView)

        textView1.text = name
        textView2.text = description
    }

    private fun fetchCharacters() {
        val timeStamp = System.currentTimeMillis().toString()
        val hash = generateMarvelHash(timeStamp)

        val client = AsyncHttpClient()
        val baseURL = "https://gateway.marvel.com:443/v1/public/characters"
        val url = "$baseURL?ts=$timeStamp&apikey=5a21c46ab50f933ec09bc16a2868a484&hash=$hash&limit=5"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val data = json.jsonObject.getJSONObject("data")
                characters = data.getJSONArray("results")
                setCharacterDetails(currentCharacterIndex)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String,
                throwable: Throwable?
            ) {
                Log.e("APIError", "Failed to fetch characters: $response")
            }
        })
    }

    private fun generateMarvelHash(timestamp: String): String {
        val toBeHashed = timestamp + "24eaf071d090944596251ee901563bd4cfda1062" + "5a21c46ab50f933ec09bc16a2868a484"
        return md5(toBeHashed)
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
