package com.openrattle.wanandroid.auth

import com.openrattle.base.utils.UiText

data class RegisterState(
    val username: String = "",
    val password: String = "",
    val repassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: UiText? = null
)

sealed class RegisterIntent {
    data class UpdateUsername(val username: String) : RegisterIntent()
    data class UpdatePassword(val password: String) : RegisterIntent()
    data class UpdateRepassword(val repassword: String) : RegisterIntent()
    data object TogglePasswordVisibility : RegisterIntent()
    data object Register : RegisterIntent()
}

sealed class RegisterEffect {
    data class ShowMessage(val message: UiText) : RegisterEffect()
    data object RegisterSuccess : RegisterEffect()
}
