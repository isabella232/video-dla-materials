/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.cocktailmakerapp.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.cocktailmakerapp.data.network.NetworkResult
import com.raywenderlich.android.cocktailmakerapp.data.repository.CocktailRepository
import com.raywenderlich.android.cocktailmakerapp.data.repository.CocktailRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

  private val cocktailRepository: CocktailRepository = CocktailRepositoryImpl()
  private val _cocktails: MutableStateFlow<UIState> = MutableStateFlow(UIState.Loading)
  val cocktails = _cocktails.asStateFlow()

  fun fetchCocktails() {
    viewModelScope.launch {
      when (val value = cocktailRepository.fetchCocktails()) {
        is NetworkResult.Success -> _cocktails.value = UIState.ShowData(value.data)
        is NetworkResult.APIError -> _cocktails.value = UIState.Error("An API occurred")
        is NetworkResult.ServerError -> _cocktails.value = UIState.Error("An error occurred")
        is NetworkResult.NetworkError -> _cocktails.value = UIState.Error("You don't have an active internet connection")
      }
    }
  }
}