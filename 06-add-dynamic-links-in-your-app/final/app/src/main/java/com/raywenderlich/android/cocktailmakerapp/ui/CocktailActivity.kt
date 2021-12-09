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

package com.raywenderlich.android.cocktailmakerapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.raywenderlich.android.cocktailmakerapp.R
import com.raywenderlich.android.cocktailmakerapp.data.model.Drink
import com.raywenderlich.android.cocktailmakerapp.databinding.ActivityCocktailBinding
import com.raywenderlich.android.cocktailmakerapp.vm.CocktailViewModel
import com.raywenderlich.android.cocktailmakerapp.vm.UIState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Main Screen
 */
class CocktailActivity : AppCompatActivity() {

  private val binding by lazy {
    ActivityCocktailBinding.inflate(layoutInflater)
  }
  private val viewModel by lazy {
    ViewModelProviders.of(this)[CocktailViewModel::class.java]
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    observe()
    intent.getStringExtra(ID_KEY)?.let {
      viewModel.getCocktailById(it)
    }
  }

  private fun observe() {
    lifecycleScope.launch {
      viewModel.cocktailsById.flowWithLifecycle(lifecycle).collect { value: UIState ->
        when (value) {
          is UIState.ShowData<*> -> {
            val cocktails = value.data as Drink
            val cocktail = cocktails.drinks.first()

            with(binding) {
              tvName.text = cocktail.name
              tvCategory.text = getString(R.string.category)
              tvCategoryCustom.text = cocktail.category
              tvDrinkType.text = getString(R.string.drink_type)
              tvDrinkTypeCustom.text = cocktail.drinkType
              tvGlassType.text = getString(R.string.glass_type)
              tvGlassTypeCustom.text = cocktail.glassType
              tvInstructions.text = getString(R.string.instructions)
              tvInstructionsCustom.text = cocktail.instructions
            }
          }
          is UIState.Error -> {
            Toast.makeText(applicationContext, value.message, Toast.LENGTH_SHORT).show()
          }
          UIState.Loading -> {
            Toast.makeText(applicationContext, "Loading Cocktail", Toast.LENGTH_SHORT).show()
          }

        }
      }
    }
  }

  companion object {
    const val ID_KEY = "ID"
  }
}
