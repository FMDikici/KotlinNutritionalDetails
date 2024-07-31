package com.fmd.besinprojesi.model.service

import com.fmd.besinprojesi.model.Besin
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BesinAPIServis {

    val retrofit=Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BesinAPI::class.java)

    suspend fun getData():List<Besin>{
        return retrofit.getBesin()
    }
}