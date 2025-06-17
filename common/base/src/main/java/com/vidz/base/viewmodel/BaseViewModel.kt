package com.vidz.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel<VE : ViewEvent, VS : ViewState, VMS : ViewModelState>(
    initState: VMS
) : ViewModel() {

    protected val viewModelState = MutableStateFlow(initState)

    // UI state exposed to the UI
    val uiState: StateFlow<VS> = viewModelState
        .map { it.toUiState() as VS }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState() as VS
        )


    abstract fun onTriggerEvent(event: VE)

}
