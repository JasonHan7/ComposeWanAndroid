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
            is RegisterIntent.Register -> {
                performRegister(intent.username, intent.password, intent.repassword)
            }
        }
    }

    private suspend fun performRegister(username: String, password: String, repassword: String) {
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
