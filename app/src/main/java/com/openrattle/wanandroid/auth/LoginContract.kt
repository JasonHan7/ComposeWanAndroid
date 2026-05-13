package com.openrattle.wanandroid.auth

import com.openrattle.base.utils.UiText

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: UiText? = null
)

sealed class LoginIntent {
    data class UpdateUsername(val username: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    data object TogglePasswordVisibility : LoginIntent()
    data object Login : LoginIntent()
}

sealed class LoginEffect {
    data class ShowMessage(val message: UiText) : LoginEffect()
    data object LoginSuccess : LoginEffect()
}
