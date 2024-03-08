package org.third.medicalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import org.third.medicalapp.community.CommunityActivity
import org.third.medicalapp.databinding.ActivityMainBinding
import org.third.medicalapp.medicalInfo.MedicalInfoActivity
import org.third.medicalapp.hospital.HospitalListActivity
import org.third.medicalapp.pharmacy.PharmacyListActivity
import org.third.medicalapp.sign.LoginActivity
import org.third.medicalapp.admin.UserListActivity
import org.third.medicalapp.user.UserMainActivity
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.myCheckPermission

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myCheckPermission(this)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawer

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.drawer_open, R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.imageView2.setOnClickListener {
            var intent = Intent(this, HospitalListActivity::class.java)
            startActivity(intent)
        }

        binding.imageView7.setOnClickListener {
            var intent = Intent(this, PharmacyListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        navView = binding.navView
        if (MyApplication.checkAuth()) {
            navView.menu.findItem(R.id.nav_login).setVisible(false)
            navView.menu.findItem(R.id.nav_my_page).setVisible(true)
            if (MyApplication.checkAdmin()) {
                navView.menu.findItem(R.id.nav_admin).setVisible(true)
                navView.menu.findItem(R.id.nav_my_page).setVisible(false)
            }
        } else {
            navView.menu.findItem(R.id.nav_login).setVisible(true)
            navView.menu.findItem(R.id.nav_admin).setVisible(false)
            navView.menu.findItem(R.id.nav_my_page).setVisible(false)
        }
        navView.setNavigationItemSelectedListener { menuItem ->
            // 클릭된 아이템에 따라 동작 처리
            when (menuItem.itemId) {
                R.id.nav_login -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                R.id.nav_my_page -> {
                    val intent = Intent(this, UserMainActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                R.id.nav_admin -> {
                    val intent = Intent(this, UserListActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                R.id.nav_hospital -> {
                    val intent = Intent(this, HospitalListActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                R.id.nav_pharmacy -> {
                    val intent = Intent(this, PharmacyListActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                R.id.nav_community -> {
                    val intent = Intent(this, CommunityActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                R.id.nav_medical_info -> {
                    val intent = Intent(this, MedicalInfoActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}