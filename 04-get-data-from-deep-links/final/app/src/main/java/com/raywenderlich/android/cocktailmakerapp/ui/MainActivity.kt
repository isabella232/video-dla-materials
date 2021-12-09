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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.cocktailmakerapp.R
import com.raywenderlich.android.cocktailmakerapp.adapter.CocktailAdapter
import com.raywenderlich.android.cocktailmakerapp.data.model.Drink
import com.raywenderlich.android.cocktailmakerapp.databinding.ActivityMainBinding
import com.raywenderlich.android.cocktailmakerapp.ui.CocktailActivity.Companion.ID_KEY
import com.raywenderlich.android.cocktailmakerapp.vm.MainViewModel
import com.raywenderlich.android.cocktailmakerapp.vm.UIState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  private val binding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }
  private val viewModel by lazy {
    ViewModelProviders.of(this)[MainViewModel::class.java]
  }
  private val rvAdapter by lazy { CocktailAdapter(::onCocktailClick) }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    observe()
    viewModel.fetchCocktails()

    with(binding.recyclerView) {
      layoutManager = LinearLayoutManager(this@MainActivity)
      addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      adapter = rvAdapter
    }
    handleDeepLink(intent?.action, intent?.data)
  }

  private fun observe() {
    lifecycleScope.launch {
      viewModel.cocktails.flowWithLifecycle(lifecycle).collect { value: UIState ->
        when (value) {
          is UIState.ShowData<*> -> {
            val cocktails = value.data as Drink
            binding.progressBar.visibility = View.GONE
            rvAdapter.submitList(cocktails.drinks)
          }
          is UIState.Error -> {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(applicationContext, value.message, Toast.LENGTH_SHORT).show()
          }
          UIState.Loading -> {
            binding.progressBar.visibility = View.VISIBLE
          }
        }
      }
    }
  }

  private fun onCocktailClick(id: String) {
    startActivity(Intent(this, CocktailActivity::class.java).putExtra(ID_KEY, id))
  }

  private fun showOffer(appLinkData: Uri?) {
    val drinkId = appLinkData?.getQueryParameter("i")
    if (!drinkId.isNullOrBlank()) {
      onCocktailClick(drinkId)
    } else {
      Toast.makeText(this, R.string.drink_doesnt_exist, Toast.LENGTH_SHORT).show()
    }
  }

  private fun handleDeepLink(appLinkAction: String?, appLinkData: Uri?) {
    if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
      showOffer(appLinkData)
    }
  }
}
