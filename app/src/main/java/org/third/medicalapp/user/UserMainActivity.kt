package org.third.medicalapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.third.medicalapp.R
import org.third.medicalapp.community.CommunityActivity
import org.third.medicalapp.databinding.ActivityUserMainBinding
import org.third.medicalapp.hospital.HospitalListActivity
import org.third.medicalapp.medicalInfo.MedicalInfoActivity
import org.third.medicalapp.pharmacy.PharmacyListActivity
import org.third.medicalapp.util.MyApplication


class UserMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserMainBinding
    lateinit var navController: NavController
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title="My Page"
        //네비게이션 및 drawer 설정
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val drawerLayout: DrawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.drawer_open, R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navView = binding.navView
        navView.menu.clear()
        navView.inflateMenu(R.menu.activity_main_drawer)
        if (MyApplication.checkAdmin()) {
            navView.menu.findItem(R.id.nav_login).setVisible(false)
            navView.menu.findItem(R.id.nav_admin).setVisible(true)
            navView.menu.findItem(R.id.nav_my_page).setVisible(false)
        } else {
            navView.menu.findItem(R.id.nav_login).setVisible(false)
            navView.menu.findItem(R.id.nav_admin).setVisible(false)
            navView.menu.findItem(R.id.nav_my_page).setVisible(true)
        }
        navView.setNavigationItemSelectedListener { menuItem ->
            // 클릭된 아이템에 따라 동작 처리
            when (menuItem.itemId) {
                R.id.nav_my_page -> {
                    navController.navigate(R.id.nav_my_page)
                    drawerLayout.closeDrawers()
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
