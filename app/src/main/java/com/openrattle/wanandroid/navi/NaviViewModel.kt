package com.openrattle.wanandroid.navi

import androidx.lifecycle.viewModelScope
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NaviViewModel @Inject constructor(
    private val getNaviUseCase: GetNaviUseCase
) : MviViewModel<NaviState, NaviIntent, NaviEffect>() {

    override fun initialState(): NaviState = NaviState()

    init {
        viewModelScope.launch {
            getNaviUseCase.naviFlow.collectLatest { list ->
                if (list.isNotEmpty()) {
                    updateState { it.copy(naviList = list, isLoading = false) }
                }
            }
        }
        dispatch(NaviIntent.LoadData)
    }

    override suspend fun handleIntent(intent: NaviIntent) {
        when (intent) {
            is NaviIntent.LoadData -> loadNaviList()
            is NaviIntent.SelectCategory -> {
                updateState { it.copy(selectedIndex = intent.index) }
            }
        }
    }

    private suspend fun loadNaviList() {
        if (state.value.isLoading && state.value.naviList.isNotEmpty()) return
        
        updateState { it.copy(isLoading = true, error = null) }

        getNaviUseCase(forceRefresh = false)
            .onSuccess {
                updateState { it.copy(isLoading = false) }
                // 后台静默刷新
                viewModelScope.launch {
                    getNaviUseCase.refresh()
                }
            }
            .onFailure { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                emitEffect(NaviEffect.ShowMessage(e.message ?: "加载失败"))
            }
    }
}
