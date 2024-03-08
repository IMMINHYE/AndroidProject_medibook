package org.third.medicalapp.hospital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.third.medicalapp.databinding.ActivityHospitalSearchBinding

class HospitalSearchActivity : AppCompatActivity() {
    lateinit var binding: ActivityHospitalSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, HospitalListActivity::class.java)

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val hcode = intent.getStringExtra("hcode")
            binding.tvDepart.text = hcode
            Log.d("hcode", "$hcode")


        }
        binding.btnCloseX.setOnClickListener {
            startActivity(intent)
            finish()
        }

        binding.btnDepartSearch.setOnClickListener {
            val intent = Intent(this, DepartSearchActivity::class.java)
            requestLauncher.launch(intent)
        }

        binding.btnSearch.setOnClickListener {
            val hname = binding.tvHname.text.toString()
            val dong = binding.tvDong.text.toString()
            val hcode = binding.tvDepart.text.toString()
            intent.putExtra("hname", hname)
            intent.putExtra("dong", dong)
            intent.putExtra("hcode", hcode)
            startActivity(intent)
            finish()
        }
    }
}