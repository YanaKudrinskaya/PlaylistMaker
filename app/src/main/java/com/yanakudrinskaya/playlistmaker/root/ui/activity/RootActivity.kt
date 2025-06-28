package com.yanakudrinskaya.playlistmaker.root.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.ActivityRootBinding
import com.yanakudrinskaya.playlistmaker.root.ui.view_model.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel by viewModel<RootViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getNavigationEvents().observe(this) { event ->
            binding.bottomNavigationView.visibility = if (event) View.GONE else View.VISIBLE
        }
    }

    private fun setupListeners() {

        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.changeDestination(destination.id)
        }
    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

    }

}