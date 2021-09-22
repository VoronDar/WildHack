package com.astery.thisapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {


    private lateinit var fragmentManager: FragmentManager
    private lateinit var currentFragment: TFragment

    private lateinit var logoutMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity)
        setSupportActionBar(findViewById(R.id.toolbar))


        pushFragment()

    }


    private fun pushFragment(){
        currentFragment = LoginFragment()
        fragmentManager = supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.fragment_container, currentFragment)
        ft.commit()

        showLogout(false)

    }

    fun move(fragment: TFragment) {
        fragment.setTransition(false)
        currentFragment.setTransition(true)

        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()

        currentFragment = fragment


    }

    fun showLogout(show:Boolean){
        if (::logoutMenu.isInitialized)
            this.logoutMenu.findItem(R.id.action_logout).isVisible = show
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                move(LoginFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        this.logoutMenu = menu
        showLogout(false)
        return true
    }
}