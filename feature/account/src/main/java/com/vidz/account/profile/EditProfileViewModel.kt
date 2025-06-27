package com.vidz.account.profile

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountUpdateRequest
import com.vidz.domain.usecase.account.GetMeUseCase
import com.vidz.domain.usecase.account.UpdateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<EditProfileViewModel.EditProfileViewEvent,
        EditProfileViewModel.EditProfileViewState,
        EditProfileViewModel.EditProfileViewModelState>(
    initState = EditProfileViewModelState()
) {

    init {
        loadUserProfile()
    }

    override fun onTriggerEvent(event: EditProfileViewEvent) {
        when (event) {
            is EditProfileViewEvent.LoadProfile -> loadUserProfile()
            is EditProfileViewEvent.SaveProfile -> saveProfile(event.fullName, event.phoneNumber)
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch(ioDispatcher) {
            viewModelState.value = viewModelState.value.copy(isLoading = true, error = null)
            
            getMeUseCase().collect { result ->
                when (result) {
                    is Result.Init -> {
                        // Keep loading state
                    }
                    is Result.Success -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            account = result.data,
                            error = null
                        )
                    }
                    is Result.ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            error = when (result) {
                                is Result.ServerError.Internet -> "No internet connection"
                                is Result.ServerError.Token -> "Session expired, please login again"
                                is Result.ServerError.General -> result.message ?: "Unknown error occurred"
                                else -> "Failed to load profile"
                            }
                        )
                    }
                }
            }
        }
    }

    private fun saveProfile(fullName: String, phoneNumber: String) {
        viewModelScope.launch(ioDispatcher) {
            val currentAccount = viewModelState.value.account ?: return@launch
            
            viewModelState.value = viewModelState.value.copy(
                isUpdating = true,
                updateError = null,
                isUpdateSuccess = false
            )
            
            val updateRequest = AccountUpdateRequest(
                fullName = fullName.ifBlank { null },
                phoneNumber = phoneNumber.ifBlank { null },
                role = currentAccount.role
            )
            
            updateAccountUseCase(currentAccount.id, updateRequest).collect { result ->
                when (result) {
                    is Result.Init -> {
                        // Keep updating state
                    }
                    is Result.Success -> {
                        viewModelState.value = viewModelState.value.copy(
                            isUpdating = false,
                            account = result.data,
                            isUpdateSuccess = true,
                            updateError = null
                        )
                    }
                    is Result.ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isUpdating = false,
                            updateError = when (result) {
                                is Result.ServerError.Internet -> "No internet connection"
                                is Result.ServerError.Token -> "Session expired, please login again"
                                is Result.ServerError.General -> result.message ?: "Unknown error occurred"
                                is Result.ServerError.MissingParam -> "Invalid input: ${result.message}"
                                else -> "Failed to update profile"
                            },
                            isUpdateSuccess = false
                        )
                    }
                }
            }
        }
    }

    data class EditProfileViewModelState(
        val isLoading: Boolean = false,
        val account: Account? = null,
        val error: String? = null,
        val isUpdating: Boolean = false,
        val updateError: String? = null,
        val isUpdateSuccess: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState = EditProfileViewState(
            isLoading = isLoading,
            account = account,
            error = error,
            isUpdating = isUpdating,
            updateError = updateError,
            isUpdateSuccess = isUpdateSuccess
        )
    }

    data class EditProfileViewState(
        val isLoading: Boolean,
        val account: Account?,
        val error: String?,
        val isUpdating: Boolean,
        val updateError: String?,
        val isUpdateSuccess: Boolean
    ) : ViewState()

    sealed class EditProfileViewEvent : ViewEvent {
        object LoadProfile : EditProfileViewEvent()
        data class SaveProfile(val fullName: String, val phoneNumber: String) : EditProfileViewEvent()
    }
} 