package com.rcorp.mapts.mapPage

import com.rcorp.mapts.mapboxgl
import com.rcorp.mapts.model.territory.Territory
import com.rcorp.mapts.presenter.map.MapContract
import com.rcorp.mapts.presenter.map.MapPresenter
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlin.browser.window
import kotlin.random.Random

class MapPage {

    init {
        window.asDynamic().mapPage = this
    }

    @JsName("template")
    val template = createHTML().div {
        div("loader") {
            attributes["style"] = "color:black;z-index:10;top:0;bottom:0; position:absolute; z-index: 10;margin: auto;right: 0;left: 0;"
            attributes["v-if"] = "loading"
            span("sr-only") { + "Loading..." }
        }
        div {
            id = "map"
            attributes["style"] = "opacity: 1;"
        }
    }

    var map: dynamic? = null

    @JsName("data")
    fun data(): Any = object : MapContract.View {

        @JsName("loading")
        var loading = true

        val presenter = MapPresenter()

        init {
            presenter.start(this)
        }

        override fun onTerritoriesLoaded(territories: List<Territory>) {

            val geo = object {
                @JsName("type")
                val type = "FeatureCollection"
                @JsName("features")
                val features = mutableListOf<Territory>()
            };

            console.log(territories.size)

            territories.forEach {
                it.geometry = JSON.parse(it.geometry ?: "")
                it.asDynamic()["properties"] = object {

                    var letters = "0123456789ABCDEF"
                    @JsName("color")
                    var color = "#"
                    init {
                        for (i in 0..5) {
                            color += letters[Random.nextInt(0, 16)]
                        }
                    }

                }
                geo.features.add(it)
            }

            map.addSource("cities", object {
                @JsName("type")
                val type = "geojson"
                @JsName("data")
                val data = geo
            })


            // Add a layer showing the state polygons.
            map.addLayer(object {
                @JsName("id")
                val id = "cities-layer"
                @JsName("type")
                val type = "fill"
                @JsName("source")
                val source = "cities"
                @JsName("paint")
                val paint = object {
                }

                init {
                    paint.asDynamic()["fill-color"] = js("[\"get\", \"color\"]")
                    paint.asDynamic()["fill-outline-color"] = "#AA3333"
                    paint.asDynamic()["fill-opacity"] = 0.3
                }
            })
            loading = false
        }

    }

    @JsName("mounted")
    fun mounted() {
        mapboxgl.asDynamic().accessToken = "pk.eyJ1IjoiZ2FkZ2V1cngiLCJhIjoiY2sxMHY4andrMDRhZDNubzBseXIyNW8yNyJ9.ZX9pc8gCJtGmmkvrtsqCDg"
        map = js("new mapboxgl.Map({\n" +
                "        style: 'mapbox://styles/gadgeurx/ck8errj47070m1itchg9vv0gm',\n" +
                "        center: [-0.8769057444109194, 47.0912337940309],\n" +
                "        zoom: 8.3,\n" +
                "        pitch: 45,\n" +
                "        bearing: -17.6,\n" +
                "        container: 'map',\n" +
                "        antialias: true\n" +
                "    });")
    }
}