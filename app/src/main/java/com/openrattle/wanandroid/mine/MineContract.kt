package com.openrattle.wanandroid.mine

data class MineState(
    val user: User? = null,
    val coinCount: String = "--",
    val level: String = "--",
    val rank: String = "--",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class User(
    val username: String,
    val nickname: String,
    val icon: String? = null,
    val publicName: String? = null
)

sealed class MineIntent {
    data object LoadProfile : MineIntent()
    data object Logout : MineIntent()
    data object Refresh : MineIntent()
}

sealed class MineEffect {
    data class ShowMessage(val message: String) : MineEffect()
}
