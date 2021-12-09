package com.raywenderlich.android.cocktailmakerapp.vm

sealed class UIState {
  object Loading : UIState()
  data class ShowData<T>(val data: T) : UIState()
  data class Error(val message: String) : UIState()
}