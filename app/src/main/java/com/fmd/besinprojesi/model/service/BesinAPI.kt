package com.fmd.besinprojesi.model.service

import com.fmd.besinprojesi.model.Besin
import retrofit2.http.GET

interface BesinAPI {

    //BASE URL ->//https://raw.githubusercontent.com/
    //ENDPOİNT ->atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json


    @GET("atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json")
    suspend /*istenilen zaman duraklatılıp devam eden fonkiyon*/ fun getBesin(): List<Besin>



}