package com.openrattle.wanandroid.mine

import androidx.lifecycle.viewModelScope
import com.openrattle.wanandroid.auth.AuthRepository
import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : MviViewModel<MineState, MineIntent, MineEffect>() {

    init {
        viewModelScope.launch {
            authRepository.userFlow.collectLatest { userInfo ->
                val user = userInfo?.let {
                    User(
                        username = it.username,
                        nickname = it.nickname,
                        icon = it.icon.ifBlank { null }
                    )
                }
                updateState { it.copy(
                    user = user,
                    coinCount = userInfo?.coinCount?.toString() ?: "--"
                ) }
                
                if (userInfo != null) {
                    fetchCoinInfo()
                } else {
                    // 退出登录或未登录时，所有数值统一显示为 "--"
                    updateState { it.copy(
                        level = "--", 
                        rank = "--", 
                        coinCount = "--"
                    ) }
                }
            }
        }
    }

    override fun initialState(): MineState = MineState()

    override suspend fun handleIntent(intent: MineIntent) {
        when (intent) {
            MineIntent.LoadProfile -> {
                // profile is handled by userFlow
            }
            MineIntent.Logout -> {
                authRepository.logout()
            }
            MineIntent.Refresh -> {
                fetchCoinInfo()
            }
        }
    }

    private suspend fun fetchCoinInfo() {
        authRepository.getUserCoinInfo().onSuccess { coinInfo ->
            updateState { it.copy(
                coinCount = coinInfo.coinCount.toString(),
                level = coinInfo.level.toString(),
                rank = coinInfo.rank
            ) }
        }
    }
}
