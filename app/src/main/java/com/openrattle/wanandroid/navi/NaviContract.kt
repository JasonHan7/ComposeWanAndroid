package com.openrattle.wanandroid.navi

import com.openrattle.base.utils.UiText
import com.openrattle.base.model.Navi

data class NaviState(
    val naviList: List<Navi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val selectedIndex: Int = 0
)

sealed class NaviIntent {
    data object LoadData : NaviIntent()
    data class SelectCategory(val index: Int) : NaviIntent()
}

sealed class NaviEffect {
    data class ShowMessage(val message: UiText) : NaviEffect()
}
