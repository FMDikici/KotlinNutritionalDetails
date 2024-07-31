package com.fmd.besinprojesi.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fmd.besinprojesi.model.Besin
import com.fmd.besinprojesi.model.service.BesinAPIServis
import com.fmd.besinprojesi.roomdb.BesinDatabase
import com.fmd.besinprojesi.util.OzelSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BesinListesiViewModel(application: Application):AndroidViewModel(application) {

    val besinler=MutableLiveData<List<Besin>>()
    val besinHataMesaji=MutableLiveData<Boolean>()
    val besinYukleniyor=MutableLiveData<Boolean>()

    private val besinAPIServis=BesinAPIServis()
    private val ozelSharedPreferences=OzelSharedPreferences(getApplication())


    private val guncellemeZamani=10 * 60 *1000*1000*1000L

     fun resfresData(){
        val kaydedilmeZamani=ozelSharedPreferences.zamaniAl()


        if(kaydedilmeZamani!=null && kaydedilmeZamani !=0L && System.nanoTime()-kaydedilmeZamani<guncellemeZamani){
            verileriRoomdanAl()
        }
        else{
            verileriInternettenAl()
        }
    }

    fun refreshDataFromInternet(){
        verileriInternettenAl()
    }

    private fun verileriRoomdanAl(){
        besinYukleniyor.value=true
        viewModelScope.launch(Dispatchers.IO){
            val besinListesi=BesinDatabase(getApplication()).besinDao().getAllBesin()
            withContext(Dispatchers.Main){
                besinleriGoster(besinListesi)
                Toast.makeText(getApplication(),"Besinleri Belli Bi Zaman Geçmediği İçin Room Yardımı ile Sizlere Sunuyoruz",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun verileriInternettenAl(){
        besinYukleniyor.value=true

        viewModelScope.launch(Dispatchers.IO) {
            val besinListesi=besinAPIServis.getData()
            withContext(Dispatchers.Main) {
                besinYukleniyor.value = false
                roomaKaydet(besinListesi)

                Toast.makeText(
                    getApplication(),
                    "Besinleri Internetteki Veriler Yardımıyla Sizlere Sunuyoruz",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun besinleriGoster(besinListesi: List<Besin>){
        besinler.value=besinListesi
        besinHataMesaji.value=false
        besinYukleniyor.value=false
    }

    private fun roomaKaydet(besinListesi:List<Besin>){

        viewModelScope.launch {

            val dao=BesinDatabase(getApplication()).besinDao()
            dao.deleteAllBesin()
            val uuidListesi=dao.insertAll(*besinListesi.toTypedArray())//tek tek besinleri vermenin kolay yolu
            var i=0
            while(i<besinListesi.size){
                besinListesi[i].uuid=uuidListesi[i].toInt()
                i=i+1
            }

            besinleriGoster(besinListesi)
        }
        ozelSharedPreferences.zamaniKaydet(System.nanoTime())
    }
}