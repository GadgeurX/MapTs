package com.rcorp.mapts.presenter.map

import com.rcorp.mapts.UIDispatcher
import com.rcorp.mapts.api.repo.TerritoryRepository
import com.rcorp.mapts.api.repo.UserRepository
import com.rcorp.mapts.api.routes.GetTerritories
import com.rcorp.mapts.api.routes.RegisterUser
import com.rcorp.mapts.model.user.User
import com.rcorp.mapts.presenter.map.MapContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapPresenter {
    private val getTerritories: GetTerritories = GetTerritories(TerritoryRepository())
    private lateinit var view: MapContract.View

    fun start(view: MapContract.View) {
        this.view = view
        loadTerritories()
    }

    private fun loadTerritories() {
        println("Load territories")
        try {
            getTerritories.run { territories ->
                GlobalScope.launch(UIDispatcher) {
                    if (territories != null)
                        view.onTerritoriesLoaded(territories)
                }
            }
        } catch (e: Exception) {
        }
    }
}