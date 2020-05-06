package com.rcorp.mapts.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.rcorp.mapts.R
import com.rcorp.mapts.model.territory.Territory
import com.rcorp.mapts.presenter.map.MapContract
import com.rcorp.mapts.presenter.map.MapPresenter
import kotlinx.android.synthetic.main.activity_map.*
import org.json.JSONObject

class MapActivity : AppCompatActivity(), MapContract.View {

    private lateinit var mapboxMap: MapboxMap
    private val presenter: MapPresenter = MapPresenter()
    private var style: Style? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync {
            mapboxMap = it
            mapboxMap.setStyle(Style.Builder()
                    .fromUri("mapbox://styles/gadgeurx/ck8errj47070m1itchg9vv0gm"), Style.OnStyleLoaded {
                style = it
            })
        }
        presenter.start(this)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onTerritoriesLoaded(territories: List<Territory>) {
        Log.i("Get territories", "Get territories ${territories.size}")
        val geojson = GeoJson()

        var index = 0
        territories.forEach {
            geojson.features.add(GeoJsonFeature(it.id, it.name, it.geometry))
        }
        Log.i("CITY", Gson().toJson(geojson))
        style?.addSource(GeoJsonSource("cities-source", Gson().toJson(geojson)))

        val citiesFillLayer = FillLayer("cities-layer", "cities-source")
                .withProperties(fillColor(Color.BLUE));
        style?.addLayer(citiesFillLayer);
    }
}

class GeoJson {
    @SerializedName("type")
    val type = "FeatureCollection"

    @SerializedName("features")
    val features = mutableListOf<Any>()
}

class GeoJsonFeature(val id: Long, val name: String, geometry: String?) {

    @SerializedName("geometry")
    val geometry = JSONObject(geometry)
}
