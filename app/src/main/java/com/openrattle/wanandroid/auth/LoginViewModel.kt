package com.openrattle.wanandroid.auth

import com.openrattle.core.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : MviViewModel<LoginState, LoginIntent, LoginEffect>() {

    override fun initialState(): LoginState = LoginState()

    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> {
                performLogin(intent.username, intent.password)
            }
        }
    }

    private suspend fun performLogin(username: String, password: String) {
        updateState { it.copy(isLoading = true, error = null) }
        repository.login(username, password).onSuccess {
            updateState { it.copy(isLoading = false, success = true) }
            emitEffect(LoginEffect.LoginSuccess)
        }.onFailure { e ->
            updateState { it.copy(isLoading = false, error = e.message) }
            emitEffect(LoginEffect.ShowMessage(e.message ?: "登录失败"))
        }
    }
}
