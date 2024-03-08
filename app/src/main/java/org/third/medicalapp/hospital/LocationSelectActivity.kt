package org.third.medicalapp.hospital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.third.medicalapp.databinding.ActivityLocationSelectBinding

class LocationSelectActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationSelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, HospitalListActivity::class.java)
        val hcode = intent.getStringExtra("hcode")
        Log.d("진료과 데이터", "$hcode")

        binding.btnCloseX.setOnClickListener {
            startActivity(intent)
            finish()
        }

        binding.btnDongSearch.setOnClickListener {
            val dong = binding.tvDong.text.toString()
            intent.putExtra("dong", dong)
            intent.putExtra("hcode", hcode)
            startActivity(intent)
            finish()
        }

    }
}