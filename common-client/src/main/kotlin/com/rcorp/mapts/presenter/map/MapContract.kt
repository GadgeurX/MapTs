package com.rcorp.mapts.presenter.map

import com.rcorp.mapts.model.territory.Territory

class MapContract {
    interface View {
        fun onTerritoriesLoaded(territories: List<Territory>)
    }
}