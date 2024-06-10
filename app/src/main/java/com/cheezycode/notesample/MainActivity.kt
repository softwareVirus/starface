package com.cheezycode.notesample

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.cheezycode.notesample.models.User
import com.cheezycode.notesample.models.UserResponse
import com.cheezycode.notesample.utils.Constants.PREFS_TOKEN_FILE
import com.cheezycode.notesample.utils.NetworkResult
import com.cheezycode.notesample.utils.TokenManager
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val authViewModel by viewModels<SearchViewModel>()
    private var user: User? = null

    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val menuButton: ImageButton = findViewById(R.id.menu_button)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // Disable the default home as up button
        supportActionBar?.title = null // Remove title from toolbar

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.enterScreenFragment // Add all top-level destinations here
        ), drawerLayout)

        NavigationUI.setupWithNavController(navigationView, navController)

        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_camera -> {
                    navController.navigate(R.id.cameraFragment)
                }
                R.id.nav_search -> {
                    navController.navigate(R.id.searchFragment)
                }
                R.id.nav_history -> {
                    navController.navigate(R.id.historyFragment)
                }
                R.id.nav_logout -> {
                    user = null
                    tokenManager.deleteToken(PREFS_TOKEN_FILE)
                }
                R.id.nav_login -> {
                    navController.navigate(R.id.loginFragment)
                }
                R.id.nav_signup -> {
                    navController.navigate(R.id.registerFragment)
                }
                R.id.nav_user -> {
                    navController.navigate(R.id.profileFragment)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        // Observe the user data
        authViewModel.userSearchResponseLiveData.observe(this, Observer { result ->
            handleSearchResult(result, navigationView.menu, navController)
        })

        authViewModel.getUser()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchResult(result: NetworkResult<*>, menu: Menu, navController: NavController) {
        when (result) {
            is NetworkResult.Success<*> -> {
                Log.d("asdas", (result.data is UserResponse).toString())
                if (result.data is UserResponse) {
                    user = (result.data as UserResponse?)?.user
                    updateNavigationMenu(menu, true, navController)
                } else {
                    updateNavigationMenu(menu, false, navController)
                }
            }
            is NetworkResult.Error<*> -> {
                updateNavigationMenu(menu, false, navController)
            }
        }
    }

    private fun updateNavigationMenu(menu: Menu, isLoggedIn: Boolean, navController: NavController) {
        menu.findItem(R.id.nav_login).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_signup).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
        menu.findItem(R.id.nav_camera).isVisible = true
        menu.findItem(R.id.nav_search).isVisible = isLoggedIn
        menu.findItem(R.id.nav_history).isVisible = true
        if (isLoggedIn) {
            menu.findItem(R.id.nav_user).isVisible = true
            menu.findItem(R.id.nav_user).title = (user?.firstname ?: "") + " " + user?.lastname
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

}
