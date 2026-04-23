package com.openrattle.wanandroid.auth

data class RegisterState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

sealed class RegisterIntent {
    data class Register(val username: String, val password: String, val repassword: String) : RegisterIntent()
}

sealed class RegisterEffect {
    data class ShowMessage(val message: String) : RegisterEffect()
    data object RegisterSuccess : RegisterEffect()
}
