package org.third.medicalapp.hospital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.third.medicalapp.MainActivity
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityHospitalListBinding
import org.third.medicalapp.hospital.adapter.HospitalAdapter
import org.third.medicalapp.hospital.model.HospitalList
import org.third.medicalapp.util.MyApplication
import retrofit2.Call
import retrofit2.Response

class HospitalListActivity : AppCompatActivity() {
    lateinit var binding: ActivityHospitalListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "병원 찾기"

        val hcode = intent.getStringExtra("hcode")
        val dong = intent.getStringExtra("dong")


        binding.tvDepart.text = hcode
        binding.tvDong.text = dong

        binding.btnDepSelect.setOnClickListener {
            val intent = Intent(this, DepartSelectActivity::class.java)
            startActivity(intent)

        }

        binding.btnLocalSelect.setOnClickListener {
            val intent = Intent(this, LocationSelectActivity::class.java)
            startActivity(intent)

        }

        binding.btnSearchHospital.setOnClickListener {
            val intent = Intent(this, NameSearchActivity::class.java)
            startActivity(intent)
        }

        if (MyApplication.checkAdmin()) {
            binding.addFab.visibility = View.VISIBLE
        }
        binding.addFab.setOnClickListener {
            startActivity(Intent(this, HospitalAddActivity::class.java))
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 저장 메뉴 아이템을 선택한 경우
        if (item.itemId == R.id.menu_main) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        val hCode = intent.getStringExtra("hcode")
        val Dong = intent.getStringExtra("dong")
        val hName = intent.getStringExtra("hname")

        val networkService = (applicationContext as MyApplication).hospitalServie
        // 전체리스트 호출
        if (hCode == null && Dong == null && hName == null) {
            val hospitalListCall = networkService.doGetHospitalList()
            hospitalListCall.enqueue(object : retrofit2.Callback<HospitalList> {
                override fun onResponse(
                    call: Call<HospitalList>,
                    response: Response<HospitalList>
                ) {
                    if (response.isSuccessful) {
                        binding.recyclerListView.layoutManager =
                            LinearLayoutManager(this@HospitalListActivity)
                        val hospital = response.body()?.hospitalList
                        val adapter = HospitalAdapter(this@HospitalListActivity, hospital)
                        binding.recyclerListView.adapter = adapter
                        binding.recyclerListView.addItemDecoration(
                            DividerItemDecoration(
                                this@HospitalListActivity,
                                LinearLayoutManager.VERTICAL
                            )
                        )
                    }

                }

                override fun onFailure(call: Call<HospitalList>, t: Throwable) {
                    call.cancel()
                }
            })
        }
        // 진료과 선택 리스트 호출
        else if (hCode != null) {
            val hcodeListCall = networkService.doGetHcode(hcode = hCode)
            hcodeListCall.enqueue(object : retrofit2.Callback<HospitalList> {
                override fun onResponse(
                    call: Call<HospitalList>,
                    response: Response<HospitalList>
                ) {
                    binding.recyclerListView.layoutManager =
                        LinearLayoutManager(this@HospitalListActivity)
                    val hcodeHospital = response.body()?.hospitalList
                    val adapter = HospitalAdapter(this@HospitalListActivity, hcodeHospital)
                    binding.recyclerListView.adapter = adapter
                    binding.recyclerListView.addItemDecoration(
                        DividerItemDecoration(
                            this@HospitalListActivity,
                            LinearLayoutManager.VERTICAL
                        )
                    )
                }

                override fun onFailure(call: Call<HospitalList>, t: Throwable) {
                    call.cancel()
                }
            })
        }

        // 지역검색 리스트 호출
        if (Dong != null) {
            val dongListCall = networkService.doGetDong(dong = Dong)
            dongListCall.enqueue(object : retrofit2.Callback<HospitalList> {
                override fun onResponse(
                    call: Call<HospitalList>,
                    response: Response<HospitalList>
                ) {
                    if (response.isSuccessful) {
                        binding.recyclerListView.layoutManager =
                            LinearLayoutManager(this@HospitalListActivity)
                        val dongHospital = response.body()?.hospitalList
                        val adapter = HospitalAdapter(this@HospitalListActivity, dongHospital)
                        binding.recyclerListView.adapter = adapter
                        binding.recyclerListView.addItemDecoration(
                            DividerItemDecoration(
                                this@HospitalListActivity,
                                LinearLayoutManager.VERTICAL
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<HospitalList>, t: Throwable) {
                    call.cancel()
                }
            })
        }
        // 이름검색 호출
        if (hName != null){
            val nameListCall = networkService.doGetHospitalName(name = hName)
            nameListCall.enqueue(object : retrofit2.Callback<HospitalList>{
                override fun onResponse(
                    call: Call<HospitalList>,
                    response: Response<HospitalList>
                ) {
                    if(response.isSuccessful){
                        binding.recyclerListView.layoutManager = LinearLayoutManager(this@HospitalListActivity)
                        val nameHospital = response.body()?.hospitalList
                        val adapter = HospitalAdapter(this@HospitalListActivity, nameHospital)
                        binding.recyclerListView.adapter = adapter
                        binding.recyclerListView.addItemDecoration(DividerItemDecoration(this@HospitalListActivity, LinearLayoutManager.VERTICAL))
                    }
                }

                override fun onFailure(call: Call<HospitalList>, t: Throwable) {
                    call.cancel()
                }

            })
        }
        // 통합검색
        if (hName != null && Dong !=null && hCode != null){
            val searchListCall = networkService.doSearch(hcode = hCode, hname = hName, dong = Dong)
            searchListCall.enqueue(object :retrofit2.Callback<HospitalList>{
                override fun onResponse(
                    call: Call<HospitalList>,
                    response: Response<HospitalList>
                ) {
                    if(response.isSuccessful){
                        binding.recyclerListView.layoutManager = LinearLayoutManager(this@HospitalListActivity)
                        val nameAndDong = response.body()?.hospitalList
                        val adapter = HospitalAdapter(this@HospitalListActivity, nameAndDong)
                        binding.recyclerListView.adapter = adapter
                        binding.recyclerListView.addItemDecoration(DividerItemDecoration(this@HospitalListActivity, LinearLayoutManager.VERTICAL))
                    }
                }

                override fun onFailure(call: Call<HospitalList>, t: Throwable) {
                    call.cancel()
                }

            })
        }
    }

}