package com.example.marvelapi_app

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var characters: JSONArray
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        fetchCharacters()
    }

    private fun setupRecyclerView(characters: JSONArray) {
        val adapter = CharacterAdapter(characters)
        recyclerView.adapter = adapter
    }

    private fun fetchCharacters() {
        val timeStamp = System.currentTimeMillis().toString()
        val hash = generateMarvelHash(timeStamp)

        val client = AsyncHttpClient()
        val baseURL = "https://gateway.marvel.com:443/v1/public/characters"
        val url = "$baseURL?ts=$timeStamp&apikey=5a21c46ab50f933ec09bc16a2868a484&hash=$hash&limit=20"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val data = json.jsonObject.getJSONObject("data")
                characters = data.getJSONArray("results")
                setupRecyclerView(characters)
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
