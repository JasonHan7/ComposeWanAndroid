package com.openrattle.wanandroid.auth

import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : MviViewModel<RegisterState, RegisterIntent, RegisterEffect>() {

    override fun initialState(): RegisterState = RegisterState()

    override suspend fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.UpdateUsername -> {
                updateState { it.copy(username = intent.username) }
            }
            is RegisterIntent.UpdatePassword -> {
                updateState { it.copy(password = intent.password) }
            }
            is RegisterIntent.UpdateRepassword -> {
                updateState { it.copy(repassword = intent.repassword) }
            }
            is RegisterIntent.TogglePasswordVisibility -> {
                updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is RegisterIntent.Register -> {
                performRegister()
            }
        }
    }

    private suspend fun performRegister() {
        val username = state.value.username
        val password = state.value.password
        val repassword = state.value.repassword

        updateState { it.copy(isLoading = true, error = null) }
        repository.register(username, password, repassword).onSuccess {
            updateState { it.copy(isLoading = false, success = true) }
            emitEffect(RegisterEffect.RegisterSuccess)
        }.onFailure { e ->
            updateState { it.copy(isLoading = false, error = e.message) }
            emitEffect(RegisterEffect.ShowMessage(e.message ?: "注册失败"))
        }
    }
}
