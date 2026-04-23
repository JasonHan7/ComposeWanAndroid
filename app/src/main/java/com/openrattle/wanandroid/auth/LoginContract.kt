package com.openrattle.wanandroid.auth

data class LoginState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

sealed class LoginIntent {
    data class Login(val username: String, val password: String) : LoginIntent()
}

sealed class LoginEffect {
    data class ShowMessage(val message: String) : LoginEffect()
    data object LoginSuccess : LoginEffect()
}
