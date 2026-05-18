package com.coworker.jjikmuk.feature.home

sealed class HomeEvent {
    data object OpenCamera : HomeEvent()
    data object OpenImagePicker : HomeEvent()
    data object OpenFilePicker : HomeEvent()
}