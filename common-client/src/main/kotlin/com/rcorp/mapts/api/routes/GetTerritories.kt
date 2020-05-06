package com.rcorp.mapts.api.routes

import com.rcorp.mapts.api.repo.TerritoryRepository
import com.rcorp.mapts.api.repo.UserRepository
import com.rcorp.mapts.model.territory.Territory
import com.rcorp.mapts.model.user.User

class GetTerritories(private val territoryRepository: TerritoryRepository) {
    fun run(callback: (List<Territory>?) -> Unit) = territoryRepository.getTerritories(callback)
}
