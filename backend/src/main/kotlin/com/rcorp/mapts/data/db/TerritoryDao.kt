package com.rcorp.mapts.data.db

import com.rcorp.mapts.model.territory.Territory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

object TerritoryDao {

    init {
    }

    var territories: MutableList<Territory>? = null

    fun getAllTerritories(): List<Territory> {
        if (territories != null)
            return territories!!
        val resultTerritories = mutableListOf<Territory>()
        transaction {
            addLogger(StdOutSqlLogger)
            val toLatLonFunction = CustomFunction<ExposedBlob?>("ST_Transform", BlobColumnType(), TerritorySQLData.way, intParam(4326))
            val toGeoJson = CustomFunction<String?>("ST_AsGeoJSON", TextColumnType(), toLatLonFunction)
            TerritorySQLData.slice(TerritorySQLData.id, TerritorySQLData.name, toGeoJson).select { (TerritorySQLData.boundary eq "administrative") and (TerritorySQLData.adminLevel eq "8")}.forEach {
                resultTerritories.add(Territory(it[TerritorySQLData.id], it[TerritorySQLData.name]
                        ?: "Unknown", it[toGeoJson]))
            }
        }
        territories = resultTerritories
        return resultTerritories
    }

    object TerritorySQLData : Table("planet_osm_polygon") {
        val id = long("osm_id")
        val name = text("name").nullable()
        val boundary = text("boundary").nullable()
        val adminLevel = text("admin_level").nullable()
        val way = blob("way").nullable()
    }
}