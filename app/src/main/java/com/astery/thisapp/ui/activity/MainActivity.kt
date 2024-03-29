package com.astery.thisapp.ui.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.astery.thisapp.R
import com.astery.thisapp.ui.fragments.TFragment
import com.astery.thisapp.ui.fragments.login.LoginFragment
import com.astery.thisapp.ui.fragments.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * single activity
 * */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var fragmentManager: FragmentManager
    private lateinit var currentFragment: TFragment
    private val viewModel: MainActivityViewModel by viewModels()

    private var logoutMenu: Menu? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        selectStartFragment()
    }

    private fun selectStartFragment() {
        viewModel.checkEntered()
        viewModel.isEntered.observe(this) { entered ->
            when (entered) {
                true -> pushFragment(MainFragment())
                false -> {
                    pushFragment(LoginFragment())
                }
            }
            showLogout()
        }
    }


    private fun pushFragment(fragment: TFragment) {
        currentFragment = fragment
        fragmentManager = supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.fragment_container, currentFragment)
        ft.commit()

    }

    fun move(fragment: TFragment) {
        fragment.setTransition(false)
        currentFragment.setTransition(true)

        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()

        currentFragment = fragment
        showLogout()


    }

    private fun showLogout() {
        this.logoutMenu?.findItem(R.id.action_logout)?.isVisible = isShowLogout()
    }

    private fun isShowLogout(): Boolean {
        return currentFragment is MainFragment
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        viewModel.logOut()
        move(LoginFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        this.logoutMenu = menu
        showLogout()
        return true
    }
}